package com.example.prakmobileuas.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.prakmobileuas.R
import com.example.prakmobileuas.database.Film
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class TambahFilmActivity : AppCompatActivity() {

    private lateinit var edtJudul: EditText
    private lateinit var edtSinopsis: EditText
    private lateinit var edtTahun: EditText
    private lateinit var edtGenre: EditText
    private lateinit var edtRating: EditText
    private lateinit var btnSimpan: Button
    private lateinit var pickImageButton: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")

    private lateinit var selectedImageUri: Uri
    private lateinit var storageRef: StorageReference

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data ?: return@registerForActivityResult

                // Lakukan sesuatu dengan URI gambar yang dipilih, contoh: tampilkan gambar
                 val imageView = findViewById<ImageView>(R.id.imageView)
                 imageView.setImageURI(selectedImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_film)

        edtJudul = findViewById(R.id.edtJudul)
        edtSinopsis = findViewById(R.id.edtSinopsis)
        edtTahun = findViewById(R.id.edtTahun)
        edtGenre = findViewById(R.id.edtGenre)
        edtRating = findViewById(R.id.edtRating)
        btnSimpan = findViewById(R.id.btnSimpan)
        pickImageButton = findViewById(R.id.pickImageButton)

        storageRef = FirebaseStorage.getInstance().reference

        pickImageButton.setOnClickListener {
            pickImage()
        }

        if (intent.getBooleanExtra("edit_mode", false)) {
            val filmId = intent.getStringExtra("film_id")
            if (filmId != null) {
                // Jika dalam mode edit, dapatkan data film dari Firestore
                getFilmData(filmId)
            }
        }

        btnSimpan.setOnClickListener {
            // Mendapatkan data dari input
            val judul = edtJudul.text.toString()
            val sinopsis = edtSinopsis.text.toString()
            val tahun = edtTahun.text.toString()
            val genre = edtGenre.text.toString()
            val rating = edtRating.text.toString()

            // Memastikan gambar sudah dipilih sebelum menyimpan
            if (::selectedImageUri.isInitialized) {
                if (intent.getBooleanExtra("edit_mode", false)) {
                    // Jika dalam mode edit, buat objek Film dari data yang diedit dan panggil fungsi editFilm
                    val editedFilm = Film(
                        id = intent.getStringExtra("film_id") ?: return@setOnClickListener,
                        judul = judul,
                        sinopsis = sinopsis,
                        tahun = tahun,
                        genre = genre,
                        rating = rating,
                        poster = ""
                    )

                    editFilm(editedFilm)
                } else {
                    // Jika bukan mode edit, jalankan proses upload gambar dan tambah film baru
                    uploadImageToStorage(judul, sinopsis, tahun, genre, rating)
                }
            } else {
                Log.e("TambahFilmActivity", "Please pick an image")
            }
        }
    }

    private fun getFilmData(filmId: String) {
        filmCollectionRef.document(filmId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Dokumen ditemukan, ambil data dan isi formulir
                    val film = documentSnapshot.toObject(Film::class.java)
                    if (film != null) {
                        fillFormWithFilmData(film)
                    }
                } else {
                    Log.e("TambahFilmActivity", "Document not found for filmId: $filmId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TambahFilmActivity", "Error getting film data", e)
            }
    }

    private fun fillFormWithFilmData(film: Film) {
        // Isikan formulir dengan data film yang ditemukan
        edtJudul.setText(film.judul)
        edtSinopsis.setText(film.sinopsis)
        edtTahun.setText(film.tahun)
        edtGenre.setText(film.genre)
        edtRating.setText(film.rating)

        // TODO: Tambahkan logika untuk menampilkan gambar poster jika diperlukan
    }

    private fun pickImage() {
        val pickImage = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(pickImage)
    }

    private fun uploadImageToStorage(judul: String, sinopsis: String, tahun: String, genre: String, rating: String) {
        val filmId = generateFilmId() // Generate a unique ID for the film
        val imageRef = storageRef.child("posters/$filmId.jpg")

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener {
                // Gambar berhasil diunggah, dapatkan URL gambar dari Storage
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Dapatkan URI gambar dan simpan ke Firestore atau lakukan apa yang diperlukan
                    val imageUrl = uri.toString()

                    // Simpan data film ke Firestore
                    val film = Film(
                        id = filmId,
                        judul = judul,
                        sinopsis = sinopsis,
                        tahun = tahun,
                        genre = genre,
                        rating = rating,
                        poster = imageUrl
                    )

                    tambahFilm(film)
                }
            }
            .addOnFailureListener {
                // Penanganan kesalahan unggah
                Log.e("TambahFilmActivity", "Failed to upload image to storage", it)
            }
    }

    private fun tambahFilm(film: Film) {
        filmCollectionRef.add(film)
            .addOnSuccessListener { documentReference ->
                Log.d("TambahFilmActivity", "Film successfully added with ID: ${documentReference.id}")
                navigateToAdminActivity()
            }
            .addOnFailureListener { e ->
                Log.e("TambahFilmActivity", "Error adding film", e)
            }
    }

    private fun editFilm(film: Film) {
        val filmId = film.id

        Log.d("TambahFilmActivity", "Editing film with ID: $filmId")

        val filmData = mapOf(
            "judul" to film.judul,
            "sinopsis" to film.sinopsis,
            "tahun" to film.tahun,
            "genre" to film.genre,
            "rating" to film.rating,
            "poster" to film.poster
            // tambahkan properti lain sesuai kebutuhan
        )

        filmCollectionRef.document(filmId)
            .update(filmData)
            .addOnSuccessListener {
                Log.d("TambahFilmActivity", "Film successfully updated with ID: $filmId")
                navigateToAdminActivity()
            }
            .addOnFailureListener { e ->
                Log.e("TambahFilmActivity", "Error updating film with ID: $filmId", e)
            }
    }

    private fun navigateToAdminActivity() {
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun generateFilmId(): String {
        // Implement your logic to generate a unique film ID (you can use timestamp, UUID, etc.)
        // For simplicity, I'm using a timestamp in milliseconds
        return System.currentTimeMillis().toString()
    }
}
