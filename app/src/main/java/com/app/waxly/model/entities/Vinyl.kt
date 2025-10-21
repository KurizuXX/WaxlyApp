package com.app.waxly.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Vinilo en catálogo (coverRes guarda el drawable)
@Entity(tableName = "vinyls")
data class Vinyl(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val year: Int?,
    val coverRes: Int
)
