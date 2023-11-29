package com.example.prakmobileuas.ui
// RegisterActivity.kt
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prakmobileuas.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        val etUsername = findViewById<EditText>(R.id.username)
        val etPassword = findViewById<EditText>(R.id.password)
        val btnRegister = findViewById<Button>(R.id.register_register)
        val btnLogin = findViewById<Button>(R.id.register_login)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            registerUser(username, password)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(username: String, password: String) {
        mAuth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user?.updateProfile(profileUpdates)

                    // Menambahkan informasi peran ke database Firebase Realtime Database
                    val role = "user" // Default role, dapat diubah menjadi "admin" jika diperlukan
                    saveUserRole(user?.uid, role)

                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, UserActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this@RegisterActivity, "Email sudah digunakan.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registrasi gagal. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun saveUserRole(userId: String?, role: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userId ?: "").child("role").setValue(role)
    }
}
