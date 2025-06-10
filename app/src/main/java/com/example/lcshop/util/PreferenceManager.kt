package com.example.lcshop.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    companion object {
        private const val PREF_NAME = "lcshop"
        private const val KEY_USER_TOKEN = "user_token"

    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ----- Token người dùng -----
    fun setUserToken(token: String) {
        prefs.edit().putString(KEY_USER_TOKEN, token).apply()
    }

    fun getUserToken(): String? {
        return prefs.getString(KEY_USER_TOKEN, null)
    }
    // ----- Xóa toàn bộ (dùng khi logout) -----
    fun clear() {
        prefs.edit().clear().apply()
    }
}
