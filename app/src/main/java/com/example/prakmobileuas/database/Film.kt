package com.example.prakmobileuas.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Film(
    @PrimaryKey val id: String = "",
    val judul: String = "",
    val sinopsis: String = "",
    val tahun: String = "",
    val genre: String = "",
    val rating: String = "",
    val poster: String = ""
)
