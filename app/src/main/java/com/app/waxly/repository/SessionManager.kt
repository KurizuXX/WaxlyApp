package com.app.waxly.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// para manejar sesión de usuario
class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUser(name: String, email: String) {
        prefs.edit {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putBoolean(KEY_LOGGED_IN, true)
        }
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit { putBoolean(KEY_LOGGED_IN, loggedIn) }
    }

    fun clear() {
        prefs.edit { clear() }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)
    fun getName(): String? = prefs.getString(KEY_NAME, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    companion object {
        private const val PREFS_NAME = "waxly_session"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
    }
}