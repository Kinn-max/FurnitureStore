package com.example.furniturestore.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.SharedPreferences
import com.example.furniturestore.config.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class UserProfile(
    val uid: String = "",
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val isEmailVerified: Boolean = false
)
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: TokenManager

) : ViewModel() {
    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _signOutEvent = MutableStateFlow(false)
    val signOutEvent: StateFlow<Boolean> = _signOutEvent.asStateFlow()

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()



    fun initialize(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("754256864805-t1jibglggp4jnkeu8qfikr7f9eoq34oe.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
        updateUser(auth.currentUser)
    }

    fun signIn(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun signOut(context: Context) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            updateUser(null)
            tokenManager.clearToken()
            _signOutEvent.value = true
            Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }
    }
    fun resetSignOutEvent() {
        _signOutEvent.value = false
    }
    fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        task.addOnCompleteListener { signInTask ->
            if (signInTask.isSuccessful) {
                val account = signInTask.result
                firebaseAuthWithGoogle(account.idToken!!)
            } else {
                _isSignedIn.value = false
                _user.value = null
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateUser(auth.currentUser)
                } else {
                    updateUser(null)
                }
            }
    }

    private fun updateUser(firebaseUser: FirebaseUser?) {
        _isSignedIn.value = firebaseUser != null
        _user.value = firebaseUser

        if (firebaseUser != null) {
            val profile = UserProfile(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName,
                email = firebaseUser.email,
                photoUrl = firebaseUser.photoUrl?.toString(),
                phoneNumber = firebaseUser.phoneNumber,
                isEmailVerified = firebaseUser.isEmailVerified
            )
            _userProfile.value = profile


            firebaseUser.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    token?.let {
                        tokenManager.saveToken(it, firebaseUser.uid,firebaseUser.displayName)
                    }
                } else {
                    Log.e("AuthViewModel", "Failed to get ID token")
                }
            }

            Log.d("AuthViewModel", "Custom User Profile: $profile")
        } else {
            _userProfile.value = null
        }
    }

}