package com.soliton.courier.exception

import org.springframework.http.HttpStatus

data class ApiException(override val message: String?, val status: HttpStatus) : RuntimeException(message)