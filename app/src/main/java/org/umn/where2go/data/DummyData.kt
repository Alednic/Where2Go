package org.umn.where2go.data

import org.umn.where2go.R
object DummyData {
    val places = listOf(
        Place(1, "Kopi Kenangan", "BSD City, Tangerang Selatan", 0.25, Vibe.QUIET, 90, 60, true, 4.7, "07.00 - 22.00",
            "Kafe dua lantai dengan musik instrumental pelan. Pagi hari sangat sepi, cocok untuk membaca dan menulis.",
            listOf("Wi-Fi cepat", "Stopkontak", "AC", "Mushola"),
            checkIns = 248, minOrderK = 12,
            tags = listOf("Tenang", "Free wifi", "Good vibes", "Focus", "Affordable"),
            peakHours = listOf(35, 22, 30, 30, 38, 45, 78, 62),
            dayparts = listOf(DayPart("Pagi", Vibe.QUIET), DayPart("Siang", Vibe.LIVELY), DayPart("Malam", Vibe.STUDY)),
            suitability = Suitability(82, 64, 43),
            culinary = listOf(
                Culinary("Kopi Kenangan Mantan", "Kopi susu gula aren, favorit pengunjung", 22000),
                Culinary("Roti Bakar Cokelat", "Disajikan hangat, cocok menemani nugas", 18000)
            ),
            cardTags = listOf("Colokan", "Wifi Kencang", "AC Dingin"),
            imageRes = R.drawable.kopi_kenangan
        ),
        Place(2, "Eat & Drink", "Gading Serpong, Tangerang", 0.75, Vibe.STUDY, 70, 100, true, 4.5, "08.00 - 23.00",
            "Meja panjang dan banyak colokan. Favorit mahasiswa saat minggu tugas dan menjelang ujian.",
            listOf("Wi-Fi 100 Mbps", "Stopkontak", "Meja panjang"),
            checkIns = 312, minOrderK = 20,
            tags = listOf("Nugas", "Free wifi", "Focus"),
            peakHours = listOf(25, 30, 45, 50, 60, 55, 40, 30),
            dayparts = listOf(DayPart("Pagi", Vibe.STUDY), DayPart("Siang", Vibe.STUDY), DayPart("Malam", Vibe.LIVELY)),
            suitability = Suitability(90, 45, 70),
            culinary = listOf(Culinary("Nasi Ayam Sambal Matah", "Porsi besar, harga mahasiswa", 25000)),
            cardTags = listOf("Murah", "Enak", "Cozy", "Porsi Besar"),
            imageRes = R.drawable.eat_and_drink
        ),
        Place(3, "Sunset Corner", "Jl. Gading Serpong Barat No. 7", 0.9, Vibe.LIVELY, 25, 25, false, 4.2, "10.00 - 24.00",
            "Rooftop dengan live music akustik pada akhir pekan. Ramai saat sore dan malam.",
            listOf("Live music", "Area outdoor", "Parkir luas"),
            checkIns = 187, minOrderK = 35,
            tags = listOf("Ramai", "Good vibes"),
            peakHours = listOf(10, 12, 18, 25, 45, 70, 95, 85),
            dayparts = listOf(DayPart("Pagi", Vibe.QUIET), DayPart("Siang", Vibe.QUIET), DayPart("Malam", Vibe.LIVELY)),
            suitability = Suitability(25, 92, 35),
            culinary = listOf(Culinary("Es Kopi Senja", "Signature drink, paling laris", 28000)),
            cardTags = listOf("Sunset", "Live Music", "Outdoor"),
            imageRes = R.drawable.sunset_corner
        ),
        Place(4, "Pojok Baca Library Cafe", "Jl. Pondok Jagung Raya No. 3", 1.6, Vibe.QUIET, 95, 40, true, 4.8, "09.00 - 21.00",
            "Perpustakaan mini yang digabung dengan kafe. Dilarang bicara keras, sangat cocok untuk fokus.",
            listOf("Area hening", "Stopkontak", "Koleksi buku", "AC"),
            checkIns = 156, minOrderK = 18,
            tags = listOf("Tenang", "Focus", "Free wifi"),
            peakHours = listOf(20, 28, 35, 30, 32, 38, 30, 15),
            suitability = Suitability(95, 30, 55),
            culinary = listOf(Culinary("Matcha Latte", "Manis lembut, cocok menemani membaca", 24000)),
            cardTags = listOf("Hening", "Banyak Buku", "Nyaman"),
            imageRes = R.drawable.pojok_baca_library_cafe
        ),
        Place(5, "Hub Coworking Space", "Ruko Paramount Blok C5", 2.1, Vibe.STUDY, 60, 150, true, 4.6, "24 jam",
            "Ruang kerja bersama dengan ruang rapat kecil. Internet sangat stabil.",
            listOf("Wi-Fi 150 Mbps", "Ruang rapat", "Stopkontak", "Loker", "24 jam"),
            checkIns = 421, minOrderK = 50,
            tags = listOf("Nugas", "Free wifi", "Focus"),
            peakHours = listOf(40, 55, 70, 65, 60, 50, 35, 25),
            suitability = Suitability(88, 35, 95),
            culinary = listOf(Culinary("Americano Double Shot", "Kopi hitam pekat penghilang kantuk", 20000)),
            cardTags = listOf("Buka 24 Jam", "Wifi Cepat", "Ruang Rapat"),
            imageRes = R.drawable.hub_coworking_space
        ),
        Place(6, "Kedai Bang Jago", "Jl. Raya Serpong KM 7", 1.4, Vibe.LIVELY, 15, 0, false, 4.0, "15.00 - 02.00",
            "Warung kopi ramai tempat nongkrong malam. Tidak ada Wi-Fi, tapi harganya sangat bersahabat.",
            listOf("Harga murah", "Area merokok", "Parkir motor"),
            checkIns = 98, minOrderK = 8,
            tags = listOf("Ramai", "Affordable"),
            peakHours = listOf(0, 0, 5, 10, 25, 45, 85, 100),
            suitability = Suitability(10, 95, 20),
            culinary = listOf(Culinary("Kopi Hitam Tubruk", "Kopi lokal khas, nikmat dan murah", 8000)),
            cardTags = listOf("Murah Meriah", "Nongkrong Malam", "Outdoor"),
            imageRes = R.drawable.kedai_bang_jago
        ),
        Place(7, "Taman Sudut Park", "Jl. Taman Sari Blok A", 2.8, Vibe.LIVELY, 35, 0, false, 4.1, "05.00 - 21.00",
            "Taman kota dengan area jogging dan kursi santai. Ramai pada pagi dan sore hari.",
            listOf("Area outdoor", "Jogging track", "Gratis"),
            checkIns = 134, minOrderK = 0,
            tags = listOf("Ramai", "Affordable", "Good vibes"),
            peakHours = listOf(80, 60, 30, 20, 40, 75, 60, 20),
            suitability = Suitability(15, 85, 25),
            cardTags = listOf("Area Luas", "Jogging Track", "Gratis"),
            imageRes = R.drawable.taman_sudut_park
        ),
        Place(8, "Kedai Hening", "Jl. Melati No. 21, Tangerang", 3.4, Vibe.QUIET, 85, 35, true, 4.4, "08.00 - 20.00",
            "Kedai kecil bernuansa kayu. Pemiliknya menerapkan aturan telepon dalam mode senyap.",
            listOf("Stopkontak", "Mode senyap", "Teh & kopi lokal"),
            checkIns = 76, minOrderK = 15,
            tags = listOf("Tenang", "Focus"),
            suitability = Suitability(78, 40, 30),
            culinary = listOf(Culinary("Teh Chamomile", "Teh herbal hangat menenangkan", 15000)),
            cardTags = listOf("Mode Senyap", "Teh Herbal", "Private"),
            imageRes = R.drawable.kedai_hening
        )
    )

