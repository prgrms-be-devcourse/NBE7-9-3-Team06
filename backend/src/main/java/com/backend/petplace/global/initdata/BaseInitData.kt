package com.backend.petplace.global.initdata

import com.backend.petplace.domain.order.entity.Order
import com.backend.petplace.domain.order.repository.OrderRepository
import com.backend.petplace.domain.orderproduct.entity.OrderProduct
import com.backend.petplace.domain.product.entity.Product
import com.backend.petplace.domain.product.repository.ProductRepository
import com.backend.petplace.domain.user.dto.request.UserSignupRequest
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class BaseInitData(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val p1 = Product("상품 1", 1L, 9999L, "상품 1")
        val p2 = Product("상품 2", 1L, 9999L, "상품 2")
        val p3 = Product("상품 3", 120L, 9999L, "상품 3")
        val p4 = Product("상품 4", 200L, 9999L, "상품 4")

        // DB에 저장
        productRepository.save<Product>(p1)
        productRepository.save<Product>(p2)
        productRepository.save<Product>(p3)
        productRepository.save<Product>(p4)

        // 스케줄러 테스트용
        // 유저 1번 사용
        val userInfo = UserSignupRequest(
            nickName = "1111",
            password = "encodedPassword",
            email = "testUser1@naver.com",
            authCode = "10001",
            address = "310-5, Stone Street, Manhattan, NY, USA",
            zipcode = "10001",
            addressDetail = "101"
        )

        val user = userRepository.findById(1L)
            .orElseGet { User.create(userInfo, "encodedPassword") }

        val product = productRepository.findById(1L)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_PRODUCT) }

        val orderCount = orderRepository.count()
        if (orderCount == 0L) {
            val orders = mutableListOf<Order>()

            //필요하면 숫자를 늘려주세요.
            //권장: 0 ~ 1
            for (i in 0..-1) {
                // order 생성
                val order = Order.createOrder(user, 0)
                orders.add(order)

                // orderProduct 생성
                val orderProduct = OrderProduct.createOrderProduct(order, product, 1L)
                order.addOrderProducts(listOf(orderProduct))
            }

            // 저장: 한번에
            orderRepository.saveAll<Order?>(orders)
        }
    }
}
