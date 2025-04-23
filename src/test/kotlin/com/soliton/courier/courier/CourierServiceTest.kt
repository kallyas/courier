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
            phone = "1234567890",
            vehicle = "Motorcycle",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        courierDto = CourierDto(
            id = 1L,
            name = "John Doe",
            phone = "1234567890",
            vehicle = "Motorcycle"
        )
    }

    @Test
    fun `getAllCouriers should return list of courier DTOs`() {
        // Given
        val courierList = listOf(courier)
        `when`(courierRepository.findAll()).thenReturn(courierList)

        // When
        val result = courierService.getAllCouriers()

        // Then
        assertEquals(1, result.size)
        assertEquals(courierDto.id, result[0].id)
        assertEquals(courierDto.name, result[0].name)
        assertEquals(courierDto.phone, result[0].phone)
        assertEquals(courierDto.vehicle, result[0].vehicle)
        verify(courierRepository, times(1)).findAll()
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
            phone = "1234567890",
            vehicle = "Motorcycle"
        )
        val savedCourier = Courier(
            id = 1L,
            name = "John Doe",
            phone = "1234567890",
            vehicle = "Motorcycle",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val inputDto = CourierDto(
            id = null,
            name = "John Doe",
            phone = "1234567890",
            vehicle = "Motorcycle"
        )

        `when`(courierRepository.save(any(Courier::class.java))).thenReturn(savedCourier)

        // When
        val result = courierService.createCourier(inputDto)

        // Then
        assertEquals(courierDto.id, result.id)
        assertEquals(courierDto.name, result.name)
        assertEquals(courierDto.phone, result.phone)
        assertEquals(courierDto.vehicle, result.vehicle)
        verify(courierRepository, times(1)).save(any(Courier::class.java))
    }

    @Test
    fun `updateCourier should return updated courier DTO when courier exists`() {
        // Given
        val updatedCourier = Courier(
            id = 1L,
            name = "Jane Doe",
            phone = "0987654321",
            vehicle = "Car",
            createdAt = courier.createdAt,
            updatedAt = LocalDateTime.now()
        )
        val updateDto = CourierDto(
            id = 1L,
            name = "Jane Doe",
            phone = "0987654321",
            vehicle = "Car"
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
        verify(courierRepository, times(1)).findById(1L)
        verify(courierRepository, times(1)).save(any(Courier::class.java))
    }

    @Test
    fun `updateCourier should throw ApiException when courier does not exist`() {
        // Given
        val updateDto = CourierDto(
            id = 1L,
            name = "Jane Doe",
            phone = "0987654321",
            vehicle = "Car"
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
}