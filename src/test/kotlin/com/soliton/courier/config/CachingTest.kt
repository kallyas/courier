package com.soliton.courier.config

import com.soliton.courier.courier.Courier
import com.soliton.courier.courier.CourierDto
import com.soliton.courier.courier.CourierRepository
import com.soliton.courier.courier.CourierService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class CachingTest {

    @Autowired
    private lateinit var courierService: CourierService

    @MockBean
    private lateinit var courierRepository: CourierRepository

    @Test
    fun `getCourierById should cache results`() {
        // Given
        val courier = Courier(
            id = 1L,
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        `when`(courierRepository.findById(1L)).thenReturn(Optional.of(courier))

        // When
        // Call the service method twice with the same ID
        courierService.getCourierById(1L)
        courierService.getCourierById(1L)

        // Then
        // Repository should be called only once due to caching
        verify(courierRepository, times(1)).findById(1L)
    }

    @Test
    fun `getAllCouriers should cache results`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val courier = Courier(
            id = 1L,
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val courierList = listOf(courier)
        val page = PageImpl(courierList, pageable, courierList.size.toLong())
        `when`(courierRepository.findAll(pageable)).thenReturn(page)

        // When
        // Call the service method twice with the same pageable
        courierService.getAllCouriers(pageable)
        courierService.getAllCouriers(pageable)

        // Then
        // Repository should be called only once due to caching
        verify(courierRepository, times(1)).findAll(pageable)
    }

    @Test
    fun `createCourier should evict cache`() {
        // Given
        val courier = Courier(
            id = 1L,
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val courierDto = CourierDto(
            id = null,
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com"
        )
        val pageable = PageRequest.of(0, 10)
        val courierList = listOf(courier)
        val page = PageImpl(courierList, pageable, courierList.size.toLong())
        
        `when`(courierRepository.findById(1L)).thenReturn(Optional.of(courier))
        `when`(courierRepository.findAll(pageable)).thenReturn(page)
        `when`(courierRepository.save(any(Courier::class.java))).thenReturn(courier)

        // When
        // First, populate the cache
        courierService.getCourierById(1L)
        courierService.getAllCouriers(pageable)
        
        // Then create a new courier, which should evict the cache
        courierService.createCourier(courierDto)
        
        // Then call the methods again
        courierService.getCourierById(1L)
        courierService.getAllCouriers(pageable)

        // Then
        // Repository should be called twice for each method (once before and once after cache eviction)
        verify(courierRepository, times(2)).findById(1L)
        verify(courierRepository, times(2)).findAll(pageable)
    }
}