package com.example.furniturestore.ui.screens.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.config.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        val userUid = tokenManager.getUserUid() ?: ""
        _uiState.value = CartUiState(uid = userUid)
        Log.e("CartViewModel", "id nek $userUid")
    }
}
