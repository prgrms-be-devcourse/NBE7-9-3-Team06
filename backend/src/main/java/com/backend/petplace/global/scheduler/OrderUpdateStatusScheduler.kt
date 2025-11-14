package com.backend.petplace.global.scheduler

import com.backend.petplace.domain.order.service.OrderService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OrderUpdateStatusScheduler(
    private val orderService: OrderService
) {

    // 필요할 때 주석 제거하시면 됩니다.
    @Scheduled(fixedRate = 1000 * 60 * 1) // 1분마다 실행
    fun updateOrderStatusEveryDay() {
        orderService.updateAllOrderStatus()
    }
}