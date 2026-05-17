package no.usn.kulturminner.data.local

import android.content.Context
import androidx.core.content.edit

class TokenStorage(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit { putString("token", token) }
    fun saveUserId(userId: String) = prefs.edit { putString("user_id", userId) }

    fun getToken(): String? = prefs.getString("token", null)
    fun getUserId(): String? = prefs.getString("user_id", null)

    fun clear() = prefs.edit { clear() }

    fun isLoggedIn(): Boolean = getToken() != null
}