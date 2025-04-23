package com.soliton.courier.courier

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import com.soliton.courier.exception.ApiException
import org.slf4j.LoggerFactory

@Service
class CourierService(private val courierRepository: CourierRepository) {

    private val logger = LoggerFactory.getLogger(CourierService::class.java)

    @Transactional(readOnly = true)
    fun getAllCouriers(): List<CourierDto> {
        logger.info("Fetching all couriers")
        val couriers = courierRepository.findAll().map { it.toDto() }
        logger.info("Found {} couriers", couriers.size)
        return couriers
    }

    @Transactional(readOnly = true)
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
    fun createCourier(courierDto: CourierDto): CourierDto {
        logger.info("Creating new courier with name: {}", courierDto.name)
        val courier = Courier(name = courierDto.name, phone = courierDto.phone, vehicle = courierDto.vehicle)
        val savedCourier = courierRepository.save(courier)
        logger.info("Created courier with id: {}", savedCourier.id)
        return savedCourier.toDto()
    }

    @Transactional
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
        existingCourier.updatedAt = LocalDateTime.now()

        val updatedCourier = courierRepository.save(existingCourier)
        logger.info("Updated courier with id: {}", updatedCourier.id)
        return updatedCourier.toDto()
    }

    @Transactional
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
        return CourierDto(id = this.id, name = this.name, phone = this.phone, vehicle = this.vehicle)
    }
}
