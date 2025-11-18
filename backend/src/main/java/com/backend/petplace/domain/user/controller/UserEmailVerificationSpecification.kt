package com.backend.petplace.domain.user.controller

import com.backend.petplace.domain.email.dto.request.CheckAuthCodeRequest
import com.backend.petplace.global.response.ErrorCode
import com.backend.petplace.domain.user.dto.response.BoolResultResponse
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples
import com.backend.petplace.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "User Email", description = "Email 인증 관련 API")
interface UserEmailVerificationSpecification {

    @ApiErrorCodeExamples(
        ErrorCode.MAIL_CREATION_FAILED,
        ErrorCode.MAIL_AUTH_FAILED, ErrorCode.SMTP_CONNECTION_FAILED, ErrorCode.MAIL_SEND_FAILED
    )
    @Operation(
        summary = "회원가입 이메일 인증번호 전송",
        description = "이메일 인증하기 버튼을 누르면 인증번호를 해당 이메일에 전송합니다. (유효성 검사: 빈칸 유무, 이메일 형식)"
    )
    fun sendAuthCodeToEmail(
        @RequestParam @NotBlank @Email email: String
    ): ResponseEntity<ApiResponse<BoolResultResponse?>>

    @ApiErrorCodeExamples(ErrorCode.AUTH_CODE_NOT_FOUND, ErrorCode.AUTH_CODE_EXPIRED)
    @Operation(
        summary = "회원가입 이메일 인증번호 검사",
        description = "인증 완료 버튼을 누르면 인증번호 검사를 실행합니다. (유효성 검사: 이메일 형식, 인증번호, 이메일 빈칸 유무)"
    )
    fun checkAuthCode(
        @RequestBody @Valid request: CheckAuthCodeRequest
    ): ResponseEntity<ApiResponse<BoolResultResponse?>>
}