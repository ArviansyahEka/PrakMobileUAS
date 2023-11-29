// TambahFilmActivity.kt
package com.example.prakmobileuas.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.prakmobileuas.R
import com.example.prakmobileuas.database.Film
import com.google.firebase.firestore.FirebaseFirestore

class TambahFilmActivity : AppCompatActivity() {

    private lateinit var edtJudul: EditText
    private lateinit var edtSinopsis: EditText
    private lateinit var edtTahun: EditText
    private lateinit var edtGenre: EditText
    private lateinit var edtRating: EditText
    private lateinit var edtPoster: EditText
    private lateinit var btnSimpan: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_film)

        edtJudul = findViewById(R.id.edtJudul)
        edtSinopsis = findViewById(R.id.edtSinopsis)
        edtTahun = findViewById(R.id.edtTahun)
        edtGenre = findViewById(R.id.edtGenre)
        edtRating = findViewById(R.id.edtRating)
        edtPoster = findViewById(R.id.edtPoster)
        btnSimpan = findViewById(R.id.btnSimpan)

        btnSimpan.setOnClickListener {
            val judul = edtJudul.text.toString()
            val sinopsis = edtSinopsis.text.toString()
            val tahun = edtTahun.text.toString()
            val genre = edtGenre.text.toString()
            val rating = edtRating.text.toString()
            val poster = edtPoster.text.toString()

            val film = Film(judul = judul, genre = genre, tahun = tahun, rating = rating, sinopsis = sinopsis, poster = poster)

            tambahFilm(film)

            // Kembali ke ListActivity setelah menyimpan
            startActivity(AdminActivity.createIntent(this))
        }
    }

    private fun tambahFilm(film: Film) {
        // Use judul as the document ID
        filmCollectionRef.add(film).addOnSuccessListener { documentReference ->
            val createdFilmId = documentReference.id
            // Create a new Pengaduan object with the updated id
            val updatedFilm = Film(
                id = createdFilmId,
                judul = film.judul,
                genre = film.genre,
                tahun = film.tahun,
                rating = film.rating,
                sinopsis = film.sinopsis,
                poster = film.poster
            )

            documentReference.set(updatedFilm)
                .addOnSuccessListener {
                    Log.d("TambahFilmActivity", "Film successfully added!")
                    navigateToListActivity()
                }
                .addOnFailureListener { e ->
                    Log.e("TambahFilmActivity", "Error adding film", e)
                }
        }
    }

    private fun navigateToListActivity() {
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}
