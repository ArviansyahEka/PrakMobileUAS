package com.example.prakmobileuas.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.prakmobileuas.ui.LoginActivity

class SessionManager(private val context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_ROLE = "role"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"

        private const val PREF_NAME = "MyAppPref"
    }

    fun createLoginSession(username: String, role: String) {
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    fun getUserDetails(): HashMap<String, String> {
        val userDetails = HashMap<String, String>()
        userDetails[KEY_USERNAME] = sharedPreferences.getString(KEY_USERNAME, "").toString()
        userDetails[KEY_ROLE] = sharedPreferences.getString(KEY_ROLE, "").toString()
        return userDetails
    }

    fun setLogin(status: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, status)
        editor.apply()
    }

    // Fungsi untuk mendapatkan status login
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
}
