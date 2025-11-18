package com.backend.petplace.domain.email.service

import com.backend.petplace.domain.email.dto.request.CheckAuthCodeRequest
import com.backend.petplace.domain.email.entity.EmailAuthCode
import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class EmailAuthCodeServiceTest {
    @Mock
    private val emailAuthCodeRepository: EmailAuthCodeRepository? = null

    @InjectMocks
    private val emailAuthCodeService: EmailAuthCodeService? = null

    @Test
    fun 인증번호가_만료되었으면_예외를_던진다() {
        // given
        val email = "test@example.com"
        val authCode = "ABC1234"

        val expiredCode = EmailAuthCode.create(email, authCode, -1) // 이미 만료됨
        val request = CheckAuthCodeRequest(email, authCode)

        Mockito.`when`<EmailAuthCode?>(
            emailAuthCodeRepository!!.findByEmailAndAuthCode(
                email,
                authCode
            )
        )
            .thenReturn(Optional.of<T?>(expiredCode))

        // when & then
        val exception =
            Assertions.assertThrows<BusinessException>(BusinessException::class.java, Executable {
                emailAuthCodeService!!.checkAuthCode(request)
            })

        Assertions.assertEquals(ErrorCode.AUTH_CODE_EXPIRED, exception.errorCode)
    }
}