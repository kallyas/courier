package com.soliton.courier

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CourierApplication

fun main(args: Array<String>) {
    runApplication<CourierApplication>(*args)
}
