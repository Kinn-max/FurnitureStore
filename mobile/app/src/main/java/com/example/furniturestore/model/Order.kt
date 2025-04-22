package com.example.furniturestore.model

import java.sql.Timestamp

data class Order(
    val id: String,
    val userId: String,
    val shippingAddress: String,
    val status: String,
    val totalAmount: Double,
    val createdAt: Timestamp,
    val orderItems: List<OrderItem>
)