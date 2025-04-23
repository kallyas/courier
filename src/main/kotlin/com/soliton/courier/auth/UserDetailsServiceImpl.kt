package com.soliton.courier.auth

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : UserDetailsService {

    // **In a real application, this data would come from a database.**
    private val users = mutableMapOf(
        "user" to User("user", BCryptPasswordEncoder().encode("password"), listOf(SimpleGrantedAuthority("ROLE_USER"))),
        "admin" to User(
            "admin",
            BCryptPasswordEncoder().encode("password"),
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )
    )

    override fun loadUserByUsername(username: String): UserDetails {
        return users[username] ?: throw UsernameNotFoundException("User not found with username: $username")
    }
}