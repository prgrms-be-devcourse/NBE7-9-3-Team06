package com.backend.petplace.domain.order.dto.request

import com.backend.petplace.domain.orderproduct.dto.request.OrderProductCreateRequest
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

@Schema(description = "주문 생성 요청 DTO")
data class OrderCreateRequest(
    @Schema(description = "총 주문 금액", example = "50000")
    @Min(value = 0L, message = "총 주문 금액은 0원 이상이어야 합니다.")
    val totalPrice: Int,

    @Schema(description = "주문할 상품 목록")
    val orderProducts: MutableList<OrderProductCreateRequest>
)