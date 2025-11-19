package com.backend.petplace.domain.order.service

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest
import com.backend.petplace.domain.order.repository.OrderRepository
import com.backend.petplace.domain.product.repository.ProductRepository
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.then
import org.mockito.kotlin.whenever
import java.util.Optional

class OrderServiceSimpleTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var user: User

    private lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        orderService = OrderService(userRepository, orderRepository, productRepository)
    }

    @Test
    @DisplayName("createOrder 정상 동작")
    fun `createOrder success`() {
        val userId = 1L
        whenever(user.totalPoint).thenReturn(100_000)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))
        whenever(orderRepository.save(any())).thenAnswer { it.arguments[0] }

        val request = OrderCreateRequest(totalPrice = 5000, orderProducts = mutableListOf())
        val orderId = orderService.createOrder(request, userId)

        assertEquals(0L, orderId)
        then(orderRepository).should().save(any())
    }

    @Test
    @DisplayName("createOrder 포인트 부족 시 예외")
    fun `createOrder not enough points`() {
        val userId = 1L
        whenever(user.totalPoint).thenReturn(1000)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))

        val request = OrderCreateRequest(totalPrice = 5000, orderProducts = mutableListOf())
        val exception = assertThrows<BusinessException> {
            orderService.createOrder(request, userId)
        }

        assertEquals(ErrorCode.NOT_ENOUGH_POINT, exception.errorCode)
    }

    @Test
    @DisplayName("getUserPoints 반환")
    fun `getUserPoints success`() {
        val userId = 1L
        whenever(user.totalPoint).thenReturn(500)
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(user))

        val points = orderService.getUserPoints(userId)

        assertEquals(500, points)
    }
}
