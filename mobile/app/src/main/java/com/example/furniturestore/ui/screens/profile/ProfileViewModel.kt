package com.example.furniturestore.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturestore.common.status.LoadStatus
import com.example.furniturestore.config.TokenManager
import com.example.furniturestore.model.Ordered
import com.example.furniturestore.repositories.MainLog
import com.example.furniturestore.ui.screens.auth.UserProfile
import com.example.furniturestore.ui.screens.cart.CartUiState
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ProfileUiState(
    val status: LoadStatus = LoadStatus.Innit(),
    val name: String? = "",
    val userProfile: UserProfile? = null,
    val sigOut:Boolean = false,
    val orders:List<Ordered> = emptyList(),
    val uid: String = "",
)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val log: MainLog?,
    private val tokenManager: TokenManager,
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getName()
        getInformation()
        getOrderList()
        getUid()
    }
    fun resetState() {
        _uiState.value = ProfileUiState()
    }
    fun getName() {
        val name = tokenManager.getName()
        _uiState.value = _uiState.value.copy(name = name)
    }
    fun getUid(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val userUid = tokenManager.getUserUid() ?: ""
                _uiState.value = _uiState.value.copy(
                    uid   =  userUid,
                    status = LoadStatus.Success()
                )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(status = LoadStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }
    fun getInformation() {
        Log.d("ProfileViewModel", "getInformation() called")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val uid = tokenManager.getUserUid()
                if (uid == null) {
                    _uiState.value = _uiState.value.copy(userProfile = null, status = LoadStatus.Success())
                } else {
                    db.collection("user").document(uid).get()
                        .addOnSuccessListener { document ->
                            Log.d("Firestore", "Document loaded: ${document.data}")
                            val profile = document.toObject(UserProfile::class.java)
                            Log.d("ProfileViewModel", "Deserialized Profile: $profile")
                            _uiState.value = _uiState.value.copy(userProfile = profile, status = LoadStatus.Success())
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = _uiState.value.copy(
                                status = LoadStatus.Error(e.message ?: "Failed to load user")
                            )
                        }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error(e.message ?: "Unknown error")
                )
            }
        }
    }
    fun getOrderList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val uid = tokenManager.getUserUid()
                if (uid != null) {
                    db.collection("order")
                        .whereEqualTo("userId", uid)
                        .get()
                        .addOnSuccessListener { documents ->
//                            val orders = documents.map { it.data }
                             val orders = documents.map { it.toObject(Ordered::class.java) }
                            Log.d("ProfileViewModel", "Orders: $orders")
                            _uiState.value = _uiState.value.copy( orders = orders,status = LoadStatus.Success())
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = _uiState.value.copy(
                                status = LoadStatus.Error(e.message ?: "Failed to load orders")
                            )
                        }
                } else {
                    _uiState.value = _uiState.value.copy(
                        status = LoadStatus.Error("User ID is null")
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    status = LoadStatus.Error(e.message ?: "Unknown error")
                )
            }
        }
    }

}