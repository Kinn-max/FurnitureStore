package com.example.furniturestore

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MainState(
    var error: String = ""
)
@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {
    val _uiState = MutableStateFlow(MainState())
    val uiState  = _uiState.asStateFlow()

    fun setError(message:String){
        _uiState.value = _uiState.value.copy(message)
    }
}