package com.soliton.courier.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitingFilter : OncePerRequestFilter() {

    private val buckets = ConcurrentHashMap<String, Bucket>()

    // Define rate limits for different endpoints
    private val authRateLimit = Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(1)))
    private val defaultRateLimit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)))

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Get client IP address
        val clientIp = request.remoteAddr
        val requestURI = request.requestURI

        // Create or get the rate limiter bucket for this client
        val bucket = buckets.computeIfAbsent(clientIp) { createBucket(requestURI) }

        // Try to consume a token
        val probe = bucket.tryConsumeAndReturnRemaining(1)
        if (probe.isConsumed) {
            // Add rate limit headers
            response.addHeader("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
            filterChain.doFilter(request, response)
        } else {
            // Rate limit exceeded
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.contentType = "application/json"
            response.writer.write("""{"error":"Rate limit exceeded. Try again later."}""")
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", 
                (probe.nanosToWaitForRefill / 1_000_000_000).toString())
        }
    }

    private fun createBucket(requestURI: String): Bucket {
        val limit = when {
            requestURI.contains("/api/auth") -> authRateLimit
            else -> defaultRateLimit
        }
        return Bucket.builder().addLimit(limit).build()
    }
}