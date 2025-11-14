package com.backend.petplace.domain.product.service

import com.backend.petplace.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    fun getAllProducts() = productRepository.findAll()
}