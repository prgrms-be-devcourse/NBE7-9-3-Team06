package com.backend.petplace.global.jwt

import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val userDetailsService: CustomUserDetailsService,
) {
    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    @Value("\${jwt.access-expiration-ms}")
    private var accessTokenExpirationMilliseconds: Long = 0

    private lateinit var key: SecretKey

    @PostConstruct
    fun init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    fun generateAccessToken(userId: Long): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenExpirationMilliseconds)

        val claims: MutableMap<String, Any> = HashMap()
        claims["userId"] = userId

        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
        } catch (e: ExpiredJwtException) {
            throw BusinessException(ErrorCode.EXPIRED_TOKEN)
        } catch (e: UnsupportedJwtException) {
            throw BusinessException(ErrorCode.UNSUPPORTED_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw BusinessException(ErrorCode.EMPTY_TOKEN)
        } catch (e: JwtException) {
            throw BusinessException(ErrorCode.INVALID_TOKEN)
        }
    }

    fun getAuthentication(token: String): Authentication {
        val userId = getUserId(token)
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(userId)
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    private fun getUserId(token: String): String {
        val claims: Claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        return claims["userId"].toString()
    }
}