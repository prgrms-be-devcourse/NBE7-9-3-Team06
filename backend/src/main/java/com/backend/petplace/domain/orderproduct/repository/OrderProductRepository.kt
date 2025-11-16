package com.backend.petplace.domain.orderproduct.repository

import com.backend.petplace.domain.orderproduct.entity.OrderProduct
import org.springframework.data.jpa.repository.JpaRepository

interface OrderProductRepository : JpaRepository<OrderProduct, Long>
