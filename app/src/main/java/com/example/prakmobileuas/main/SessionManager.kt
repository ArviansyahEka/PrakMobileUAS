package com.example.prakmobileuas.main// com.example.prakmobileuas.main.SessionManager.kt

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_ROLE = "role"
    }

    fun createLoginSession(username: String, role: String) {
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun getUserDetails(): HashMap<String, String> {
        val userDetails = HashMap<String, String>()
        userDetails[KEY_USERNAME] = sharedPreferences.getString(KEY_USERNAME, "").toString()
        userDetails[KEY_ROLE] = sharedPreferences.getString(KEY_ROLE, "").toString()
        return userDetails
    }

    // Add other session-related functions as needed

    // ... rest of the code remains unchanged
}
