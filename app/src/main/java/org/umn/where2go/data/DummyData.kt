package org.umn.where2go.data

object DummyData {
    val places = listOf(
        Place(1, "Ruang Teduh Coffee", "Jl. Boulevard Raya No. 12, Gading Serpong", 0.4,
            Vibe.QUIET, 90, 60, true, 4.7, "07.00 - 22.00",
            "Kafe dua lantai dengan musik instrumental pelan. Pagi hari sangat sepi.",
            listOf("Wi-Fi cepat", "Stopkontak", "AC", "Mushola")),
        Place(2, "Nugas Corner", "Jl. Scientia Boulevard Blok B3", 0.9,
            Vibe.STUDY, 70, 100, true, 4.5, "08.00 - 23.00",
            "Meja panjang dan banyak colokan. Favorit mahasiswa saat minggu tugas.",
            listOf("Wi-Fi 100 Mbps", "Stopkontak", "Meja panjang", "Printer")),
        Place(3, "Langit Senja Cafe", "Jl. Gading Serpong Barat No. 7", 1.2,
            Vibe.LIVELY, 25, 25, false, 4.2, "10.00 - 24.00",
            "Rooftop dengan live music akustik pada akhir pekan.",
            listOf("Live music", "Area outdoor", "Parkir luas")),
        Place(4, "Kedai Bang Jago", "Jl. Raya Serpong KM 7", 0.7,
            Vibe.LIVELY, 15, 0, false, 4.0, "15.00 - 02.00",
            "Warung kopi ramai tempat nongkrong malam. Tidak ada Wi-Fi.",
            listOf("Harga murah", "Area merokok", "Parkir motor"))
        // TODO: tambahkan Place(5, ...) sampai Place(8, ...)
    )

    val reviews = listOf(
        Review(1, 1, "Alya", Vibe.QUIET, "Pagi jam 8 sepi banget, enak buat ngerjain laporan.", "2 jam lalu"),
        Review(2, 1, "Bagas", Vibe.STUDY, "Wi-Fi lancar, colokan banyak di lantai 2.", "Kemarin"),
        Review(3, 2, "Citra", Vibe.STUDY, "Penuh saat jam 4 sore, datang lebih awal ya.", "3 jam lalu"),
        Review(4, 3, "Dimas", Vibe.LIVELY, "Live music-nya seru, tapi susah fokus nugas.", "Kemarin")
    )

    val contributors = listOf(
        Contributor(1, "Alya Putri", "Legenda Tempat", 2450),
        Contributor(2, "Bagas Pratama", "Pemandu Kota", 1980),
        Contributor(3, "Citra Lestari", "Pemandu Kota", 1620),
        Contributor(4, "Dimas Anggara", "Penjelajah", 1240)
    )
}