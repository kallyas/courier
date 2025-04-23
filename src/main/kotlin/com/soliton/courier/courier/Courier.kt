package com.soliton.courier.courier

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "couriers")
data class Courier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var phone: String,

    @Column(nullable = false)
    var vehicle: String,

    @Column(nullable = true)
    var email: String? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()

)
