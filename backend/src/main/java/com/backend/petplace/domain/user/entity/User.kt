package com.backend.petplace.domain.user.entity

import com.backend.petplace.domain.pet.entity.Pet
import com.backend.petplace.domain.order.entity.Order
import com.backend.petplace.domain.user.dto.request.UserSignupRequest
import com.backend.petplace.global.entity.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    val id: Long? = null,

    @Column(name = "userEmail", unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "userName", unique = true, nullable = false)
    val nickName: String,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val zipcode: String,

    val addressDetail: String?,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val orders: MutableList<Order> = mutableListOf(),

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "user")
    val pets: MutableList<Pet> = mutableListOf(),

    ) : BaseEntity() {

    companion object {
        fun create(request: UserSignupRequest, password: String): User {
            return User(
                nickName = requireNotNull(request.nickName) {"이름은 필수입니다."},
                password = password,
                email = requireNotNull(request.email) {"이메일은 필수입니다."},
                address = requireNotNull(request.address) {"주소는 필수입니다."},
                zipcode = requireNotNull(request.zipcode) {"우편번호는 필수입니다."},
                addressDetail = request.addressDetail
            )
        }
    }

    fun addOrders(orders: List<Order>) {
        orders.forEach { addOrder(it) }
    }

    fun addOrder(order: Order) {
        this.orders.add(order)
        order.setUser(this)
    }

    var totalPoint: Int = 10
        private set

    fun addPoints(amount: Int) {
        this.totalPoint += amount
    }

    fun abstractPoints(amount: Int) {
        this.totalPoint -= amount
    }
}