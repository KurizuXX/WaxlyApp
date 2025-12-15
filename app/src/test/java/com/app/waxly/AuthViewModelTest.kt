package com.app.waxly

import com.app.waxly.model.entities.User
import com.app.waxly.model.local.UserDao
import com.app.waxly.repository.UserRepository
import com.app.waxly.viewmodel.AuthViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/**
 * FakeUserDao en memoria para tests unitarios
 */
private class FakeUserDao : UserDao {

    private val users = mutableListOf<User>()
    private var autoId = 1L

    override suspend fun insert(user: User): Long {
        val withId = user.copy(id = autoId++)
        users.add(withId)
        return withId.id
    }

    override suspend fun getByEmail(email: String): User? {
        return users.firstOrNull { it.email == email }
    }

    override suspend fun count(): Int = users.size
}

class AuthViewModelTest {

    @Test
    fun login_exitoso_actualiza_uiState_con_usuario() = runBlocking {
        // Arrange
        val dao = FakeUserDao()
        val repo = UserRepository(dao)

        // Usuario existente (simula registro previo)
        repo.register(
            name = "Test User",
            email = "test@test.com",
            password = "123456"
        )

        val viewModel = AuthViewModel(repo)

        // Act
        viewModel.login("test@test.com", "123456")

        // Assert
        val state = viewModel.uiState.value
        assertNotNull(state.user)
        assertEquals("test@test.com", state.user?.email)
        assertNull(state.error)
    }

    @Test
    fun login_con_password_incorrecta_muestra_error() = runBlocking {
        // Arrange
        val dao = FakeUserDao()
        val repo = UserRepository(dao)

        repo.register(
            name = "Test User",
            email = "test@test.com",
            password = "123456"
        )

        val viewModel = AuthViewModel(repo)

        // Act
        viewModel.login("test@test.com", "wrongpass")

        // Assert
        val state = viewModel.uiState.value
        assertNull(state.user)
        assertNotNull(state.error)
    }
}