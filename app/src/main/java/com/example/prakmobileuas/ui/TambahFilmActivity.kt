package com.example.prakmobileuas.ui

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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
    private lateinit var btnHapus: Button
    private lateinit var pickImageButton: Button
    private lateinit var imageView: ImageView
    private val PICK_IMAGE_REQUEST = 1

    private val firestore = FirebaseFirestore.getInstance()
    private val koleksiFilmRef = firestore.collection("film")

    private lateinit var selectedImageUri: Uri
    private lateinit var storageRef: StorageReference

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data ?: return@registerForActivityResult

                // Lakukan sesuatu dengan URI gambar yang dipilih, contoh: tampilkan gambar
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
        btnHapus = findViewById(R.id.btnHapus)
        pickImageButton = findViewById(R.id.pickImageButton)
        imageView = findViewById(R.id.imageView)

        storageRef = FirebaseStorage.getInstance().reference

        pickImageButton.setOnClickListener {
            pickImage()
        }

        if (intent.getBooleanExtra("edit_mode", false)) {
            val filmId = intent.getStringExtra("film_id")
            if (filmId != null) {
                // Jika dalam mode edit, dapatkan data film dari Firestore
                dapatkanDataFilm(filmId)
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
                    val filmYangDiedit = Film(
                        id = intent.getStringExtra("film_id") ?: return@setOnClickListener,
                        judul = judul,
                        sinopsis = sinopsis,
                        tahun = tahun,
                        genre = genre,
                        rating = rating,
                        poster = ""
                    )

                    editFilm(filmYangDiedit)
                } else {
                    // Jika bukan mode edit, jalankan proses upload gambar dan tambah film baru
                    unggahGambarKePenyimpanan(judul, sinopsis, tahun, genre, rating)
                    showNotification()
                }
            } else {
                Log.e("TambahFilmActivity", "Silakan pilih gambar")
            }
        }

        // Tambahkan fungsi hapus
        btnHapus.setOnClickListener {
            // Hapus film jika dalam mode edit
            if (intent.getBooleanExtra("edit_mode", false)) {
                val filmId = intent.getStringExtra("film_id")
                if (filmId != null) {
                    hapusFilm(filmId)
                }
            } else {
                // Tampilkan pesan bahwa hapus tidak dapat dilakukan karena bukan mode edit
                Log.e("TambahFilmActivity", "Tidak dapat menghapus dalam mode tambah")
            }
        }
    }
    private fun showNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "com.example.prakmobileuas.channelId"
        val channelName = "Film Channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Film Notification Channel"
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.filmixadminlogo)
            .setContentTitle("Penambahan Film Berhasil!")
            .setContentText("Film baru telah ditambahkan.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }


    private fun dapatkanDataFilm(idFilm: String) {
        koleksiFilmRef.document(idFilm).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Dokumen ditemukan, ambil data dan isi formulir
                    val film = documentSnapshot.toObject(Film::class.java)
                    if (film != null) {
                        isiFormDenganDataFilm(film)
                    }
                } else {
                    Log.e("TambahFilmActivity", "Dokumen tidak ditemukan untuk idFilm: $idFilm")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TambahFilmActivity", "Error mendapatkan data film", e)
            }
    }

    private fun isiFormDenganDataFilm(film: Film) {
        // Isikan formulir dengan data film yang ditemukan
        edtJudul.setText(film.judul)
        edtSinopsis.setText(film.sinopsis)
        edtTahun.setText(film.tahun)
        edtGenre.setText(film.genre)
        edtRating.setText(film.rating)

        // TODO: Tambahkan logika untuk menampilkan gambar poster jika diperlukan
    }
    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun unggahGambarKePenyimpanan(judul: String, sinopsis: String, tahun: String, genre: String, rating: String) {
        val idFilm = generateFilmId() // Generate a unique ID for the film
        val imageRef = storageRef.child("posters/$idFilm.jpg")

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener {
                // Gambar berhasil diunggah, dapatkan URL gambar dari Penyimpanan
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Dapatkan URI gambar dan simpan ke Firestore atau lakukan apa yang diperlukan
                    val urlGambar = uri.toString()

                    // Simpan data film ke Firestore
                    val film = Film(
                        id = idFilm,
                        judul = judul,
                        sinopsis = sinopsis,
                        tahun = tahun,
                        genre = genre,
                        rating = rating,
                        poster = urlGambar
                    )

                    tambahFilm(film)
                }
            }
            .addOnFailureListener {
                // Penanganan kesalahan unggah
                Log.e("TambahFilmActivity", "Gagal mengunggah gambar ke penyimpanan", it)
            }
    }

    private fun tambahFilm(film: Film) {
        koleksiFilmRef.add(film)
            .addOnSuccessListener { documentReference ->
                Log.d("TambahFilmActivity", "Film berhasil ditambahkan dengan ID: ${documentReference.id}")
                navigasiKeActivityAdmin()
            }
            .addOnFailureListener { e ->
                Log.e("TambahFilmActivity", "Error menambahkan film", e)
            }
    }

    private fun editFilm(film: Film) {
        val idFilm = film.id

        Log.d("TambahFilmActivity", "Mengedit film dengan ID: $idFilm")

        val dataFilm = mapOf(
            "judul" to film.judul,
            "sinopsis" to film.sinopsis,
            "tahun" to film.tahun,
            "genre" to film.genre,
            "rating" to film.rating,
            "poster" to film.poster
            // tambahkan properti lain sesuai kebutuhan
        )

        koleksiFilmRef.document(idFilm)
            .update(dataFilm)
            .addOnSuccessListener {
                Log.d("TambahFilmActivity", "Film berhasil diperbarui dengan ID: $idFilm")
                navigasiKeActivityAdmin()
            }
            .addOnFailureListener { e ->
                Log.e("TambahFilmActivity", "Error memperbarui film dengan ID: $idFilm", e)
            }
    }

    private fun hapusFilm(idFilm: String) {
        koleksiFilmRef.document(idFilm)
            .delete()
            .addOnSuccessListener {
                Log.d("TambahFilmActivity", "Film berhasil dihapus dengan ID: $idFilm")
                navigasiKeActivityAdmin()
            }
            .addOnFailureListener { e ->
                Log.e("TambahFilmActivity", "Error menghapus film dengan ID: $idFilm", e)
            }
    }

    private fun navigasiKeActivityAdmin() {
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun generateFilmId(): String {
        // Implementasikan logika Anda untuk menghasilkan ID film yang unik (Anda dapat menggunakan timestamp, UUID, dll.)
        // Untuk sederhananya, saya menggunakan timestamp dalam milidetik
        return System.currentTimeMillis().toString()
    }
}
