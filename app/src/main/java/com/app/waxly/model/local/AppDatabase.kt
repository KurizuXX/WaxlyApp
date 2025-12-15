package com.app.waxly.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.waxly.model.entities.MyCollection
import com.app.waxly.model.entities.MyWantlist
import com.app.waxly.model.entities.User
import com.app.waxly.model.entities.Vinyl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Vinyl::class, MyCollection::class, MyWantlist::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun vinylDao(): VinylDao
    abstract fun myCollectionDao(): MyCollectionDao
    abstract fun myWantlistDao(): MyWantlistDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waxly_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // Seed inicial de vinilos cuando se crea la DB por primera vez
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = get(context)
                                val dao = database.vinylDao()

                                // Evita insertar si ya hay datos (por si acaso)
                                if (dao.count() == 0) {
                                    dao.insertAll(seedVinyls())
                                }
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // Lista inicial de vinilos (IMPORTANTE: coverName debe existir en res/drawable)
        private fun seedVinyls(): List<Vinyl> = listOf(
            Vinyl(title = "Abbey Road", artist = "The Beatles", year = 1969, coverName = "cover_abbey_road"),
            Vinyl(title = "The Dark Side of the Moon", artist = "Pink Floyd", year = 1973, coverName = "cover_dark_side"),
            Vinyl(title = "Nevermind", artist = "Nirvana", year = 1991, coverName = "cover_nevermind"),
            Vinyl(title = "The Doors", artist = "The Doors", year = 1967, coverName = "cover_the_doors"),
            Vinyl(title = "OK Computer OKNOTOK 1997-2017", artist = "Radiohead", year = 2017, coverName = "cover_ok_computer_oknotok"),
            Vinyl(title = "Discovery", artist = "Daft Punk", year = 2001, coverName = "cover_discovery"),
            Vinyl(title = "Thriller", artist = "Michael Jackson", year = 1982, coverName = "cover_thriller"),
            Vinyl(title = "Rumours", artist = "Fleetwood Mac", year = 1977, coverName = "cover_rumours"),
            Vinyl(title = "Led Zeppelin IV", artist = "Led Zeppelin", year = 1971, coverName = "cover_led_zeppelin_iv"),
            Vinyl(title = "IGOR", artist = "Tyler, the Creator", year = 2019, coverName = "cover_igor"),
            Vinyl(title = "Circles", artist = "Mac Miller", year = 2020, coverName = "cover_circles"),
            Vinyl(title = "GNX", artist = "Kendrick Lamar", year = 2024, coverName = "cover_gnx"),
            Vinyl(title = "Selected Ambient Works 85–92", artist = "Aphex Twin", year = 1992, coverName = "cover_selected_ambient_works"),
            Vinyl(title = "AM", artist = "Arctic Monkeys", year = 2013, coverName = "cover_am"),
            Vinyl(title = "Nectar", artist = "Joji", year = 2020, coverName = "cover_nectar"),
            Vinyl(title = "Salad Days", artist = "Mac DeMarco", year = 2014, coverName = "cover_salad_days"),
            Vinyl(title = "Todos los días todo el dia", artist = "LATIN MAFIA", year = 2024, coverName = "cover_todos_los_dias"),
            Vinyl(title = "Epistolares", artist = "AKRIILA", year = 2024, coverName = "cover_epistolares"),
            Vinyl(title = "The New Abnormal", artist = "The Strokes", year = 2020, coverName = "cover_the_new_abnormal"),
            Vinyl(title = "Breach", artist = "Twenty One Pilots", year = 2025, coverName = "cover_breach"),
            Vinyl(title = "Blonde", artist = "Frank Ocean", year = 2016, coverName = "cover_blonde"),
            Vinyl(title = "La Vida Era Más Corta", artist = "Milo J", year = 2025, coverName = "cover_la_vida_era_mas_corta"),
        )
    }
}
