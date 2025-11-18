package com.backend.petplace.domain.user.controller

import com.backend.petplace.domain.email.dto.request.CheckAuthCodeRequest
import com.backend.petplace.domain.email.service.EmailAuthCodeService
import com.backend.petplace.domain.user.dto.response.BoolResultResponse
import com.backend.petplace.global.response.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/v1/email/auth")
class UserEmailVerificationController(
    private val emailAuthCodeService: EmailAuthCodeService
): UserEmailVerificationSpecification {

    @GetMapping
    override fun sendAuthCodeToEmail(
        @RequestParam @NotBlank @Email email: String
    ): ResponseEntity<ApiResponse<BoolResultResponse?>> {
        val response = emailAuthCodeService.sendMail(email)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PatchMapping
    override fun checkAuthCode(
        @RequestBody @Valid request: CheckAuthCodeRequest
    ): ResponseEntity<ApiResponse<BoolResultResponse?>> {
        val response = emailAuthCodeService.checkAuthCode(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}