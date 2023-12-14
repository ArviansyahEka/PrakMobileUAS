package com.example.prakmobileuas.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.prakmobileuas.R
import com.example.prakmobileuas.adapter.FilmItemClickListener
import com.example.prakmobileuas.adapter.FilmMingguIniAdapter
import com.example.prakmobileuas.adapter.HomeCarrousel
import com.example.prakmobileuas.database.Film
import com.example.prakmobileuas.main.SessionManager
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity(), FilmItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPagerCarousel: ViewPager2
    private lateinit var carouselAdapter: HomeCarrousel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user) // Pastikan layout sudah sesuai

        recyclerView = findViewById(R.id.recyclerViewFilmMingguIni)
        viewPagerCarousel = findViewById(R.id.viewPagerCarousel)

        // Memanggil setupRecyclerView di sini
        setupRecyclerView()
        loadFilmData()

        // Menambahkan listener untuk profileImage
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        profileImage.setOnClickListener {
            // Implementasikan logika yang diinginkan saat profileImage diklik di sini
            // Misalnya, navigasi ke halaman profil atau tampilkan dialog profil, dll.
            // Contoh: Navigasi ke halaman profil
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        sessionManager = SessionManager(this)
        val userDetails = sessionManager.getUserDetails()
        val username = userDetails[SessionManager.KEY_USERNAME]
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        usernameTextView.text = username
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Inisialisasi adapter karusel dengan daftar kosong pada awalnya
        carouselAdapter = HomeCarrousel(this)
        viewPagerCarousel.adapter = carouselAdapter
    }

    private fun loadFilmData() {
        val firestore = FirebaseFirestore.getInstance()
        val filmCollectionRef = firestore.collection("film")

        filmCollectionRef.get()
            .addOnSuccessListener { result ->
                val filmList = mutableListOf<Film>()

                for (document in result) {
                    val judul = document.getString("judul") ?: ""
                    val poster = document.getString("poster") ?: ""
                    val sinopsis = document.getString("sinopsis") ?: ""
                    val tahun = document.getString("tahun") ?: ""
                    val genre = document.getString("genre") ?: ""
                    val rating = document.getString("rating") ?: ""

                    filmList.add(
                        Film(
                            judul = judul,
                            poster = poster,
                            sinopsis = sinopsis,
                            tahun = tahun,
                            genre = genre,
                            rating = rating
                        )
                    )
                }

                val adapter = FilmMingguIniAdapter(filmList, this)
                recyclerView.adapter = adapter

                carouselAdapter.updateData(filmList)
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "com.example.prakmobileuas.UserActivity",
                    "Error getting film data",
                    exception
                )
            }
    }

    override fun onFilmItemClick(film: Film) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("film_judul", film.judul)
        intent.putExtra("film_sinopsis", film.sinopsis)
        intent.putExtra("film_tahun", film.tahun)
        intent.putExtra("film_genre", film.genre)
        intent.putExtra("film_rating", film.rating)
        intent.putExtra("film_poster", film.poster)

        // Menggunakan findViewById untuk mendapatkan referensi ke tampilan
        findViewById<TextView>(R.id.hometahun).text = film.tahun
        findViewById<TextView>(R.id.homerating).text = film.rating
        findViewById<TextView>(R.id.homegenre).text = film.genre

        startActivity(intent)
    }
}


