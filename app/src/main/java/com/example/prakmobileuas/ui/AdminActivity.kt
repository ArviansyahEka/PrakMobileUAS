package com.example.prakmobileuas.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.prakmobileuas.R
import com.example.prakmobileuas.adapter.FilmMingguIniAdapter
import com.example.prakmobileuas.database.Film
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var tambahButton: Button
    private lateinit var filmList: MutableList<Film>
    private lateinit var adapter: ArrayAdapter<Film>

    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        listView = findViewById(R.id.listView)
        tambahButton = findViewById(R.id.btnTambah)
        filmList = mutableListOf()

        adapter = ArrayAdapter(
            this@AdminActivity,
            android.R.layout.simple_list_item_1,
            filmList
        )
        listView.adapter = adapter

        observeFilmChanges()

        tambahButton.setOnClickListener {
            // Buka TambahFilmActivity untuk menambahkan data
            startActivity(Intent(this, TambahFilmActivity::class.java))
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedFilm = filmList[position]
            // Handle the selected film, you can navigate to UpdateDeleteActivity with the selected film's ID
            val intent = Intent(this, TambahFilmActivity::class.java)
            intent.putExtra("film_judul", selectedFilm.judul)
            intent.putExtra("film_id", selectedFilm.id)
            intent.putExtra("film_sinopsis", selectedFilm.sinopsis)
            intent.putExtra("film_tanggal", selectedFilm.tahun)

            startActivity(intent)
        }
    }

    private fun observeFilmChanges() {
        filmCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            val films = snapshots?.toObjects(Film::class.java)
            if (films != null) {
                filmList.clear()
                filmList.addAll(films)
                adapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, AdminActivity::class.java)
        }
    }
}
