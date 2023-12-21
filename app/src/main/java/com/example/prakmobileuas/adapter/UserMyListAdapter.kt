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

// Define the interface for item click in MyList
interface MyListFilmItemClickListener {
    fun onMyListItemClick(film: Film)
}

class UserMyListAdapter(
    private val filmList: MutableList<Film>,
    private var myListFilmItemClickListener: MyListFilmItemClickListener
) : RecyclerView.Adapter<UserMyListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.usermylistimageview) // Change ID here
        val textFilmTitle: TextView = itemView.findViewById(R.id.textJudul)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usermylistview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = filmList[position]

        holder.textFilmTitle.text = film.judul
        Glide.with(holder.imageView)
            .load(film.poster)
            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
            .error(R.drawable.error_image) // Error image if loading fails
            .centerCrop()
            .into(holder.imageView)

        // Set click listener untuk item di RecyclerView
        holder.itemView.setOnClickListener {
            myListFilmItemClickListener.onMyListItemClick(film)
        }
    }

    fun setMyListFilmItemClickListener(listener: MyListFilmItemClickListener) {
        myListFilmItemClickListener = listener
    }

    fun addFilmToList(film: Film) {
        filmList.add(film)
        notifyItemInserted(filmList.size - 1)
    }

    override fun getItemCount(): Int {
        return filmList.size
    }
}
