package com.backend.petplace.domain.email.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CheckAuthCodeRequest(

    @NotBlank(message = "이메일은 필수입니다.")
    @Email
    val email: String?,

    @NotBlank(message = "인증번호는 필수입니다.")
    val authCode: String?
)