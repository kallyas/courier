package com.soliton.courier.courier

import com.soliton.courier.exception.ApiException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class CourierServiceTest {

    @Mock
    private lateinit var courierRepository: CourierRepository

    @InjectMocks
    private lateinit var courierService: CourierService

    private lateinit var courier: Courier
    private lateinit var courierDto: CourierDto

    @BeforeEach
    fun setUp() {
        courier = Courier(
            id = 1L,
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        courierDto = CourierDto(
            id = 1L,
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com"
        )
    }

    @Test
    fun `getAllCouriers should return page of courier DTOs`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val courierList = listOf(courier)
        val courierPage = PageImpl(courierList, pageable, courierList.size.toLong())
        `when`(courierRepository.findAll(pageable)).thenReturn(courierPage)

        // When
        val result = courierService.getAllCouriers(pageable)

        // Then
        assertEquals(1, result.content.size)
        assertEquals(courierDto.id, result.content[0].id)
        assertEquals(courierDto.name, result.content[0].name)
        assertEquals(courierDto.phone, result.content[0].phone)
        assertEquals(courierDto.vehicle, result.content[0].vehicle)
        assertEquals(courierDto.email, result.content[0].email)
        verify(courierRepository, times(1)).findAll(pageable)
    }

    @Test
    fun `getCourierById should return courier DTO when courier exists`() {
        // Given
        `when`(courierRepository.findById(1L)).thenReturn(Optional.of(courier))

        // When
        val result = courierService.getCourierById(1L)

        // Then
        assertEquals(courierDto.id, result.id)
        assertEquals(courierDto.name, result.name)
        assertEquals(courierDto.phone, result.phone)
        assertEquals(courierDto.vehicle, result.vehicle)
        assertEquals(courierDto.email, result.email)
        verify(courierRepository, times(1)).findById(1L)
    }

    @Test
    fun `getCourierById should throw ApiException when courier does not exist`() {
        // Given
        `when`(courierRepository.findById(1L)).thenReturn(Optional.empty())

        // When/Then
        val exception = assertThrows(ApiException::class.java) {
            courierService.getCourierById(1L)
        }
        assertEquals("Courier not found with id 1", exception.message)
        assertEquals(HttpStatus.NOT_FOUND, exception.status)
        verify(courierRepository, times(1)).findById(1L)
    }

    @Test
    fun `createCourier should return created courier DTO`() {
        // Given
        val newCourier = Courier(
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com"
        )
        val savedCourier = Courier(
            id = 1L,
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val inputDto = CourierDto(
            id = null,
            name = "John Doe",
            phone = "+1234567890",
            vehicle = "CAR",
            email = "john.doe@example.com"
        )

        `when`(courierRepository.save(any(Courier::class.java))).thenReturn(savedCourier)

        // When
        val result = courierService.createCourier(inputDto)

        // Then
        assertEquals(courierDto.id, result.id)
        assertEquals(courierDto.name, result.name)
        assertEquals(courierDto.phone, result.phone)
        assertEquals(courierDto.vehicle, result.vehicle)
        assertEquals(courierDto.email, result.email)
        verify(courierRepository, times(1)).save(any(Courier::class.java))
    }

    @Test
    fun `updateCourier should return updated courier DTO when courier exists`() {
        // Given
        val updatedCourier = Courier(
            id = 1L,
            name = "Jane Doe",
            phone = "+0987654321",
            vehicle = "VAN",
            email = "jane.doe@example.com",
            createdAt = courier.createdAt,
            updatedAt = LocalDateTime.now()
        )
        val updateDto = CourierDto(
            id = 1L,
            name = "Jane Doe",
            phone = "+0987654321",
            vehicle = "VAN",
            email = "jane.doe@example.com"
        )

        `when`(courierRepository.findById(1L)).thenReturn(Optional.of(courier))
        `when`(courierRepository.save(any(Courier::class.java))).thenReturn(updatedCourier)

        // When
        val result = courierService.updateCourier(1L, updateDto)

        // Then
        assertEquals(updateDto.id, result.id)
        assertEquals(updateDto.name, result.name)
        assertEquals(updateDto.phone, result.phone)
        assertEquals(updateDto.vehicle, result.vehicle)
        assertEquals(updateDto.email, result.email)
        verify(courierRepository, times(1)).findById(1L)
        verify(courierRepository, times(1)).save(any(Courier::class.java))
    }

    @Test
    fun `updateCourier should throw ApiException when courier does not exist`() {
        // Given
        val updateDto = CourierDto(
            id = 1L,
            name = "Jane Doe",
            phone = "+0987654321",
            vehicle = "VAN",
            email = "jane.doe@example.com"
        )
        `when`(courierRepository.findById(1L)).thenReturn(Optional.empty())

        // When/Then
        val exception = assertThrows(ApiException::class.java) {
            courierService.updateCourier(1L, updateDto)
        }
        assertEquals("Courier not found with id 1", exception.message)
        assertEquals(HttpStatus.NOT_FOUND, exception.status)
        verify(courierRepository, times(1)).findById(1L)
        verify(courierRepository, never()).save(any(Courier::class.java))
    }

    @Test
    fun `deleteCourier should delete courier when courier exists`() {
        // Given
        `when`(courierRepository.existsById(1L)).thenReturn(true)
        doNothing().`when`(courierRepository).deleteById(1L)

        // When
        courierService.deleteCourier(1L)

        // Then
        verify(courierRepository, times(1)).existsById(1L)
        verify(courierRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `deleteCourier should throw ApiException when courier does not exist`() {
        // Given
        `when`(courierRepository.existsById(1L)).thenReturn(false)

        // When/Then
        val exception = assertThrows(ApiException::class.java) {
            courierService.deleteCourier(1L)
        }
        assertEquals("Courier not found with id 1", exception.message)
        assertEquals(HttpStatus.NOT_FOUND, exception.status)
        verify(courierRepository, times(1)).existsById(1L)
        verify(courierRepository, never()).deleteById(any())
    }

    @Test
    fun `getAllCouriers should handle pagination correctly`() {
        // Given
        val courier1 = courier.copy(id = 1L, name = "Courier 1")
        val courier2 = courier.copy(id = 2L, name = "Courier 2")
        val courier3 = courier.copy(id = 3L, name = "Courier 3")
        val courierList = listOf(courier1, courier2, courier3)

        // Test first page with size 2
        val pageable1 = PageRequest.of(0, 2)
        val page1 = PageImpl(courierList.subList(0, 2), pageable1, courierList.size.toLong())
        `when`(courierRepository.findAll(pageable1)).thenReturn(page1)

        // Test second page with size 2
        val pageable2 = PageRequest.of(1, 2)
        val page2 = PageImpl(courierList.subList(2, 3), pageable2, courierList.size.toLong())
        `when`(courierRepository.findAll(pageable2)).thenReturn(page2)

        // When & Then - First page
        val result1 = courierService.getAllCouriers(pageable1)
        assertEquals(2, result1.content.size)
        assertEquals(3, result1.totalElements)
        assertEquals(2, result1.numberOfElements)
        assertEquals(0, result1.number)
        assertEquals(2, result1.size)
        assertEquals(2, result1.totalPages)
        assertEquals("Courier 1", result1.content[0].name)
        assertEquals("Courier 2", result1.content[1].name)

        // When & Then - Second page
        val result2 = courierService.getAllCouriers(pageable2)
        assertEquals(1, result2.content.size)
        assertEquals(3, result2.totalElements)
        assertEquals(1, result2.numberOfElements)
        assertEquals(1, result2.number)
        assertEquals(2, result2.size)
        assertEquals(2, result2.totalPages)
        assertEquals("Courier 3", result2.content[0].name)
    }
}
