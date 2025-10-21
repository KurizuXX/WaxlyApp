package com.app.waxly.model.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Usuario básico con email único (para login)
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String
)
