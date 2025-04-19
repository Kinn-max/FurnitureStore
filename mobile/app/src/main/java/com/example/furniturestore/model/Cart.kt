package com.example.furniturestore.model

data class Cart(
    val id: String? = null,
    val productId: String = "",
    val quantity: String = "1",
    val total: Double = 0.0,
    val userId: String = "",
)
