package com.soliton.courier.courier

import com.soliton.courier.validation.ValidVehicleType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CourierDto(
    val id: Long?,

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(
        regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", 
        message = "Phone number must be in international format (e.g., +1 123 456 7890)"
    )
    val phone: String,

    @field:NotBlank(message = "Vehicle type is required")
    @field:ValidVehicleType
    val vehicle: String,

    @field:Email(message = "Email must be valid")
    val email: String? = null
)
