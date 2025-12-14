package com.app.waxly.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vinyls")
data class Vinyl(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val year: Int? = null,

    // Nombre del drawable en res/drawable (ej: "blonde", "salad_days")
    // Esto evita crashes por ids inválidos o recursos no soportados.
    val coverName: String = "ic_launcher_foreground"
)
