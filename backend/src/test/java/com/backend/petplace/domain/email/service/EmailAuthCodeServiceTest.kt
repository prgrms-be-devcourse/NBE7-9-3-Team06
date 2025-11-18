package com.backend.petplace.domain.email.service

import com.backend.petplace.domain.email.dto.request.CheckAuthCodeRequest
import com.backend.petplace.domain.email.entity.EmailAuthCode
import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class EmailAuthCodeServiceTest {

    @Mock
    private lateinit var emailAuthCodeRepository: EmailAuthCodeRepository

    private lateinit var emailAuthCodeService: EmailAuthCodeService

    @BeforeEach
    fun setUp() {
        // EmailAuthCodeService는 생성자 파라미터가 필요하므로 직접 생성
        emailAuthCodeService = EmailAuthCodeService(
            javaMailSender = org.mockito.kotlin.mock(), // 필요 없는 경우 mock 처리
            emailAuthCodeRepository = emailAuthCodeRepository,
            senderEmail = "test@example.com",
            authCodeExpirationTime = 10L
        )
    }

    @Test
    fun `인증번호가 만료되었으면 예외를 던진다`() {
        // given
        val email = "test@example.com"
        val authCode = "ABC1234"

        val expiredCode = EmailAuthCode.create(email, authCode, -1) // 이미 만료됨
        val request = CheckAuthCodeRequest(email, authCode)

        whenever(emailAuthCodeRepository.findByEmailAndAuthCode(email, authCode))
            .thenReturn(expiredCode)

        // when & then
        val exception = assertThrows(BusinessException::class.java) {
            emailAuthCodeService.checkAuthCode(request)
        }

        assertEquals(ErrorCode.AUTH_CODE_EXPIRED, exception.errorCode)
    }
}