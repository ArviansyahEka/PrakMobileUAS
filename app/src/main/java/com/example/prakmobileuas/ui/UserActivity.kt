package com.example.prakmobileuas.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.prakmobileuas.R
import com.example.prakmobileuas.adapter.FilmItemClickListener
import com.example.prakmobileuas.adapter.FilmMingguIniAdapter
import com.example.prakmobileuas.adapter.HomeCarrousel
import com.example.prakmobileuas.adapter.MyListFilmItemClickListener
import com.example.prakmobileuas.adapter.UserMyListAdapter
import com.example.prakmobileuas.database.Film
import com.example.prakmobileuas.main.SessionManager
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity(), MyListFilmItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPagerCarousel: ViewPager2
    private lateinit var carouselAdapter: HomeCarrousel
    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerViewListSaya: RecyclerView

    companion object {
        private const val TAG = "UserActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        recyclerView = findViewById(R.id.recyclerViewFilmMingguIni)
        recyclerViewListSaya = findViewById(R.id.recyclerViewListSaya)
        viewPagerCarousel = findViewById(R.id.viewPagerCarousel)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Set LayoutManager for recyclerViewListSaya
        recyclerViewListSaya.layoutManager = GridLayoutManager(this, 2)

        // Call loadFilmData here
        loadFilmData()

        // Add listener for profileImage
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        profileImage.setOnClickListener {
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

                val adapter = FilmMingguIniAdapter(filmList, object : FilmItemClickListener {
                    override fun onFilmItemClick(film: Film) {
                        // Handle item click in FilmMingguIniAdapter
                        val intent = Intent(this@UserActivity, DetailActivity::class.java)
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
                })

                recyclerView.adapter = adapter
                setupRecyclerView()

                val mylistadapter = UserMyListAdapter(filmList, this@UserActivity)
                recyclerViewListSaya.adapter = mylistadapter
                mylistadapter.setMyListFilmItemClickListener(this@UserActivity)

                carouselAdapter.updateData(filmList)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting film data", exception)
            }
    }

    override fun onMyListItemClick(film: Film) {
        // Handle item click in MyList
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

