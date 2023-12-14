package com.example.prakmobileuas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prakmobileuas.R
import com.example.prakmobileuas.database.Film

class HomeCarrousel(private val context: Context) :
    RecyclerView.Adapter<HomeCarrousel.ViewHolder>() {

    private val filmList = mutableListOf<Film>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carouselImage: ImageView = itemView.findViewById(R.id.carouselImage)
        val judulTextView: TextView = itemView.findViewById(R.id.judulTextView)
        val tahunTextView: TextView = itemView.findViewById(R.id.hometahun)
        val ratingTextView: TextView = itemView.findViewById(R.id.homerating)
        val genreTextView: TextView = itemView.findViewById(R.id.homegenre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_carrousel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = filmList[position]

        // Load image into ImageView using Glide
        Glide.with(holder.carouselImage)
            .load(film.poster)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .centerCrop()
            .into(holder.carouselImage)

        holder.judulTextView.text = film.judul
        holder.tahunTextView.text = film.tahun
        holder.ratingTextView.text = film.rating
        holder.genreTextView.text = film.genre
    }

    fun updateData(newFilmList: List<Film>) {
        filmList.clear()
        filmList.addAll(newFilmList)
        notifyDataSetChanged()
    }

    fun addFilmToCarousel(film: Film) {
        filmList.add(film)
        notifyItemInserted(filmList.size - 1)
    }

    override fun getItemCount(): Int {
        return filmList.size
    }
}
