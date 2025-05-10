package com.example.furniturestore.model

data class OrderItem(
//    val productId: String,
    val variantId: String?,
    val quantity: Int,
    val price: Double,
    val productImage: String = "",
    val productName: String = ""
)

fun mapToOrderItem(map: Map<String, Any>): OrderItem {
    return OrderItem(
        quantity = (map["quantity"] as Long).toInt(),
        productImage = map["productImage"].toString(),
        productName = map["productName"].toString(),
        price = map["price"] as Double,
        variantId = map["variantId"] as? String
    )
}
