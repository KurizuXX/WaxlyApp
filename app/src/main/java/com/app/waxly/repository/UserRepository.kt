package com.app.waxly.repository

import com.app.waxly.model.entities.User
import com.app.waxly.model.local.UserDao
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    suspend fun register(name: String, email: String, password: String): Result<Long> {
        if (name.isBlank() || email.isBlank() || password.length < 6)
            return Result.failure(IllegalArgumentException("Datos inválidos"))
        if (userDao.getByEmail(email.trim()) != null)
            return Result.failure(IllegalStateException("El email ya está registrado"))
        val id = userDao.insert(
            User(name = name.trim(), email = email.trim(), passwordHash = sha256(password))
        )
        return Result.success(id)
    }

    suspend fun login(email: String, password: String) : Result<User> {
        val user = userDao.getByEmail(email.trim())
            ?: return Result.failure(IllegalArgumentException("Usuario no encontrado"))
        return if (user.passwordHash == sha256(password))
            Result.success(user) else Result.failure(IllegalArgumentException("Contraseña incorrecta"))
    }

    private fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
