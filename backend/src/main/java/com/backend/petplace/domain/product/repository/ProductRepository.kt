package com.backend.petplace.domain.product.repository

import com.backend.petplace.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>
