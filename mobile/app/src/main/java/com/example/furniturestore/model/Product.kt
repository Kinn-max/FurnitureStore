package com.example.furniturestore.model

import com.google.firebase.firestore.PropertyName

data class Product(
    val id: Int? = null,
    val name: String? = null,
    val price: Double? = null,
    val image: String? = null,
    val color: String? = null,
    val category_id:String? = null,
    val description:String? = null,
    @PropertyName("isVariant")
    val isVariant: Boolean? = null,
)
