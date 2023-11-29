package com.example.prakmobileuas.database
data class Film(
    val id: String = "",
    val judul: String = "",
    val genre: String = "",
    val tahun: String = "",
    val rating: String = "",
    val sinopsis: String = "",
    val poster: String = "" // Properti baru untuk menyimpan URL gambar
)
