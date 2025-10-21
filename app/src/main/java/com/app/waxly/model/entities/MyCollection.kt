package com.app.waxly.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Item de mi colección (vinylId único para no duplicar)
@Entity(
    tableName = "my_collection",
    indices = [Index(value = ["vinylId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Vinyl::class,
            parentColumns = ["id"],
            childColumns = ["vinylId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class MyCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vinylId: Long
)
