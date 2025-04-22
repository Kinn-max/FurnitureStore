package com.example.furniturestore.ui.screens.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturestore.common.status.LoadStatus
import com.example.furniturestore.config.TokenManager
import com.example.furniturestore.model.Cart
import com.example.furniturestore.model.Order
import com.example.furniturestore.model.OrderItem
import com.example.furniturestore.model.Product
import com.example.furniturestore.ui.screens.auth.UserProfile
import com.example.furniturestore.ui.screens.cart.CartItem
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user.asStateFlow()

    private val _status = MutableStateFlow<LoadStatus>(LoadStatus.Innit())
    val status: StateFlow<LoadStatus> = _status.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    init {
        loadUserData()
        loadCartItems()
    }

    fun resetStatus() {
        _status.value = LoadStatus.Success()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _status.value = LoadStatus.Loading()
            val uid = tokenManager.getUserUid()
            if (uid == null) {
                Log.d("Error", "Not found user")
                _status.value = LoadStatus.Error("Failed to load user")
            } else {
                db.collection("user").document(uid).get()
                    .addOnSuccessListener { document ->
                        val profile = document.toObject(UserProfile::class.java)
                        _user.value = profile
                    }
                    .addOnFailureListener { e ->
                        _status.value = LoadStatus.Error(e.message ?: "Failed to load user")
                    }
            }
        }
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            val uid = tokenManager.getUserUid()
            _status.value = LoadStatus.Loading()

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
                        _status.value = LoadStatus.Success()
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
                                    _status.value = LoadStatus.Success()
                                }
                            }
                            .addOnFailureListener {
                                Log.e("CartViewModel", "Failed to fetch product: ${it.message}")
                                _status.value = LoadStatus.Error("Failed to fetch product")
                            }
                    }
                }
        }
    }

    fun placeOrder(
        cartItems: List<CartItem>,
        userId: String,
        name: String,
        phone: String,
        email: String,
        address: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _status.value = LoadStatus.Loading()
                if (name == "" || name.isEmpty()) {
                    _status.value = LoadStatus.Error("Please enter all field")
                } else if (phone == "" || phone.isEmpty()) {
                    _status.value = LoadStatus.Error("Please enter all field")
                } else if (email == "" || email.isEmpty()) {
                    _status.value = LoadStatus.Error("Please enter all field")
                } else if (address == "" || address.isEmpty()) {
                    _status.value = LoadStatus.Error("Please enter all field")
                } else {
                    val totalAmount = cartItems.sumOf { it.price * it.quantity }
                    val order = Order(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        shippingAddress = address,
                        status = "PENDING",
                        totalAmount = totalAmount,
                        createdAt = Timestamp(System.currentTimeMillis()),
                        orderItems = cartItems.map {
                            OrderItem(
                                productId = it.id,
                                variantId = null,
                                quantity = it.quantity,
                                price = it.price
                            )
                        }
                    )

                    db.collection("order")
                        .document(order.id)
                        .set(order)
                        .addOnSuccessListener {
                            clearUserCart(userId)
                            _status.value = LoadStatus.Success()
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _status.value = LoadStatus.Error(e.message ?: "Unknown error")
                            onFailure(e.message ?: "Unknown error")
                        }
                }
            } catch (e: Exception) {
                _status.value = LoadStatus.Error(e.message ?: "Unknown error")
                onFailure(e.message ?: "Unknown error")
            }
        }
    }

    private fun clearUserCart(uid: String) {
        db.collection("cart")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    db.collection("cart").document(doc.id).delete()
                }
            }
    }
}