    val reviews = listOf(
        Review(1, 1, "Alya", Vibe.QUIET, "Pagi jam 8 sepi banget, enak buat ngerjain laporan.", "2 jam lalu"),
        Review(2, 1, "Bagas", Vibe.STUDY, "Wi-Fi lancar, colokan banyak di lantai 2.", "Kemarin"),
        Review(3, 2, "Citra", Vibe.STUDY, "Penuh saat jam 4 sore, datang lebih awal ya.", "3 jam lalu"),
        Review(4, 3, "Dimas", Vibe.LIVELY, "Live music-nya seru, tapi susah fokus nugas.", "Kemarin"),
        Review(5, 4, "Eka", Vibe.QUIET, "Paling tenang di area sini, sangat direkomendasikan.", "5 jam lalu"),
        Review(6, 5, "Farah", Vibe.STUDY, "Internet kencang, cocok untuk meeting online.", "2 hari lalu"),
        Review(7, 6, "Gilang", Vibe.LIVELY, "Rame banget kalau malam minggu, tapi asik.", "Kemarin")
    )

    val contributors = listOf(
        Contributor(1, "Alicia", "Lv. 9 Master Tagger", 500, R.drawable.avatar_alicia),
        Contributor(2, "Michelle", "Lv. 8 City Guide", 412, R.drawable.avatar_michelle),
        Contributor(3, "Budi", "Lv. 8 City Guide", 389, R.drawable.avatar_budi),
        Contributor(4, "Kevin", "Lv. 7 Local Guide", 255, R.drawable.avatar_kevin),
        Contributor(5, "Daniel", "Lv. 5 Tracker", 238, R.drawable.avatar_daniel),
        Contributor(6, "Aisyah", "Lv. 2 Explorer", 222, R.drawable.avatar_aisyah)
    )

    val currentUser = Contributor(
        rank = 12,
        name = "Hanna (You)",
        level = "Lv. 7 Local Guide",
        tags = 178,
    )
    val topPlaces = listOf(
        TopPlace(1, "Kopi Kenangan BSD", "BSD City", 248, R.drawable.kopi_kenangan),
        TopPlace(2, "Working Space CO", "Serpong", 194, R.drawable.working_space_co),
        TopPlace(3, "Warung Bu Ani", "Gading Serpong", 167, R.drawable.warung_bu_ani),
        TopPlace(4, "Janji Jiwa BSD", "BSD City", 143, R.drawable.janji_jiwa_bsd),
        TopPlace(5, "Taman Kota BSD", "BSD City", 134, R.drawable.taman_kota_bsd),
        TopPlace(6, "Kopi Nusantara BSD", "BSD City", 121, imageRes = R.drawable.kopi_nusantara_bsd)
    )
}