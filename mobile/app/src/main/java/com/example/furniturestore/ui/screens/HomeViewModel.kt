package com.example.furniturestore.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.model.Product
import com.example.furniturestore.repositories.MainLog
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val status: LoadStatus = LoadStatus.Innit(),
    val hi: String = "",
    val products: List<Product> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val log: MainLog?,
    //
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    constructor() : this(null) {
    }
    init {
        loadHomeApp()
        loadProducts()
    }

    private fun loadHomeApp(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val hi = "hi"
                _uiState.value = _uiState.value.copy(
                    hi = hi,
                    status = LoadStatus.Success()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(status = LoadStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }
    private fun loadProducts() {
        db.collection("product")
            .get()
            .addOnSuccessListener { result ->
                val productList = result.map { it.toObject(Product::class.java) }
                _uiState.value = _uiState.value.copy(products = productList, status = LoadStatus.Success())
            }
            .addOnFailureListener { exception ->
                _uiState.value = _uiState.value.copy(status = LoadStatus.Error(exception.message ?: "Lỗi tải dữ liệu"))
            }
    }

}