package com.example.furniturestore.model

data class ProductWithCategory(
    val id: Int? = null,
    val name: String? = null,
    val price: Double? = null,
    val image: String? = null,
    val color: String? = null,
    val category: String? = null,
    val description: String? = null,
    val isVariant: Boolean? = null,
    val isFavorite: Boolean? = null,
)