package com.example.furniturestore.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturestore.common.enum.LoadStatus
import com.example.furniturestore.config.TokenManager
import com.example.furniturestore.model.Product
import com.example.furniturestore.repositories.MainLog
import com.example.furniturestore.ui.screens.auth.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ProfileUiState(
    val status: LoadStatus = LoadStatus.Innit(),
    val name: String = "",
    val userProfile: UserProfile? = null,
)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val log: MainLog?,
    private val tokenManager: TokenManager,
    //
) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfileApp()
    }

    private fun loadProfileApp() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = LoadStatus.Loading())
            try {
                val uid = tokenManager.getUserUid() // Lấy UID của user

                if (uid!!.isNotEmpty()) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val userProfile = document.toObject(UserProfile::class.java)
                                _uiState.value = _uiState.value.copy(
                                    userProfile = userProfile,
                                    status = LoadStatus.Success()
                                )
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    status = LoadStatus.Error("User not found")
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = _uiState.value.copy(
                                status = LoadStatus.Error(e.message ?: "Failed to fetch user")
                            )
                        }
                } else {
                    _uiState.value = _uiState.value.copy(status = LoadStatus.Error("Invalid UID"))
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(status = LoadStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }

}
