package com.backend.petplace.domain.product.entity

import com.backend.petplace.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
class Product(

    @Column(name = "product_name", nullable = false)
    private var _productName: String = "",

    @Column(name = "price", nullable = false)
    @Access(AccessType.FIELD)
    private var _price: Long = 0L,

    @Column(name = "stock", nullable = false)
    private var _stock: Long = 0L,

    @Column(name = "description")
    var description: String? = null
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val productId: Long? = null

    // 외부에서 읽기 전용으로 제공
    val productName: String
        get() = _productName

    val price: Long
        get() = _price

    val stock: Long
        get() = _stock

    companion object {
        fun create(
            productName: String,
            price: Long,
            stock: Long,
            description: String? = null
        ) = Product().apply {
            _productName = productName
            _price = price
            _stock = stock
            this.description = description
        }
    }
}