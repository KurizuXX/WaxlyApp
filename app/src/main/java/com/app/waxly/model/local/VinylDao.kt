package com.app.waxly.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.waxly.model.entities.Vinyl
import kotlinx.coroutines.flow.Flow

@Dao
interface VinylDao {

    @Query("SELECT * FROM vinyls ORDER BY title ASC")
    fun getAll(): Flow<List<Vinyl>>

    @Query("SELECT * FROM vinyls WHERE title LIKE :q OR artist LIKE :q ORDER BY title ASC")
    fun searchVinyls(q: String): Flow<List<Vinyl>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Vinyl>)

    @Query("SELECT COUNT(*) FROM vinyls")
    suspend fun count(): Int
}