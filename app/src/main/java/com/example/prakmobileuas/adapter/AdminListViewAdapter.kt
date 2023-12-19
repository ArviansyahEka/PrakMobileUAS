// FilmAdapter.kt
package com.example.prakmobileuas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prakmobileuas.R
import com.example.prakmobileuas.database.Film

class AdminListViewAdapter(private val films: List<Film>) : RecyclerView.Adapter<AdminListViewAdapter.FilmViewHolder>() {

    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePoster: ImageView = itemView.findViewById(R.id.imagePoster)
        val textFilmId: TextView = itemView.findViewById(R.id.textFilmId)
        val textFilmTitle: TextView = itemView.findViewById(R.id.textFilmTitle)
        val textFilmSynopsis: TextView = itemView.findViewById(R.id.textFilmSynopsis)
        val textFilmGenre: TextView = itemView.findViewById(R.id.textFilmGenre)
        val textFilmRating: TextView = itemView.findViewById(R.id.textFilmRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_film_view, parent, false)
        return FilmViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val currentFilm = films[position]

        // Set data ke tampilan
        holder.textFilmId.text = "ID: ${currentFilm.id}"
        holder.textFilmTitle.text = currentFilm.judul
        holder.textFilmSynopsis.text = currentFilm.sinopsis
        holder.textFilmGenre.text = "Genre: ${currentFilm.genre}"
        holder.textFilmRating.text = "Rating: ${currentFilm.rating}"

        // Load image into ImageView using Glide
        Glide.with(holder.imagePoster)
            .load(currentFilm.poster)
            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
            .error(R.drawable.error_image) // Error image if loading fails
            .centerCrop()
            .into(holder.imagePoster)
    }

    override fun getItemCount(): Int {
        return films.size
    }
}
