package com.backend.petplace.domain.point.controller

import com.backend.petplace.domain.point.dto.response.PointHistoryResponse
import com.backend.petplace.domain.point.service.PointService
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.jwt.CustomUserDetails
import com.backend.petplace.global.response.ApiResponse
import com.backend.petplace.global.response.ErrorCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/")
class PointController (
    private val pointService: PointService
) : PointSpecification {

    @GetMapping("/my/points")
    override fun getMyPointHistory(
        @AuthenticationPrincipal userDetails: CustomUserDetails?
    ): ResponseEntity<ApiResponse<PointHistoryResponse?>?>? {
        val user = userDetails ?: throw BusinessException(ErrorCode.NOT_LOGIN_ACCESS)

        val currentUserId = user.getUserId()

        val response = pointService.getPointHistory(currentUserId) // 생성자 주입이므로 !!없이 사용

        return ResponseEntity.ok(
            ApiResponse.success(response)
        )
    }
}
