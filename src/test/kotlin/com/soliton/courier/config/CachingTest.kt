package com.soliton.courier.config

import com.soliton.courier.config.CacheConfig
import com.soliton.courier.courier.Courier
import com.soliton.courier.courier.CourierDto
import com.soliton.courier.courier.CourierRepository
import com.soliton.courier.courier.CourierService
import org.junit.jupiter.api.Assertions.assertEquals
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

        // Configure mock to return the page when findAll is called with the exact pageable instance
        `when`(courierRepository.findAll(eq(pageable))).thenReturn(page)

        // When
        // Call the service method twice with the same pageable
        val result1 = courierService.getAllCouriers(pageable)
        val result2 = courierService.getAllCouriers(pageable)

        // Then
        // Verify results are as expected
        assertEquals(1, result1.content.size)
        assertEquals(1, result2.content.size)
        assertEquals(courier.id, result1.content[0].id)

        // Repository should be called only once due to caching
        verify(courierRepository, times(1)).findAll(eq(pageable))
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

        // Configure mocks with exact matchers
        `when`(courierRepository.findById(eq(1L))).thenReturn(Optional.of(courier))
        `when`(courierRepository.findAll(eq(pageable))).thenReturn(page)
        `when`(courierRepository.save(any(Courier::class.java))).thenReturn(courier)

        // When
        // First, populate the cache
        val result1 = courierService.getCourierById(1L)
        val result2 = courierService.getAllCouriers(pageable)

        // Verify initial results
        assertEquals(courier.id, result1.id)
        assertEquals(1, result2.content.size)

        // Then create a new courier, which should evict the cache
        val createdCourier = courierService.createCourier(courierDto)
        assertEquals(courier.id, createdCourier.id)

        // Then call the methods again
        val result3 = courierService.getCourierById(1L)
        val result4 = courierService.getAllCouriers(pageable)

        // Verify results after cache eviction
        assertEquals(courier.id, result3.id)
        assertEquals(1, result4.content.size)

        // Then
        // Repository should be called twice for each method (once before and once after cache eviction)
        verify(courierRepository, times(2)).findById(eq(1L))
        verify(courierRepository, times(2)).findAll(eq(pageable))
    }
}
