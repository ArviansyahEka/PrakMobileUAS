package com.example.prakmobileuas.ui

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prakmobileuas.R
import com.example.prakmobileuas.main.SessionManager
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var sessionManager: SessionManager
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)


        val etUsername = findViewById<EditText>(R.id.username)
        val etPassword = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.login_login)
        val btnRegister = findViewById<Button>(R.id.login_daftar)

        btnLogin.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            loginUser(username, password)
        }

        btnRegister.setOnClickListener {
            Log.d("LoginActivity", "Register button clicked")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (sessionManager.isLoggedIn()) {
            // Pengguna sudah login, arahkan ke Activity utama
            redirectToAppropriateActivity(sessionManager.getUserDetails()[SessionManager.KEY_ROLE] ?: "")
            finish() // Menutup LoginActivity agar tidak dapat dikembalikan
        }
    }

    private fun loginUser(username: String, password: String) {
        mAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    getUserRoleFromDatabase(user?.uid) { role ->
                        if (!role.isNullOrBlank()) {
                            val lowercaseRole = role.toLowerCase()

                            // Store user details in the session
                            sessionManager.createLoginSession(username, lowercaseRole)
                            sessionManager.setLogin(true) // Tandai bahwa pengguna sudah login

                            if (lowercaseRole == "admin") {
                                handleUserRole("Admin")
                            } else {
                                handleUserRole("User")
                            }
                        } else {
                            Log.e("LoginActivity", "Role is null or blank.")
                            Toast.makeText(this@LoginActivity, "Role is null or blank.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("LoginActivity", "Authentication failed: ${task.exception}")
                    Toast.makeText(this@LoginActivity, "Login gagal. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun getUserRoleFromDatabase(userId: String?, callback: (String?) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val userReference = databaseReference.child(userId ?: "")
        val roleReference = userReference.child("role")

        roleReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val role = dataSnapshot.getValue(String::class.java)
                callback.invoke(role)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("LoginActivity", "Database error: ${databaseError.message}")
                callback.invoke(null)
            }
        })
    }

    private fun handleUserRole(role: String) {
        // Simpan informasi sesi dan arahkan ke aktivitas yang sesuai
        Toast.makeText(this@LoginActivity, "Login berhasil sebagai $role!", Toast.LENGTH_SHORT).show()
        sessionManager.createLoginSession(username, role)
        redirectToAppropriateActivity(role)
    }

    private fun redirectToAppropriateActivity(role: String) {
        val intent = when (role) {
            "Admin" -> Intent(this@LoginActivity, AdminActivity::class.java)
            "User" -> Intent(this@LoginActivity, UserActivity::class.java)
            else -> {
                // Handle the case where role is neither "Admin" nor "User"
                Log.e("LoginActivity", "Unknown role: $role")
                Toast.makeText(this@LoginActivity, "Unknown role: $role", Toast.LENGTH_SHORT).show()
                null
            }
        }

        intent?.let {
            startActivity(it)
            finish()
        }
    }
}
