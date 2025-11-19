package com.backend.petplace.global.jwt

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockitoExtension::class)
class JwtTokenProviderTest {

    @Mock
    private lateinit var userDetailsService: CustomUserDetailsService

    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun setUp() {
        jwtTokenProvider = JwtTokenProvider(userDetailsService)

        val base64Key = Base64.getEncoder()
            .encodeToString("test-secret-key-12345678901234567890".toByteArray())
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", base64Key)
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationMilliseconds", 60000L)
        jwtTokenProvider.init()
    }

    @Test
    @DisplayName("JwtTokenProvider 통합 테스트: 암호화된 토큰 생성 & 파싱 확인")
    fun token_shouldBeGeneratedAndParsedCorrectly() {
        val token = jwtTokenProvider.generateAccessToken(123L)
        assertThat(token).isNotNull

        assertDoesNotThrow { jwtTokenProvider.validateToken(token) }

        val mockUser: UserDetails = User("123", "pw", listOf())
        `when`(userDetailsService.loadUserByUsername("123")).thenReturn(mockUser)

        val auth: Authentication = jwtTokenProvider.getAuthentication(token)
        assertThat(auth.name).isEqualTo("123")
    }
}