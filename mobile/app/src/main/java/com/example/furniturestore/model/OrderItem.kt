package com.example.furniturestore.model

data class OrderItem(
    val productId: String,
    val variantId: String?,
    val quantity: Int,
    val price: Double
)
