package com.example.furniturestore.model

import com.google.firebase.Timestamp

data class Ordered(
    val id: String = "",
    val orderItems: List<Map<String, Any>> = emptyList(),
    val shippingAddress: String = "",
    val status: String = "",
    val totalAmount: Int = 0,
    val userId: String = "",
    val createdAt: Timestamp? = null,
)
