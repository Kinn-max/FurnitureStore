package com.example.furniturestore.ui.screens.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.config.TokenManager
import com.example.furniturestore.model.Cart
import com.example.furniturestore.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

data class CartUiState(
    val status: LoadStatus = LoadStatus.Innit(),
    val uid: String = "",
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        val userUid = tokenManager.getUserUid() ?: ""
        _uiState.value = CartUiState(uid = userUid)
        getCartItems()
    }

    fun addToCart(productId: String, total: Double) {
        val userId = _uiState.value.uid

        db.collection("cart")
            .whereEqualTo("userId", userId)
            .whereEqualTo("productId", productId)
            .get()
            .addOnSuccessListener {
                querySnapshot ->
                if(!querySnapshot.isEmpty){
                    val document = querySnapshot.documents[0]
                    val currentQuantity = (document["quantity"] as? String)?.toIntOrNull() ?: 0
                    val newQuantity = currentQuantity + 1
                    val newTotal = newQuantity * total

                    db.collection("cart")
                        .document(document.id)
                        .update(
                            mapOf(
                                "quantity" to newQuantity.toString(),
                                "total" to newTotal
                            )
                        ).addOnSuccessListener {
                            Log.d("CartViewModel", "Cart updated successfully")
                        }.addOnFailureListener { e ->
                            Log.e("CartViewModel", "Error updating cart", e)
                        }
                } else {
                    val cart = Cart(
                        id = UUID.randomUUID().toString(),
                        productId = productId,
                        quantity = "1",
                        total = total,
                        userId = _uiState.value.uid
                    )

                    db.collection("cart")
                        .document(cart.id!!).set(cart)
                        .addOnSuccessListener {
                            Log.d("CartViewModel", "Cart added successfully")
                        }.addOnFailureListener { e ->
                            Log.e("CartViewModel", "Error adding cart", e)
                        }
                }
            }
            .addOnFailureListener{e ->
                Log.e("CartViewModel", "Error fetching cart", e)
            }
    }

    fun getCartItems() {
        val uid = _uiState.value.uid
        if (uid.isEmpty()) return

        _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())

        db.collection("cart")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("CartViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val cartDocuments = snapshots?.documents ?: return@addSnapshotListener
                val cartList = mutableListOf<CartItem>()

                if (cartDocuments.isEmpty()) {
                    _cartItems.value = cartList.toList()
                    _uiState.value = _uiState.value.copy(status = LoadStatus.Success())
                    return@addSnapshotListener
                }

                for (doc in cartDocuments) {
                    val cart = doc.toObject(Cart::class.java) ?: continue
                    val productId = cart.productId
                    val quantity = cart.quantity.toInt()

                    db.collection("product")
                        .document(productId)
                        .get()
                        .addOnSuccessListener { productSnapshot ->
                            val product = productSnapshot.toObject(Product::class.java)
                            if (product != null) {
                                val cartItem = CartItem(
                                    id = productId,
                                    name = product.name ?: "",
                                    price = cart.total,
                                    quantity = quantity,
                                    imageRes = product.image ?: ""
                                )
                                cartList.add(cartItem)
                                _cartItems.value = cartList.toList()
                                _uiState.value = uiState.value.copy(
                                    status = LoadStatus.Success()
                                )
                            }
                        }
                        .addOnFailureListener {
                            Log.e("CartViewModel", "Failed to fetch product: ${it.message}")
                            _uiState.value = _uiState.value.copy(status = LoadStatus.Error("Failed to fetch product"))
                        }
                }
            }
    }

    fun updateQuantity(item: CartItem, newQuantity: Int) {
        db.collection("cart")
            .whereEqualTo("userId", _uiState.value.uid)
            .whereEqualTo("productId", item.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]

                    if (newQuantity <= 0) {
                        db.collection("cart").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                getCartItems()
                            }
                        return@addOnSuccessListener
                    }

                    val newTotal = item.price * newQuantity
                    db.collection("cart").document(document.id)
                        .update(
                            mapOf(
                                "quantity" to newQuantity.toString(),
                                "total" to newTotal
                            )
                        )
                        .addOnSuccessListener {
                            getCartItems()
                        }
                }
            }
    }
}
