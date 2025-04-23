package com.soliton.courier.courier

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/couriers")
class CourierController(private val courierService: CourierService) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    fun getAllCouriers(): ResponseEntity<List<CourierDto>> {
        return ResponseEntity.ok(courierService.getAllCouriers())
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    fun getCourierById(@PathVariable id: Long): ResponseEntity<CourierDto> {
        return ResponseEntity.ok(courierService.getCourierById(id))
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createCourier(@Valid @RequestBody courierDto: CourierDto): ResponseEntity<CourierDto> {
        val createdCourier = courierService.createCourier(courierDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourier)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateCourier(@PathVariable id: Long, @Valid @RequestBody courierDto: CourierDto): ResponseEntity<CourierDto> {
        return ResponseEntity.ok(courierService.updateCourier(id, courierDto))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteCourier(@PathVariable id: Long): ResponseEntity<Void> {
        courierService.deleteCourier(id)
        return ResponseEntity.noContent().build()
    }
}
