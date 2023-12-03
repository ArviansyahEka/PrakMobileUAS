// UserActivity.kt

package com.example.prakmobileuas.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity(), FilmItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPagerCarousel: ViewPager2
    private lateinit var carouselAdapter: HomeCarrousel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        recyclerView = findViewById(R.id.recyclerViewFilmMingguIni)
        viewPagerCarousel = findViewById(R.id.viewPagerCarousel) // Initialize viewPagerCarousel
        setupRecyclerView()
        loadFilmData()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize the carousel adapter with an empty list initially
        carouselAdapter = HomeCarrousel(this, listOf()) // Pass an empty list for now
        viewPagerCarousel.adapter = carouselAdapter
    }

    private fun loadFilmData() {
        // Gantilah dengan kode yang sesuai untuk mengambil data dari Firebase Firestore
        // Misalnya, dengan menggunakan collection("films") untuk mendapatkan daftar film
        // (pastikan sudah ada data di Firestore dengan struktur yang sesuai)
        val firestore = FirebaseFirestore.getInstance()
        val filmCollectionRef = firestore.collection("film")

        // Mendapatkan data film dari Firestore
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

                    // Tambahkan objek Film ke dalam daftar
                    filmList.add(Film(judul = judul, poster = poster, sinopsis = sinopsis, tahun = tahun, genre = genre, rating = rating))
                }

                // Inisialisasi dan atur adapter setelah mendapatkan data
                val adapter = FilmMingguIniAdapter(filmList, this)
                recyclerView.adapter = adapter

                // Update carousel adapter with film posters' URLs
                carouselAdapter.updateData(filmList)
            }
            .addOnFailureListener { exception ->
                // Handle kegagalan saat mengambil data dari Firestore
                Log.e("UserActivity", "Error getting film data", exception)
            }
    }

        override fun onFilmItemClick(film: Film) {
            // Tanggapan ketika item di RecyclerView diklik
            // Buka DetailActivity dengan data film yang dipilih
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("film_judul", film.judul)
            intent.putExtra("film_sinopsis", film.sinopsis)
            intent.putExtra("film_tahun", film.tahun)
            intent.putExtra("film_genre", film.genre)
            intent.putExtra("film_rating", film.rating)
            intent.putExtra("film_poster", film.poster)

            findViewById<TextView>(R.id.hometahun).text = film.tahun
            findViewById<TextView>(R.id.homerating).text = film.rating
            findViewById<TextView>(R.id.homegenre).text = film.genre

        startActivity(intent)
    }
}
