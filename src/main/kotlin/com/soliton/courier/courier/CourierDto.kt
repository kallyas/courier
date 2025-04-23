package com.soliton.courier.courier

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CourierDto(
    val id: Long?,

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits, may start with +)")
    val phone: String,

    @field:NotBlank(message = "Vehicle type is required")
    @field:Size(min = 2, max = 50, message = "Vehicle type must be between 2 and 50 characters")
    val vehicle: String
)
