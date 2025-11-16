package com.backend.petplace.domain.orderproduct.entity

import com.backend.petplace.domain.order.entity.Order
import com.backend.petplace.domain.product.entity.Product
import com.backend.petplace.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
class OrderProduct private constructor(
    @JoinColumn(name = "orderId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var _order: Order,

    @JoinColumn(name = "productId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var _product: Product,

    @Column(name = "quantity", nullable = false)
    private var _quantity: Long
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val orderProductId: Long? = null

    val order: Order get() = _order
    val product: Product get() = _product
    val quantity: Long get() = _quantity

    fun setOrder(order: Order) {
        //기존 order와의 연관관계 제거
        _order.removeOrderProduct(this)

        //새로운 order와의 연관관계 설정
        this._order = order

        //새로운 order의 orderProducts 리스트에 현재 객체 추가
        order.addOrderProduct(this)
    }

    companion object {
        fun createOrderProduct(order: Order, product: Product, quantity: Long): OrderProduct {
            val op = OrderProduct(order, product, quantity)
            order.addOrderProduct(op)
            return op
        }
    }
}
