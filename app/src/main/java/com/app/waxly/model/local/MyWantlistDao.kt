package com.app.waxly.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.waxly.model.entities.MyWantlist
import com.app.waxly.model.entities.Vinyl
import kotlinx.coroutines.flow.Flow

// Relación wantlist -> vinilo
@Dao
interface MyWantlistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: MyWantlist): Long

    @Query("""
        SELECT v.* FROM vinyls v
        INNER JOIN my_wantlist w ON w.vinylId = v.id
        ORDER BY v.title ASC
    """)
    fun getWantlistVinyls(): Flow<List<Vinyl>>

    @Query("DELETE FROM my_wantlist WHERE vinylId = :vinylId")
    suspend fun remove(vinylId: Long)
}
