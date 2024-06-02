# LyricFinder

LyricFinder adalah aplikasi Android yang memungkinkan pengguna mencari lirik lagu favorit mereka. Aplikasi ini menyediakan fitur untuk mencari lagu berdasarkan artis, judul lagu, dan menampilkan lirik secara detail. Selain itu, pengguna dapat melihat profil dan daftar favorit mereka.

## Fitur

- **Pencarian Lagu** : Cari lagu berdasarkan nama Artis dan Judul Lagu
- **Lirik Lengkap** : Tampilan Lirik lagu secara lengkap dengan format yang rapi
- **Lagu Favorite** : Melihat daftar lagu favorite
- **Navigasi Mudah** : Menggunakan bottom navbar untuk berpindah antara Fragment

## Struktur Aplikasi

### Aktivity Utama
- **Main Activity** : Aktivity utama yang mengelola navigasi antara berbagai fragment menggunakan _**MeowBottomNavigation**_

### Fragment
- **SongFragment** : Menampilkan daftar lagu yang tersedia
- **ArtistFragment** : Menampilkan daftar artis
- **SearchFragment** : Menyediakan fitur pencarian lagu berdasarkan nama artis atau judul lagu
- **ProfilFragment** : Menampilkan UI pengguna dan daftar lagu favorite

### Activity Lirik Lagu
- **SongLyricsActivity** : Menampilkan lirik lagu secara lengkap. Mengambil data lirik dari API berdasarkan id

## Teknologi
- **Java** : untuk pengembangan Aplikasi
- **Retrofit** : untuk pengambilan API
- **Lottie** : untuk tampilan progressBar lebih menarik
- **MeowBottomNavigation** : untuk tampilan navigasi yang lebih menarik
- **Glide** : memuat gambar dari URL jaringan / API

## Integrasi API
[Genius Lyrics](https://rapidapi.com/Glavier/api/genius-song-lyrics1/playground/)
 
