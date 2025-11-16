package com.backend.petplace.domain.product.controller

import com.backend.petplace.domain.product.service.ProductService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ProductController(
    private val productService: ProductService
)