package com.soliton.courier.validation

import jakarta.validation.ConstraintValidatorContext
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class VehicleTypeValidatorTest {

    private lateinit var validator: VehicleTypeValidator

    @Mock
    private lateinit var context: ConstraintValidatorContext

    @BeforeEach
    fun setUp() {
        validator = VehicleTypeValidator()
    }

    @Test
    fun `should return true for valid vehicle types`() {
        // Test all allowed vehicle types
        assertTrue(validator.isValid("CAR", context))
        assertTrue(validator.isValid("MOTORCYCLE", context))
        assertTrue(validator.isValid("BICYCLE", context))
        assertTrue(validator.isValid("VAN", context))
        assertTrue(validator.isValid("TRUCK", context))
        
        // Test case insensitivity
        assertTrue(validator.isValid("car", context))
        assertTrue(validator.isValid("Motorcycle", context))
        assertTrue(validator.isValid("bicycle", context))
        assertTrue(validator.isValid("Van", context))
        assertTrue(validator.isValid("truck", context))
    }

    @Test
    fun `should return false for invalid vehicle types`() {
        assertFalse(validator.isValid("INVALID_TYPE", context))
        assertFalse(validator.isValid("SCOOTER", context))
        assertFalse(validator.isValid("BUS", context))
        assertFalse(validator.isValid("", context))
    }

    @Test
    fun `should return true for null value`() {
        // Null values should be handled by @NotBlank
        assertTrue(validator.isValid(null, context))
    }
}