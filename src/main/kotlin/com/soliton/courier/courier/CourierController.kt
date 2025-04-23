package com.soliton.courier.courier

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/couriers")
@Tag(name = "Courier", description = "Courier management APIs")
class CourierController(private val courierService: CourierService) {

    @Operation(summary = "Get all couriers with pagination", description = "Returns a paginated list of all couriers")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved couriers"),
        ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    ])
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    fun getAllCouriers(
        @Parameter(description = "Pagination information")
        @PageableDefault(page = 0, size = 20) pageable: Pageable
    ): ResponseEntity<Page<CourierDto>> {
        return ResponseEntity.ok(courierService.getAllCouriers(pageable))
    }

    @Operation(summary = "Get courier by ID", description = "Returns a courier by ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved courier"),
        ApiResponse(responseCode = "404", description = "Courier not found"),
        ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    ])
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    fun getCourierById(
        @Parameter(description = "Courier ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<CourierDto> {
        return ResponseEntity.ok(courierService.getCourierById(id))
    }

    @Operation(summary = "Create a new courier", description = "Creates a new courier and returns the created courier")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Courier successfully created"),
        ApiResponse(responseCode = "400", description = "Invalid input data"),
        ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    ])
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createCourier(
        @Parameter(description = "Courier data", required = true)
        @Valid @RequestBody courierDto: CourierDto
    ): ResponseEntity<CourierDto> {
        val createdCourier = courierService.createCourier(courierDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourier)
    }

    @Operation(summary = "Update an existing courier", description = "Updates an existing courier and returns the updated courier")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Courier successfully updated"),
        ApiResponse(responseCode = "400", description = "Invalid input data"),
        ApiResponse(responseCode = "404", description = "Courier not found"),
        ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    ])
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateCourier(
        @Parameter(description = "Courier ID", required = true)
        @PathVariable id: Long, 
        @Parameter(description = "Updated courier data", required = true)
        @Valid @RequestBody courierDto: CourierDto
    ): ResponseEntity<CourierDto> {
        return ResponseEntity.ok(courierService.updateCourier(id, courierDto))
    }

    @Operation(summary = "Delete a courier", description = "Deletes a courier by ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Courier successfully deleted"),
        ApiResponse(responseCode = "404", description = "Courier not found"),
        ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    ])
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteCourier(
        @Parameter(description = "Courier ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        courierService.deleteCourier(id)
        return ResponseEntity.noContent().build()
    }
}
