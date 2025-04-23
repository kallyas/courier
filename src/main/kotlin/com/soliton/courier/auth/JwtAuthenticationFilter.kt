package com.soliton.courier.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }

            val jwtToken = authHeader.substring(7)  // Remove "Bearer " prefix
            val username = jwtTokenProvider.extractUsername(jwtToken)

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                try {
                    val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)

                    if (jwtTokenProvider.isTokenValid(jwtToken, userDetails)) {
                        val authenticationToken = UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.authorities
                        )
                        authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authenticationToken
                    }
                } catch (ex: Exception) {
                    logger.error("Failed to authenticate user: ${ex.message}")
                }
            }
        } catch (ex: Exception) {
            logger.error("JWT authentication error: ${ex.message}")
        }

        filterChain.doFilter(request, response)
    }
}
