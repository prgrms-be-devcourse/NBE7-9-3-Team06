package com.backend.petplace.global.jwt

import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)

        if (token != null) {
            try {
                jwtTokenProvider.validateToken(token)

                val authentication: Authentication = jwtTokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication

            } catch (ex: BusinessException) {
                response.characterEncoding = "UTF-8"
                response.status = ex.errorCode.status.value()
                val apiResponse: ApiResponse<Void?> = ApiResponse.error(ex.errorCode)
                response.writer.write(ObjectMapper().writeValueAsString(apiResponse))
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            logger.info("Bearer Token: $bearerToken")
            bearerToken.substring(7)
        } else {
            null
        }
    }
}