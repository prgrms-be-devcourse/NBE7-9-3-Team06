package com.backend.petplace.domain.mypage.controller

import com.backend.petplace.domain.mypage.dto.response.MyPageResponse
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples
import com.backend.petplace.global.jwt.CustomUserDetails
import com.backend.petplace.global.response.ApiResponse
import com.backend.petplace.global.response.ErrorCode.NOT_FOUND_MEMBER
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "MyPage", description = "마이페이지 API")
interface MyPageSpecification {

    @ApiErrorCodeExamples(NOT_FOUND_MEMBER)
    @Operation(summary = "마이페이지 불러오기", description = "마이페이지 정보를 불러옵니다.")
    fun myPage(user: CustomUserDetails): ResponseEntity<ApiResponse<MyPageResponse?>>
}