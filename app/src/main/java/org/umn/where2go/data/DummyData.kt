package org.umn.where2go.data

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
            )
        ),
        Place(2, "Eat & Drink", "Gading Serpong, Tangerang", 0.9, Vibe.STUDY, 70, 100, true, 4.5, "08.00 - 23.00",
            "Meja panjang dan banyak colokan. Favorit mahasiswa saat minggu tugas dan menjelang ujian.",
            listOf("Wi-Fi 100 Mbps", "Stopkontak", "Meja panjang", "Printer"),
            checkIns = 312, minOrderK = 20,
            tags = listOf("Nugas", "Free wifi", "Focus"),
            peakHours = listOf(25, 30, 45, 50, 60, 55, 40, 30),
            dayparts = listOf(DayPart("Pagi", Vibe.STUDY), DayPart("Siang", Vibe.STUDY), DayPart("Malam", Vibe.LIVELY)),
            suitability = Suitability(90, 45, 70),
            culinary = listOf(Culinary("Nasi Ayam Sambal Matah", "Porsi besar, harga mahasiswa", 25000))
        ),
        Place(3, "Sunset Corner", "Jl. Gading Serpong Barat No. 7", 1.2, Vibe.LIVELY, 25, 25, false, 4.2, "10.00 - 24.00",
            "Rooftop dengan live music akustik pada akhir pekan. Ramai saat sore dan malam.",
            listOf("Live music", "Area outdoor", "Parkir luas"),
            checkIns = 187, minOrderK = 35,
            tags = listOf("Ramai", "Good vibes"),
            peakHours = listOf(10, 12, 18, 25, 45, 70, 95, 85),
            dayparts = listOf(DayPart("Pagi", Vibe.QUIET), DayPart("Siang", Vibe.QUIET), DayPart("Malam", Vibe.LIVELY)),
            suitability = Suitability(25, 92, 35),
            culinary = listOf(Culinary("Es Kopi Senja", "Signature drink, paling laris", 28000))
        ),
        Place(4, "Pojok Baca Library Cafe", "Jl. Pondok Jagung Raya No. 3", 1.6, Vibe.QUIET, 95, 40, true, 4.8, "09.00 - 21.00",
            "Perpustakaan mini yang digabung dengan kafe. Dilarang bicara keras, sangat cocok untuk fokus.",
            listOf("Area hening", "Stopkontak", "Koleksi buku", "AC"),
            checkIns = 156, minOrderK = 18,
            tags = listOf("Tenang", "Focus", "Free wifi"),
            peakHours = listOf(20, 28, 35, 30, 32, 38, 30, 15),
            suitability = Suitability(95, 30, 55),
            culinary = listOf(Culinary("Matcha Latte", "Manis lembut, cocok menemani membaca", 24000))
        ),
        Place(5, "Hub Coworking Space", "Ruko Paramount Blok C5", 2.1, Vibe.STUDY, 60, 150, true, 4.6, "24 jam",
            "Ruang kerja bersama dengan ruang rapat kecil. Internet sangat stabil.",
            listOf("Wi-Fi 150 Mbps", "Ruang rapat", "Stopkontak", "Loker", "24 jam"),
            checkIns = 421, minOrderK = 50,
            tags = listOf("Nugas", "Free wifi", "Focus"),
            peakHours = listOf(40, 55, 70, 65, 60, 50, 35, 25),
            suitability = Suitability(88, 35, 95),
            culinary = listOf(Culinary("Americano Double Shot", "Kopi hitam pekat penghilang kantuk", 20000))
        ),
        Place(6, "Kedai Bang Jago", "Jl. Raya Serpong KM 7", 0.7, Vibe.LIVELY, 15, 0, false, 4.0, "15.00 - 02.00",
            "Warung kopi ramai tempat nongkrong malam. Tidak ada Wi-Fi, tapi harganya sangat bersahabat.",
            listOf("Harga murah", "Area merokok", "Parkir motor"),
            checkIns = 98, minOrderK = 8,
            tags = listOf("Ramai", "Affordable"),
            peakHours = listOf(0, 0, 5, 10, 25, 45, 85, 100),
            suitability = Suitability(10, 95, 20),
            culinary = listOf(Culinary("Kopi Hitam Tubruk", "Kopi lokal khas, nikmat dan murah", 8000))
        ),
        Place(7, "Taman Sudut Park", "Jl. Taman Sari Blok A", 2.8, Vibe.LIVELY, 35, 0, false, 4.1, "05.00 - 21.00",
            "Taman kota dengan area jogging dan kursi santai. Ramai pada pagi dan sore hari.",
            listOf("Area outdoor", "Jogging track", "Gratis"),
            checkIns = 134, minOrderK = 0,
            tags = listOf("Ramai", "Affordable", "Good vibes"),
            peakHours = listOf(80, 60, 30, 20, 40, 75, 60, 20),
            suitability = Suitability(15, 85, 25)),
        Place(8, "Kedai Hening", "Jl. Melati No. 21, Tangerang", 3.4, Vibe.QUIET, 85, 35, true, 4.4, "08.00 - 20.00",
            "Kedai kecil bernuansa kayu. Pemiliknya menerapkan aturan telepon dalam mode senyap.",
            listOf("Stopkontak", "Mode senyap", "Teh & kopi lokal"),
            checkIns = 76, minOrderK = 15,
            tags = listOf("Tenang", "Focus"),
            suitability = Suitability(78, 40, 30),
            culinary = listOf(Culinary("Teh Chamomile", "Teh herbal hangat menenangkan", 15000))
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
        Contributor(1, "Alya Putri", "Legenda Tempat", 2450),
        Contributor(2, "Bagas Pratama", "Pemandu Kota", 1980),
        Contributor(3, "Citra Lestari", "Pemandu Kota", 1620),
        Contributor(4, "Dimas Anggara", "Penjelajah", 1240),
        Contributor(5, "Eka Wulandari", "Penjelajah", 980),
        Contributor(6, "Farah Nabila", "Pemula", 450)
    )
}