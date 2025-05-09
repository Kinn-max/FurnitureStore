package com.example.furniturestore.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.furniturestore.config.TokenManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class UserProfile(
    val uid: String = "",
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val isEmailVerified: Boolean = false,
    val address: String? = null,
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

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun initialize(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("754256864805-t1jibglggp4jnkeu8qfikr7f9eoq34oe.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
//        updateUser(auth.currentUser)
    }

    fun signIn(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun signOut(context: Context) {
        if (!::googleSignInClient.isInitialized) {
            initialize(context)
        }
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            updateUser(null)
            tokenManager.clearToken()
            _signOutEvent.value = true
            Toast.makeText(context, "Logout successful!", Toast.LENGTH_SHORT).show()
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
    fun resetSignOutFlag() {
        _signOutEvent.value = false
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

            // Kiểm tra nếu token đã được lưu trong TokenManager
            firebaseUser.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    token?.let {
                        tokenManager.saveToken(it, firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl.toString())
                    }
                } else {
                    Log.e("AuthViewModel", "Failed to get ID token")
                }
            }

            // Kiểm tra xem người dùng đã tồn tại trong Firestore hay chưa
            db.collection("user").document(profile.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Nếu người dùng đã tồn tại trong Firestore, không cần cập nhật lại các trường
                        Log.d("AuthViewModel", "User already exists in Firestore, no update needed.")
                    } else {
                        // Nếu người dùng chưa tồn tại, thực hiện cập nhật
                        val userMap = hashMapOf(
                            "uid" to profile.uid,
                            "displayName" to profile.displayName,
                            "email" to profile.email,
                            "photoUrl" to profile.photoUrl,
                            "phoneNumber" to profile.phoneNumber,
                            "isEmailVerified" to profile.isEmailVerified,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "address" to ""  // Hoặc các giá trị mặc định khác
                        )

                        db.collection("user")
                            .document(profile.uid) // UID làm ID chính
                            .set(userMap, SetOptions.merge()) // merge để cập nhật nếu đã tồn tại
                            .addOnSuccessListener {
                                Log.d("AuthViewModel", "User saved to Firestore")
                            }
                            .addOnFailureListener { e ->
                                Log.e("AuthViewModel", "Failed to save user to Firestore", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AuthViewModel", "Failed to check user existence in Firestore", e)
                }

            Log.d("AuthViewModel", "Custom User Profile: $profile")
        } else {
            _userProfile.value = null
        }
    }



    fun register(
        context: Context,
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
//        dateOfBirth: String,
        phone: String,
        navController: NavController
    ) {
        _registerError.value = null

        when {
            fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty() -> {
                _registerError.value = "Please enter all field"
            }

            password != confirmPassword -> {
                _registerError.value = "Confirm password fail"
            }

            password.length < 6 -> {
                _registerError.value = "The password must be at least 6 characters"
            }

            else -> {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                val userData = hashMapOf(
                                    "displayName" to fullName,
                                    "email" to email,
                                    "photoUrl" to "https://t3.ftcdn.net/jpg/05/16/27/58/360_F_516275801_f3Fsp17x6HQK0xQgDQEELoTuERO4SsWV.jpg",
//                                    "dateOfBirth" to dateOfBirth,
                                    "phoneNumber" to phone,
                                    "createdAt" to System.currentTimeMillis()
                                )
                                db.collection("user").document(user.uid).set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Create account successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("login")
                                    }
                                    .addOnFailureListener { e ->
                                        _registerError.value = "Error: ${e.message}"
                                    }
                            }
                        } else {
                            _registerError.value = task.exception?.message ?: "Register fail"
                        }
                    }
            }
        }
    }

    fun loginWithEmail(
        email: String,
        password: String,
        onFailure: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onFailure("Please enter email or password")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val uid = firebaseUser.uid

                        db.collection("user").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val displayName = document.getString("displayName")
                                    val photoUrl = document.getString("photoUrl")
                                    firebaseUser.getIdToken(true).addOnCompleteListener { tokenTask ->
                                        if (tokenTask.isSuccessful) {
                                            val token = tokenTask.result?.token
                                            token?.let {
                                                tokenManager.saveToken(
                                                    it,
                                                    uid,
                                                    displayName,
                                                    photoUrl
                                                )
                                            }
                                            _isSignedIn.value = true
                                        } else {
                                            Log.e("AuthViewModel", "Failed to get ID token")
                                        }
                                    }
                                } else {
                                    onFailure("Not found user.")
                                }
                            }
                            .addOnFailureListener { e ->
                                onFailure("Get information user fail: ${e.message}")
                            }
                    } else {
                        onFailure("Get information user fail.")
                    }
                } else {
                    onFailure(task.exception?.message ?: "Login fail")
                }
            }
    }


}