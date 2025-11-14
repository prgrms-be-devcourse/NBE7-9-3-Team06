package com.backend.petplace.domain.order.dto.response

import com.backend.petplace.domain.order.entity.Order
import com.backend.petplace.domain.order.entity.OrderStatus
import com.backend.petplace.domain.orderproduct.entity.OrderProduct
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "주문 상세 조회 응답 DTO")
data class OrderReadByIdResponse(
    @Schema(description = "주문 ID", example = "1L")
    val orderId: Long,

    @Schema(description = "주문 수정 일시", example = "2023-10-05T14:30:00")
    val updatedAt: LocalDateTime,

    @Schema(description = "주문 상태", example = "ORDERED")
    val orderStatus: OrderStatus,

    @Schema(description = "총 주문 금액", example = "50000")
    val totalPrice: Int,

    @Schema(description = "주문한 상품 목록")
    val orderProducts: List<OrderProductDto>
) {
    constructor(order: Order) : this(
        orderId = order.id ?: 0L,
        updatedAt = order.lastModified ?: LocalDateTime.MIN,
        orderStatus = order.status,
        totalPrice = order.price,
        orderProducts = order.products.map { OrderProductDto(it) }
    )

    @Schema(description = "주문 상품 정보 DTO")
    data class OrderProductDto(
        val productName: String,
        val quantity: Long
    ) {
        constructor(orderProduct: OrderProduct) : this(
            productName = orderProduct.product.productName,
            quantity = orderProduct.quantity
        )
    }
}