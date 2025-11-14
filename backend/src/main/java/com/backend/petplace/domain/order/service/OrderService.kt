package com.backend.petplace.domain.order.service

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse
import com.backend.petplace.domain.order.entity.Order
import com.backend.petplace.domain.order.entity.OrderStatus
import com.backend.petplace.domain.order.repository.OrderRepository
import com.backend.petplace.domain.orderproduct.dto.request.OrderProductCreateRequest
import com.backend.petplace.domain.orderproduct.entity.OrderProduct
import com.backend.petplace.domain.product.entity.Product
import com.backend.petplace.domain.product.repository.ProductRepository
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import com.backend.petplace.global.scheduler.managementfactory.MemoryMonitor
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class OrderService(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
)  {

    @Transactional
    fun createOrder(request: OrderCreateRequest, userId: Long): Long {
        // 기존 유저 읽어서 객체 생성
        val user = readUser(userId)

        // 포인트 부족 시 예외 처리
        // 포인트 충분 시 주문 생성
        val totalPrice = request.totalPrice
        checkLackOfPoint(user, totalPrice)
        val order = Order.createOrder(user, totalPrice)

        // 연관관계 위해 유저에 주문 추가
        user.addOrder(order)

        // 주문 상품들 생성 및 주문에 추가
        val orderProducts = addNewOrderProducts(request, order)
        order.addOrderProducts(orderProducts)

        // 총 가격만큼 유저 포인트 차감
        user.abstractPoints(totalPrice)

        // 저장
        orderRepository.save(order)

        return order.id ?: 0L
    }

    private fun readUser(userId: Long): User =
        userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_MEMBER) }

    private fun checkLackOfPoint(user: User, totalPrice: Int) {
        if (user.getTotalPoint() < totalPrice) {
            throw BusinessException(ErrorCode.NOT_ENOUGH_POINT)
        }
    }

    private fun addNewOrderProducts(request: OrderCreateRequest, order: Order): List<OrderProduct> =
        request.orderProducts
            .map { addNewOrderProduct(it, order) }

    private fun addNewOrderProduct(
        orderProductRequest: OrderProductCreateRequest?,
        order: Order
    ): OrderProduct {
        val product = orderProductRequest?.productId?.let { id ->
            productRepository.findById(id)
                .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_PRODUCT) }
        } ?: throw BusinessException(ErrorCode.NOT_FOUND_PRODUCT)

        checkLackOfStock(orderProductRequest, product)
        return OrderProduct.createOrderProduct(order, product, orderProductRequest.quantity.toLong())
    }

    private fun checkLackOfStock(orderProductRequest: OrderProductCreateRequest?, product: Product) {
        val quantity = orderProductRequest?.quantity ?: 0
        if (quantity > product.stock) {
            throw BusinessException(ErrorCode.NOT_ENOUGH_STOCK)
        }
    }

    fun getOrdersByUserId(userId: Long): List<OrderReadByIdResponse> =
        orderRepository.findByUserId(userId)
            .map { OrderReadByIdResponse(it) }

    @Transactional
    fun cancelOrder(userId: Long, orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_ORDER) }

        if (order.status != OrderStatus.ORDERED) {
            throw BusinessException(ErrorCode.INVALID_ORDER_STATUS)
        }

        order.cancelOrder()
        orderRepository.save(order)

        val user = order.owner
        user.addPoints(order.price)
        userRepository.save(user)
    }

    fun getUserPoints(userId: Long): Int =
        userRepository.findById(userId)
            .map { it.getTotalPoint() }
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_MEMBER) }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(OrderService::class.java)
    }

    @MemoryMonitor
    @Transactional
    fun updateAllOrderStatus() {
        log.info("DB에서 orders 받아오기 \n")
        val orders = orderRepository.findByOrderStatus(OrderStatus.ORDERED)
        log.info("DB에서 orders 받아오기 완료 \n\n")

        log.info("주문 상태 변경 시작 \n")
        for (order in orders) {
            order.setOrderStatusDelivered()
        }
        log.info("주문 상태 변경 끝 \n")
    }
}
