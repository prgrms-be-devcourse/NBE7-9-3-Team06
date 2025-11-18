package com.backend.petplace.domain.user.controller

import com.backend.petplace.domain.user.dto.request.UserLoginRequest
import com.backend.petplace.domain.user.dto.request.UserSignupRequest
import com.backend.petplace.domain.user.dto.response.BoolResultResponse
import com.backend.petplace.domain.user.dto.response.UserLoginResponse
import com.backend.petplace.domain.user.dto.response.UserSignupResponse
import com.backend.petplace.domain.user.service.UserService
import com.backend.petplace.global.response.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userService: UserService
): UserSpecification {

    @GetMapping("/signup-username")
    override fun checkNickName(
        @RequestParam
        @NotBlank
        @Size(min = 2, max = 12)
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$")
        nickName: String
    ): ResponseEntity<ApiResponse<BoolResultResponse?>> {
        val response = userService.validateDuplicateNickName(nickName)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/signup-email")
    override fun checkEmail(
        @RequestParam @NotBlank @Email email: String
    ): ResponseEntity<ApiResponse<BoolResultResponse?>> {
        val response = userService.validateDuplicateEmail(email)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/signup")
    override fun signup(
        @Valid @RequestBody request: UserSignupRequest
    ): ResponseEntity<ApiResponse<UserSignupResponse?>> {
        val response = userService.signup(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/login")
    override fun login(
        @Valid @RequestBody request: UserLoginRequest
    ): ResponseEntity<ApiResponse<UserLoginResponse?>> {
        val response = userService.login(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}