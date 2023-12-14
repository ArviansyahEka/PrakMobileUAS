// AdminFragment.kt
package com.example.prakmobileuas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.prakmobileuas.database.Film
import com.example.prakmobileuas.ui.AdminActivity
import com.example.prakmobileuas.ui.TambahFilmActivity
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var tambahButton: Button
    private lateinit var filmList: MutableList<Film>
    private lateinit var adapter: ArrayAdapter<Film>

    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listView)
        tambahButton = view.findViewById(R.id.btnTambah)
        filmList = mutableListOf()

        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            filmList
        )
        listView.adapter = adapter

        observeFilmChanges()

        tambahButton.setOnClickListener {
            startActivity(Intent(requireContext(), TambahFilmActivity::class.java))
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedFilm = filmList[position]

            val intent = Intent(requireContext(), TambahFilmActivity::class.java)
            intent.putExtra("film_id", selectedFilm.id)
            intent.putExtra("edit_mode", true)
            startActivity(intent)
        }

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
                adapter.notifyDataSetChanged()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        fun createIntent(context: Context): Intent {
            return Intent(context, AdminActivity::class.java)
        }
    }
}
