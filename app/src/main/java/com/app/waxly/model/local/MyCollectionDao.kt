package com.app.waxly.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.waxly.model.entities.MyCollection
import com.app.waxly.model.entities.Vinyl
import kotlinx.coroutines.flow.Flow

// Relación colección -> vinilo (JOIN para mostrar datos completos)
@Dao
interface MyCollectionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: MyCollection): Long

    @Query("""
        SELECT v.* FROM vinyls v
        INNER JOIN my_collection c ON c.vinylId = v.id
        ORDER BY v.title ASC
    """)
    fun getCollectedVinyls(): Flow<List<Vinyl>>

    @Query("DELETE FROM my_collection WHERE vinylId = :vinylId")
    suspend fun remove(vinylId: Long)
}
