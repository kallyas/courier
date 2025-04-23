package com.soliton.courier.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.function.Function

@Component
class JwtTokenProvider {

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    @Value("\${spring.security.jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${spring.security.jwt.expiration}")
    private val jwtExpirationMs: Long = 3600000 // 1 hour

    fun extractUsername(token: String): String? {
        return try {
            extractClaim(token, Claims::getSubject)
        } catch (e: Exception) {
            logger.error("Error extracting username from token: ${e.message}", e)
            null
        }
    }

    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap(), userDetails)
    }

    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        try {
            val username = extractUsername(token)
            return (username == userDetails.username) && !isTokenExpired(token)
        } catch (e: Exception) {
            logger.error("Error validating token: ${e.message}", e)
            return false
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        try {
            return extractExpiration(token).before(Date())
        } catch (e: Exception) {
            logger.error("Error checking token expiration: ${e.message}", e)
            return true  // Consider expired if there's an error
        }
    }

    private fun extractExpiration(token: String): Date {
        try {
            return extractClaim(token, Claims::getExpiration)
        } catch (e: Exception) {
            logger.error("Error extracting expiration from token: ${e.message}", e)
            throw e
        }
    }

    private fun extractAllClaims(token: String): Claims {
        try {
            return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            // Log the exception for debugging purposes
            logger.error("Error parsing JWT token: ${e.message}", e)
            throw e
        }
    }

    private fun getSignInKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

}
