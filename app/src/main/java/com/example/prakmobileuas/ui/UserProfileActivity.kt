package com.example.prakmobileuas.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.prakmobileuas.R
import com.example.prakmobileuas.main.SessionManager

class UserProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Mendapatkan data pengguna dari sesi
        val userDetails = sessionManager.getUserDetails()
        val username = userDetails[SessionManager.KEY_USERNAME]
        val role = userDetails[SessionManager.KEY_ROLE]

        // Menetapkan nilai ke TextViews
        val usernameTextView = findViewById<TextView>(R.id.username)
        val roleTextView = findViewById<TextView>(R.id.role)
        usernameTextView.text = username
        roleTextView.text = role

        // Tombol Kembali
        val backButton = findViewById<ImageView>(R.id.backbutton)
        backButton.setOnClickListener {
            // Kembali ke halaman sebelumnya atau lakukan aksi sesuai kebutuhan
            finish()
        }

        // Panggil fungsi setupLogoutButton() di sini
        setupLogoutButton()
    }

    private fun setupLogoutButton() {
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Panggil fungsi logout dari SessionManager
            sessionManager.logoutUser()
            // Pada saat logout
            sessionManager.setLogin(false)


            // Redirect ke halaman login setelah logout
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
