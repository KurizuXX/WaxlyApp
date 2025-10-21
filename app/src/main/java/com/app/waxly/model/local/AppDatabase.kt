package com.app.waxly.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.waxly.R
import com.app.waxly.model.entities.MyCollection
import com.app.waxly.model.entities.MyWantlist
import com.app.waxly.model.entities.User
import com.app.waxly.model.entities.Vinyl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Base de datos Room con 4 tablas
@Database(
    entities = [User::class, Vinyl::class, MyCollection::class, MyWantlist::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun vinylDao(): VinylDao
    abstract fun myCollectionDao(): MyCollectionDao
    abstract fun myWantlistDao(): MyWantlistDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // Singleton seguro para evitar múltiples instancias
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waxly.db"
                )

                    // Seed inicial al crear DB
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.vinylDao()?.let { dao ->
                                    if (dao.count() == 0) dao.insertAll(seedVinyls())
                                }
                            }
                        }
                    })
                    .build()

                INSTANCE = db

                // Por si la DB existía pero sin datos (seed de respaldo)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = db.vinylDao()
                    if (dao.count() == 0) dao.insertAll(seedVinyls())
                }

                db
            }

        // Catálogo de 20 vinilos con covers en drawable
        private fun seedVinyls(): List<Vinyl> = listOf(
            Vinyl(title = "Abbey Road", artist = "The Beatles", year = 1969, coverRes = safeCover(R.drawable.cover_abbey_road)),
            Vinyl(title = "The Dark Side of the Moon", artist = "Pink Floyd", year = 1973, coverRes = safeCover(R.drawable.cover_dark_side)),
            Vinyl(title = "Nevermind", artist = "Nirvana", year = 1991, coverRes = safeCover(R.drawable.cover_nevermind)),
            Vinyl(title = "The Doors", artist = "The Doors", year = 1967, coverRes = safeCover(R.drawable.cover_the_doors)),
            Vinyl(title = "OK Computer OKNOTOK 1997-2017", artist = "Radiohead", year = 2017, coverRes = safeCover(R.drawable.cover_ok_computer_oknotok)),
            Vinyl(title = "Discovery", artist = "Daft Punk", year = 2001, coverRes = safeCover(R.drawable.cover_discovery)),
            Vinyl(title = "Thriller", artist = "Michael Jackson", year = 1982, coverRes = safeCover(R.drawable.cover_thriller)),
            Vinyl(title = "Rumours", artist = "Fleetwood Mac", year = 1977, coverRes = safeCover(R.drawable.cover_rumours)),
            Vinyl(title = "Led Zeppelin IV", artist = "Led Zeppelin", year = 1971, coverRes = safeCover(R.drawable.cover_led_zeppelin_iv)),
            Vinyl(title = "IGOR", artist = "Tyler, the Creator", year = 2019, coverRes = safeCover(R.drawable.cover_igor)),
            Vinyl(title = "Circles", artist = "Mac Miller", year = 2020, coverRes = safeCover(R.drawable.cover_circles)),
            Vinyl(title = "GNX", artist = "Kendrick Lamar", year = 2024, coverRes = safeCover(R.drawable.cover_gnx)),
            Vinyl(title = "Selected Ambient Works 85–92", artist = "Aphex Twin", year = 1992, coverRes = safeCover(R.drawable.cover_selected_ambient_works)),
            Vinyl(title = "AM", artist = "Arctic Monkeys", year = 2013, coverRes = safeCover(R.drawable.cover_am)),
            Vinyl(title = "Nectar", artist = "Joji", year = 2020, coverRes = safeCover(R.drawable.cover_nectar)),
            Vinyl(title = "Salad Days", artist = "Mac DeMarco", year = 2014, coverRes = safeCover(R.drawable.cover_salad_days)),
            Vinyl(title = "Todos los días todo el dia", artist = "LATIN MAFIA", year = 2024, coverRes = safeCover(R.drawable.cover_todos_los_dias)),
            Vinyl(title = "Epistolares", artist = "AKRIILA", year = 2024, coverRes = safeCover(R.drawable.cover_epistolares)),
            Vinyl(title = "The New Abnormal", artist = "The Strokes", year = 2020, coverRes = safeCover(R.drawable.cover_the_new_abnormal)),
            Vinyl(title = "Breach", artist = "Twenty One Pilots", year = 2025, coverRes = safeCover(R.drawable.cover_breach)),
            Vinyl(title = "Blonde", artist = "Frank Ocean", year = 2016, coverRes = safeCover(R.drawable.cover_blonde))
        )

        // Si algún cover no existe, aquí podríamos redirigir a un placeholder
        private fun safeCover(resId: Int): Int = resId
        // Ejemplo si usas placeholder:
        // private fun safeCover(resId: Int) = resId.takeIf { it != 0 } ?: R.drawable.cover_placeholder
    }
}
