package com.example.prakmobileuas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.prakmobileuas.adapter.FilmItemClickListener
import com.example.prakmobileuas.adapter.FilmMingguIniAdapter
import com.example.prakmobileuas.adapter.HomeCarrousel
import com.example.prakmobileuas.database.Film
import com.example.prakmobileuas.ui.DetailActivity
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserFragment : Fragment(), FilmItemClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPagerCarousel: ViewPager2
    private lateinit var carouselAdapter: HomeCarrousel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFilmMingguIni)
        viewPagerCarousel = view.findViewById(R.id.viewPagerCarousel)
        setupRecyclerView()
        loadFilmData()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        carouselAdapter = HomeCarrousel(requireContext(), listOf())
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

                    filmList.add(Film(judul = judul, poster = poster, sinopsis = sinopsis, tahun = tahun, genre = genre, rating = rating))
                }

                val adapter = FilmMingguIniAdapter(filmList, this)
                recyclerView.adapter = adapter

                carouselAdapter.updateData(filmList)
            }
            .addOnFailureListener { exception ->
                Log.e("UserFragment", "Error getting film data", exception)
            }
    }

    override fun onFilmItemClick(film: Film) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("film_judul", film.judul)
        intent.putExtra("film_sinopsis", film.sinopsis)
        intent.putExtra("film_tahun", film.tahun)
        intent.putExtra("film_genre", film.genre)
        intent.putExtra("film_rating", film.rating)
        intent.putExtra("film_poster", film.poster)

        view?.findViewById<TextView>(R.id.hometahun)?.text = film.tahun
        view?.findViewById<TextView>(R.id.homerating)?.text = film.rating
        view?.findViewById<TextView>(R.id.homegenre)?.text = film.genre

        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
