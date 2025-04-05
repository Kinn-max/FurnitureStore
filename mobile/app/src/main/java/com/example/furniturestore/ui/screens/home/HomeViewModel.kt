package com.example.furniturestore.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.config.TokenManager
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
    val productJustForYou: List<Product> = emptyList(),
    val productDeal: List<Product> = emptyList(),
    val name: String = "",
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val log: MainLog?,
    private val tokenManager: TokenManager
    //
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeApp()
        loadRandomProducts(5, isDeal = false)
        loadRandomProducts(2, isDeal = true)
    }

    private fun loadHomeApp(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val name = tokenManager.getName() ?: ""
                _uiState.value = _uiState.value.copy(
                    name= name,
                    status = LoadStatus.Success()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(status = LoadStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }
    private fun loadRandomProducts(count: Int, isDeal: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            db.collection("product")
                .get()
                .addOnSuccessListener { result ->
                    val productList = result.map { document ->
                        val product = document.toObject(Product::class.java)
                        product.copy(id = document.id.toIntOrNull()) // Chuyển document ID thành Int nếu có thể
                    }
                    val randomList = productList.shuffled().take(count)

                    _uiState.value = if (isDeal) {
                        _uiState.value.copy(productDeal = randomList, status = LoadStatus.Success())
                    } else {
                        _uiState.value.copy(productJustForYou = randomList, status = LoadStatus.Success())
                    }
                }
                .addOnFailureListener { exception ->
                    _uiState.value = _uiState.value.copy(
                        status = LoadStatus.Error(exception.message ?: "Lỗi tải dữ liệu")
                    )
                }
        }
    }



}