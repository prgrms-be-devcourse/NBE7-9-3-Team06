package com.backend.petplace.domain.mypage.controller

import com.backend.petplace.domain.mypage.dto.response.MyPageResponse
import com.backend.petplace.domain.mypage.service.MyPageService
import com.backend.petplace.global.jwt.CustomUserDetails
import com.backend.petplace.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class MyPageController(
    private val myPageService: MyPageService
) : MyPageSpecification {

    @GetMapping("/my-page")
    override fun myPage(
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<ApiResponse<MyPageResponse?>> {

        val userId = user.userId
        val response = myPageService.myPage(userId)

        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
