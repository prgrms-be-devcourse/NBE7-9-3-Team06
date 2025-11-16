package com.backend.petplace.domain.order.entity

import com.backend.petplace.domain.orderproduct.entity.OrderProduct
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.global.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order (
    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private var user: User,

    @Column(name = "total_price", nullable = true)
    private var totalPrice: Int,

    @Enumerated(EnumType.STRING)
    private var orderStatus: @NotNull OrderStatus
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val orderId: Long? = null

    @OneToMany(mappedBy = "_order", cascade = [CascadeType.ALL])
    private val orderProducts: MutableList<OrderProduct> = mutableListOf()

    val id: Long? get() = orderId
    val owner: User
        get() = user
    val products: List<OrderProduct> get() = orderProducts.toList()
    val status: OrderStatus get() = orderStatus
    val price: Int get() = totalPrice
    // 충돌 없이 lastModified 접근
    val lastModified: LocalDateTime?
        get() = super.readModifiedDate()

    //객체 생성 후 orderProducts에 order 추가 메소드
    internal fun addOrderProducts(orderProducts: List<OrderProduct>) {
        orderProducts.forEach { addOrderProduct(it) }
    }

    internal fun addOrderProduct(orderProduct: OrderProduct) {
        if (!orderProducts.contains(orderProduct)) {
            orderProducts.add(orderProduct)
        }
    }

    internal fun removeOrderProduct(orderProduct: OrderProduct) {
        orderProducts.remove(orderProduct)
    }

    //객체 생성 후 orders에 order 추가 메소드
    fun setUser(user: User) {
        // 기존 user와의 연관관계 제거
        this.user.getOrders().remove(this)

        // 새로운 user와의 연관관계 설정
        this.user = user

        // 새로운 user의 orders 리스트에 현재 객체 추가
        if (!user.getOrders().contains(this)) { // 아직 연결 안 되어 있으면
            user.getOrders().add(this) // User 쪽 리스트에도 추가
        }
    }

    fun cancelOrder() {
        this.orderStatus = OrderStatus.CANCELED
    }

    fun setOrderStatusDelivered() {
        this.orderStatus = OrderStatus.DELIVERED
    }

    companion object {
        fun createOrder(user: User, totalPrice: Int): Order {
            return Order(user, totalPrice, OrderStatus.ORDERED)
        }
    }
}
