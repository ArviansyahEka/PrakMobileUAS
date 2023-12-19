// AdminFragment.kt
package com.example.prakmobileuas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prakmobileuas.adapter.AdminListViewAdapter
import com.example.prakmobileuas.adapter.RecyclerItemClickListener
import com.example.prakmobileuas.database.Film
import com.example.prakmobileuas.ui.TambahFilmActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tambahButton: Button
    private lateinit var filmList: MutableList<Film>
    private lateinit var filmAdapter: AdminListViewAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.AdminRecyclerView)
        tambahButton = view.findViewById(R.id.btnTambah)
        filmList = mutableListOf()

        // Inisialisasi RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter
        filmAdapter = AdminListViewAdapter(filmList)

        // Set adapter ke RecyclerView
        recyclerView.adapter = filmAdapter

        observeFilmChanges()

        tambahButton.setOnClickListener {
            startActivity(Intent(requireContext(), TambahFilmActivity::class.java))
        }

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(
                requireContext(),
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val selectedFilm = filmList[position]

                        val intent = Intent(requireContext(), TambahFilmActivity::class.java)
                        intent.putExtra("film_id", selectedFilm.id)
                        intent.putExtra("edit_mode", true)
                        startActivity(intent)
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                        // Handle long item click if needed
                    }
                })
        )
    }

    private fun observeFilmChanges() {
        filmCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("AdminFragment", "Error observing film changes", error)
                return@addSnapshotListener
            }

            // Menggunakan documents untuk mendapatkan daftar dokumen
            val films = snapshots?.documents?.mapNotNull { it.toObject(Film::class.java)?.copy(id = it.id) }
            if (films != null) {
                filmList.clear()
                filmList.addAll(films)
                filmAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.AdminRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter
        filmAdapter = AdminListViewAdapter(emptyList()) // Awalnya tanpa data

        // Set adapter ke RecyclerView
        recyclerView.adapter = filmAdapter

        return view
    }
}
