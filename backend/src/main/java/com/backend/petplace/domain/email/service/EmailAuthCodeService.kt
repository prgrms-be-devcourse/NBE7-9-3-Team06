package com.backend.petplace.domain.email.service

import com.backend.petplace.domain.email.dto.request.CheckAuthCodeRequest
import com.backend.petplace.domain.email.entity.EmailAuthCode
import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository
import com.backend.petplace.domain.user.dto.response.BoolResultResponse
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailAuthenticationException
import org.springframework.mail.MailException
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.SocketTimeoutException
import kotlin.random.Random

@Service
class EmailAuthCodeService(
    private val javaMailSender: JavaMailSender,
    private val emailAuthCodeRepository: EmailAuthCodeRepository,
    @Value("\${spring.mail.username}")
    private val senderEmail: String,
    @Value("\${spring.mail.properties.auth-code-expiration-minutes}")
    private val authCodeExpirationTime: Long
) {
    companion object {
        private const val TITLE = "[PetPlace] Email 인증 코드"
    }

    fun createCode(): String {
        val key = StringBuilder()
        repeat(7) { // 인증 코드 7자리
            when (Random.nextInt(3)) {
                0 -> key.append((Random.nextInt(26) + 65).toChar()) // A–Z
                1 -> key.append((Random.nextInt(26) + 97).toChar()) // a–z
                2 -> key.append(Random.nextInt(10))                 // 0–9
            }
        }
        return key.toString()
    }

    fun createMail(mail: String, authCode: String): MimeMessage {
        val message = javaMailSender.createMimeMessage()
        try {
            message.setFrom(senderEmail)
            message.setRecipients(MimeMessage.RecipientType.TO, mail)
            message.setSubject(TITLE)
            val body = """
                <h3>요청하신 인증 번호입니다.</h3>
                <h1>$authCode</h1>
                <h3>감사합니다.</h3>
            """.trimIndent()
            message.setText(body, "UTF-8", "html")
        } catch (e: MessagingException) {
            throw BusinessException(ErrorCode.MAIL_CREATION_FAILED)
        }
        return message
    }

    // 메일 발송
    @Transactional
    fun sendMail(sendEmail: String): BoolResultResponse {
        val authCode = createCode() // 랜덤 인증번호 생성
        val message = createMail(sendEmail, authCode) // 메일 생성

        try {
            javaMailSender.send(message) // 메일 발송
            saveEmailAuthCode(sendEmail, authCode)
        } catch (e: MailAuthenticationException) {
            throw BusinessException(ErrorCode.MAIL_AUTH_FAILED)
        } catch (e: MailSendException) {
            if (e.cause is SocketTimeoutException) {
                throw BusinessException(ErrorCode.SMTP_CONNECTION_FAILED)
            }
            throw BusinessException(ErrorCode.MAIL_SEND_FAILED)
        } catch (e: MailException) {
            throw BusinessException(ErrorCode.MAIL_SEND_FAILED)
        }
        return BoolResultResponse(true)
    }

    @Transactional
    protected fun saveEmailAuthCode(email: String, authCode: String) {
        val emailAuthCode = EmailAuthCode.create(email, authCode, authCodeExpirationTime)
        emailAuthCodeRepository.save(emailAuthCode)
    }

    @Transactional
    fun checkAuthCode(request: CheckAuthCodeRequest): BoolResultResponse {
        val emailAuthCode = emailAuthCodeRepository.findByEmailAndAuthCode(
            requireNotNull(request.email),
            requireNotNull(request.authCode)
        ) ?: throw BusinessException(ErrorCode.AUTH_CODE_NOT_FOUND)

        if (emailAuthCode.isExpired()) {
            throw BusinessException(ErrorCode.AUTH_CODE_EXPIRED)
        }

        // 성공하면 인증번호 값을 true로 변경
        if (!emailAuthCode.verified) {
            emailAuthCode.markVerifiedTrue()
        }

        return BoolResultResponse(true)
    }
}