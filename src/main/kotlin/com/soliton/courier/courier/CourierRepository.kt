package com.soliton.courier.courier

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourierRepository : JpaRepository<Courier, Long>