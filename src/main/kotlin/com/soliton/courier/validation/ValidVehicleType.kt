package com.soliton.courier.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [VehicleTypeValidator::class])
annotation class ValidVehicleType(
    val message: String = "Invalid vehicle type. Allowed types are: CAR, MOTORCYCLE, BICYCLE, VAN, TRUCK",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class VehicleTypeValidator : ConstraintValidator<ValidVehicleType, String> {
    
    companion object {
        private val ALLOWED_VEHICLE_TYPES = setOf("CAR", "MOTORCYCLE", "BICYCLE", "VAN", "TRUCK")
    }
    
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true // Let @NotBlank handle null validation
        }
        return ALLOWED_VEHICLE_TYPES.contains(value.uppercase())
    }
}