package com.backend.petplace.domain.order.controller

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse
import com.backend.petplace.domain.order.service.OrderService
import com.backend.petplace.global.jwt.CustomUserDetails
import com.backend.petplace.global.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    fun createOrder(
        @RequestBody request: OrderCreateRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse<Long>> {
        val userId = userDetails.userId
        val orderId = orderService.createOrder(request, userId)
        return ResponseEntity.ok(ApiResponse.success(orderId))
    }

    @GetMapping
    fun getOrderById(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse<List<OrderReadByIdResponse>>> {
        val userId = userDetails.userId
        val responses: List<OrderReadByIdResponse> =
            orderService.getOrdersByUserId(userId)  // null 제거
        return ResponseEntity.ok(ApiResponse.success(responses))
    }

    @PatchMapping("/{orderid}/cancel")
    fun cancelOrder(
        @PathVariable("orderid") orderId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        val userId = userDetails.userId
        orderService.cancelOrder(userId, orderId)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @GetMapping("/points")
    fun getUserPoints(
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<ApiResponse<Int>> {
        val userId = userDetails.userId
        val points = orderService.getUserPoints(userId)
        return ResponseEntity.ok(ApiResponse.success(points))
    }
}