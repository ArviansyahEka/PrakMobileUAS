package com.example.prakmobileuas.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.prakmobileuas.R
import com.example.prakmobileuas.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDetailBinding
    private lateinit var judulTextView: TextView
    private lateinit var sinopsisTextView: TextView
    private lateinit var tahunTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var miniImageView: ImageView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            backbutton.setOnClickListener {
                onBackPressed()
            }
        }


        judulTextView = findViewById(R.id.textViewJudul)
        sinopsisTextView = findViewById(R.id.textViewSinopsis)
        tahunTextView = findViewById(R.id.textViewTahun)
        genreTextView = findViewById(R.id.textViewGenre)
        ratingTextView = findViewById(R.id.textViewRating)
        posterImageView = findViewById(R.id.imageViewDetail)
        miniImageView = findViewById(R.id.miniimage)

        // Mendapatkan data dari intent
        val judul = intent.getStringExtra("film_judul")
        val sinopsis = intent.getStringExtra("film_sinopsis")
        val tahun = intent.getStringExtra("film_tahun")
        val genre = intent.getStringExtra("film_genre")
        val rating = intent.getStringExtra("film_rating")
        val poster = intent.getStringExtra("film_poster")

        // Menampilkan data ke dalam UI
        judulTextView.text = judul
        sinopsisTextView.text = sinopsis
        tahunTextView.text = tahun
        genreTextView.text = genre
        ratingTextView.text = rating

        // Load image into ImageView using Glide
        Glide.with(this)
            .load(poster)
            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
            .error(R.drawable.error_image) // Error image if loading fails
            .centerCrop()
            .into(posterImageView)

        Glide.with(this)
            .load(poster)
            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
            .error(R.drawable.error_image) // Error image if loading fails
            .centerCrop()
            .into(miniImageView)

    }
}
