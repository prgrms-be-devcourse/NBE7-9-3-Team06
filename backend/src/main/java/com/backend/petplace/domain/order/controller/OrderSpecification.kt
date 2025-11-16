package com.backend.petplace.domain.order.controller

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples
import com.backend.petplace.global.jwt.CustomUserDetails
import com.backend.petplace.global.response.ApiResponse
import com.backend.petplace.global.response.ErrorCode
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity

interface OrderSpecification {
    @ApiErrorCodeExamples(
        ErrorCode.NOT_ENOUGH_POINT,
        ErrorCode.NOT_FOUND_PRODUCT,
        ErrorCode.NOT_ENOUGH_STOCK
    )
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    fun createOrder(
        @Parameter(description = "가격 총액, 주문객체 리스트") request: OrderCreateRequest,
        @Parameter(description = "JWT토큰에서 받은 유저 정보") userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse<Long>>


    @Operation(summary = "사용자 주문 조회", description = "특정 사용자의 모든 주문을 조회합니다.")
    fun getOrderById(
        @Parameter(description = "JWT토큰에서 받은 유저 정보") userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse<MutableList<OrderReadByIdResponse>>>

    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_ORDER, ErrorCode.INVALID_ORDER_STATUS)
    @Operation(summary = "주문 취소", description = "특정 주문을 취소 상태로 업데이트합니다.")
    fun cancelOrder(
        @Parameter(description = "요청에서 받은 주문 아이디") orderId: Long,
        @Parameter(description = "JWT토큰에서 받은 유저 정보") userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse<Void>>

    @ApiErrorCodeExamples(ErrorCode.NOT_FOUND_MEMBER)
    @Operation(summary = "사용자 포인트 조회", description = "특정 사용자의 현재 포인트 잔액을 조회합니다.")
    fun getUserPoints(
        @Parameter(description = "JWT토큰에서 받은 유저 정보") userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse<Int>>
}
