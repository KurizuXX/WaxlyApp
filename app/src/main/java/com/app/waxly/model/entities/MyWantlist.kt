package com.app.waxly.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Item de mi wantlist (vinylId único para no duplicar)
@Entity(
    tableName = "my_wantlist",
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
data class MyWantlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vinylId: Long
)
