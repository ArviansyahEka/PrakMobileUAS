package com.example.prakmobileuas.ui
// RegisterActivity.kt
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
        val spinnerUserType = findViewById<Spinner>(R.id.spinnerUserType)


        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val userType = spinnerUserType.selectedItem.toString()

            registerUser(username, password, userType)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(username: String, password: String, userType: String) {
        mAuth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user?.updateProfile(profileUpdates)

                    // Saving user role to Firebase Realtime Database
                    saveUserRole(user?.uid, userType)

                    Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()

                    // Redirect based on user type (you might want to customize this)
                    val intent = if (userType == "Admin") {
                        Intent(this@RegisterActivity, AdminActivity::class.java)
                    } else {
                        Intent(this@RegisterActivity, UserActivity::class.java)
                    }

                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this@RegisterActivity, "Email is already in use.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


    private fun saveUserRole(userId: String?, role: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userId ?: "").child("role").setValue(role)
    }
}
