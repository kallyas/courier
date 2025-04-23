package com.soliton.courier.courier

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import com.soliton.courier.exception.ApiException
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching

@Service
class CourierService(private val courierRepository: CourierRepository) {

    private val logger = LoggerFactory.getLogger(CourierService::class.java)

    @Transactional(readOnly = true)
    @Cacheable(value = ["couriers"], key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize", unless = "#result.content.size() > 100")
    fun getAllCouriers(pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<CourierDto> {
        logger.info("Fetching couriers with pagination: page={}, size={}", pageable.pageNumber, pageable.pageSize)
        val courierPage = courierRepository.findAll(pageable)
        val courierDtoPage = courierPage.map { it.toDto() }
        logger.info("Found {} couriers", courierDtoPage.content.size)
        return courierDtoPage
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["courierById"], key = "#id")
    fun getCourierById(id: Long): CourierDto {
        logger.info("Fetching courier with id: {}", id)
        val courier = courierRepository.findById(id)
            .orElseThrow { 
                logger.error("Courier not found with id: {}", id)
                ApiException("Courier not found with id $id", HttpStatus.NOT_FOUND) 
            }
        logger.info("Found courier: {}", courier.id)
        return courier.toDto()
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["couriers"], allEntries = true),
        CacheEvict(value = ["courierById"], allEntries = true)
    ])
    fun createCourier(courierDto: CourierDto): CourierDto {
        logger.info("Creating new courier with name: {}", courierDto.name)
        val courier = Courier(
            name = courierDto.name, 
            phone = courierDto.phone, 
            vehicle = courierDto.vehicle,
            email = courierDto.email
        )
        val savedCourier = courierRepository.save(courier)
        logger.info("Created courier with id: {}", savedCourier.id)
        return savedCourier.toDto()
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["couriers"], allEntries = true),
        CacheEvict(value = ["courierById"], key = "#id")
    ])
    fun updateCourier(id: Long, courierDto: CourierDto): CourierDto {
        logger.info("Updating courier with id: {}", id)
        val existingCourier = courierRepository.findById(id)
            .orElseThrow { 
                logger.error("Courier not found with id: {}", id)
                ApiException("Courier not found with id $id", HttpStatus.NOT_FOUND) 
            }

        existingCourier.name = courierDto.name
        existingCourier.phone = courierDto.phone
        existingCourier.vehicle = courierDto.vehicle
        existingCourier.email = courierDto.email
        existingCourier.updatedAt = LocalDateTime.now()

        val updatedCourier = courierRepository.save(existingCourier)
        logger.info("Updated courier with id: {}", updatedCourier.id)
        return updatedCourier.toDto()
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["couriers"], allEntries = true),
        CacheEvict(value = ["courierById"], key = "#id")
    ])
    fun deleteCourier(id: Long) {
        logger.info("Deleting courier with id: {}", id)
        if (!courierRepository.existsById(id)) {
            logger.error("Courier not found with id: {}", id)
            throw ApiException("Courier not found with id $id", HttpStatus.NOT_FOUND)
        }
        courierRepository.deleteById(id)
        logger.info("Deleted courier with id: {}", id)
    }

    private fun Courier.toDto(): CourierDto {
        return CourierDto(
            id = this.id, 
            name = this.name, 
            phone = this.phone, 
            vehicle = this.vehicle,
            email = this.email
        )
    }
}
