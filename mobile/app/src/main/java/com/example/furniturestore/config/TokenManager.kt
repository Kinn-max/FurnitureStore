package com.example.furniturestore.config

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveToken(token: String, uid: String) {
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.putString("user_uid", uid)  // Lưu uid
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun getUserUid(): String? {
        return sharedPreferences.getString("user_uid", null)  // Lấy uid
    }

    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.remove("user_uid")  // Xóa uid khi đăng xuất
        editor.apply()
    }
}
