package com.backend.petplace.domain.orderproduct.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주문 상품 생성 요청 DTO")
class OrderProductCreateRequest(
    @Schema(description = "상품 ID", example = "1L")
    val productId: Long,

    @Schema(description = "상품 수량", example = "2")
    val quantity: Int = 0
)