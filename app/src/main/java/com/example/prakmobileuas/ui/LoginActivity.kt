package com.example.prakmobileuas.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prakmobileuas.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        val etUsername = findViewById<EditText>(R.id.username)
        val etPassword = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.login_login)
        val btnRegister = findViewById<Button>(R.id.login_daftar)

        btnLogin.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            loginUser(username, password)
        }

        btnRegister.setOnClickListener {
            Log.d("LoginActivity", "Register button clicked")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(username: String, password: String) {
        mAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    getUserRoleFromDatabase(user?.uid) { role ->
                        if (role == "admin") {
                            // Pengguna adalah admin, lakukan sesuatu
                            handleUserRole(role) // Tambahkan ini agar fungsi handleUserRole dipanggil dengan benar
                        } else {
                            // Pengguna adalah pengguna biasa, lakukan sesuatu yang berbeda
                            handleUserRole(role) // Tambahkan ini agar fungsi handleUserRole dipanggil dengan benar
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login gagal. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getUserRoleFromDatabase(userId: String?, callback: (String?) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val userReference = databaseReference.child(userId ?: "")
        val roleReference = userReference.child("role")

        roleReference.addListenerForSingleValueEvent(object : ValueEventListener { // Ganti ke addListenerForSingleValueEvent agar mendapatkan data hanya sekali
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val role = dataSnapshot.getValue(String::class.java)
                callback.invoke(role)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if needed
                callback.invoke(null)
            }
        })
    }

    private fun handleUserRole(role: String?) {
        if (role == "admin") {
            // User is an admin, perform admin actions
            Toast.makeText(this@LoginActivity, "Login berhasil sebagai Admin!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, AdminActivity::class.java)
            startActivity(intent)
            finish()

        } else {
            // User is a regular user, perform user actions
            Toast.makeText(this@LoginActivity, "Login berhasil sebagai Pengguna!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, UserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
