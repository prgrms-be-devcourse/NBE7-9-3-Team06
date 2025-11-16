package com.backend.petplace.domain.order.repository

import com.backend.petplace.domain.order.entity.Order
import com.backend.petplace.domain.order.entity.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByUserId(userId: Long): MutableList<Order>
    fun findByOrderStatus(orderStatus: OrderStatus): MutableList<Order>
}
