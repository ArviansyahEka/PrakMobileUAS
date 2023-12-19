// UserMyListAdapter.kt
package com.example.prakmobileuas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prakmobileuas.R
import com.example.prakmobileuas.database.Film

class UserMyListAdapter(private val filmList: List<Film>) :
    RecyclerView.Adapter<UserMyListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.usermylistimageview)
        val textJudul: TextView = itemView.findViewById(R.id.textJudul)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.usermylistview, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = filmList[position]
        // Set informasi film ke tampilan di sini
        holder.imageView.setImageResource(R.drawable.placeholder_image)
        holder.textJudul.text = currentItem.judul
    }

    override fun getItemCount(): Int {
        return filmList.size
    }
}
