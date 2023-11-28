package com.example.prakmobileuas.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prakmobileuas.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        val etUsername = findViewById<EditText>(R.id.username)
        val etPassword = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.login)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            loginWithFirebase(username, password)
        }
    }

    private fun loginWithFirebase(username: String, password: String) {
        mAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // Login berhasil, arahkan ke aktivitas selanjutnya
                    navigateToNextActivity()
                } else {
                    // Jika login gagal, tampilkan pesan kesalahan
                    Toast.makeText(this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun navigateToNextActivity() {
        // Tentukan peran pengguna (Admin atau User) dan arahkan ke aktivitas yang sesuai
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            val username = currentUser.email ?: "" // Jika email null, set sebagai string kosong
            val intent = if (isAdmin(username)) {
                Intent(this@LoginActivity, AdminActivity::class.java)
            } else {
                Intent(this@LoginActivity, UserActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }


    private fun isAdmin(username: String): Boolean {
        // Implementasikan logika untuk menentukan apakah pengguna adalah admin
        // Misalnya, jika email pengguna mengandung "admin", return true
        return username.contains("admin", ignoreCase = true)
    }
}

