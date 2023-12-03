// HomeCarrousel.kt

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

class HomeCarrousel(private val context: Context, private var films: List<Film>) :
    RecyclerView.Adapter<HomeCarrousel.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carouselImage: ImageView = itemView.findViewById(R.id.carouselImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_carrousel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load the image into the ImageView using Glide
        Glide.with(context)
            .load(films[position].poster)
            .centerCrop()
            .into(holder.carouselImage)

        // Set text judul, tahun, rating, dan genre
        val judulTextView: TextView = holder.itemView.findViewById(R.id.judulTextView)
        val tahunTextView: TextView = holder.itemView.findViewById(R.id.hometahun)
        val ratingTextView: TextView = holder.itemView.findViewById(R.id.homerating)
        val genreTextView: TextView = holder.itemView.findViewById(R.id.homegenre)

        judulTextView.text = films[position].judul
        tahunTextView.text = films[position].tahun
        ratingTextView.text = films[position].rating
        genreTextView.text = films[position].genre
    }


    override fun getItemCount(): Int {
        return films.size
    }

    fun updateData(films: List<Film>) {
        this.films = films
        notifyDataSetChanged()
    }
}
