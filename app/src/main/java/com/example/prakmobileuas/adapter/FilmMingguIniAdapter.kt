package com.example.prakmobileuas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prakmobileuas.R
import com.example.prakmobileuas.database.Film

interface FilmItemClickListener {
    fun onFilmItemClick(film: Film)
}

class FilmMingguIniAdapter(private val filmList: MutableList<Film>, private val itemClickListener: FilmItemClickListener) :
    RecyclerView.Adapter<FilmMingguIniAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewFilmMingguIni)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_minggu_ini, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = filmList[position]

        // Load image into ImageView using Glide
        Glide.with(holder.imageView)
            .load(film.poster)
            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
            .error(R.drawable.error_image) // Error image if loading fails
            .centerCrop()
            .into(holder.imageView)

        // Set click listener untuk item di RecyclerView
        holder.itemView.setOnClickListener {
            itemClickListener.onFilmItemClick(film)
        }
    }

    fun addFilmToList(film: Film) {
        filmList.add(film)
        notifyItemInserted(filmList.size - 1)
    }

    override fun getItemCount(): Int {
        return filmList.size
    }
}
