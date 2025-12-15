package com.app.waxly.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.waxly.model.local.AppDatabase
import com.app.waxly.repository.SessionManager
import com.app.waxly.repository.UserRepository
import com.app.waxly.viewmodel.AuthViewModel
import com.app.waxly.viewmodel.AuthViewModelFactory

@Composable
fun AuthLandingScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("WAXLY", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black))
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) { Text("Iniciar Sesión") }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) { Text("Registrarse") }
    }
}

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val repo = remember { UserRepository(db.userDao()) }
    val factory = remember { AuthViewModelFactory(repo) }
    val vm: AuthViewModel = viewModel(factory = factory)

    val session = remember { SessionManager(context) }
    val uiState by vm.uiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.user) {
        uiState.user?.let { user ->
            session.setLoggedIn(true)
            session.saveUser(user.name ?: "Usuario", user.email)
            vm.reset()
            onSuccess()
        }
    }

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Text("INICIAR SESIÓN", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = { vm.login(email.trim(), password) },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(if (uiState.isLoading) "Ingresando..." else "Entrar")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = { vm.reset(); onBack() },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Volver") }
    }
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val repo = remember { UserRepository(db.userDao()) }
    val factory = remember { AuthViewModelFactory(repo) }
    val vm: AuthViewModel = viewModel(factory = factory)

    val session = remember { SessionManager(context) }
    val uiState by vm.uiState.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var password2 by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.user) {
        uiState.user?.let { user ->
            // coherente: registrarse también inicia sesión
            session.setLoggedIn(true)
            session.saveUser(user.name ?: name.ifBlank { "Usuario" }, user.email)
            vm.reset()
            onSuccess()
        }
    }

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Text("REGISTRO", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; localError = null },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; localError = null },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; localError = null },
            label = { Text("Contraseña (mín. 6)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password2,
            onValueChange = { password2 = it; localError = null },
            label = { Text("Repite contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        val errorToShow = localError ?: uiState.error
        errorToShow?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = {
                val n = name.trim()
                val e = email.trim()

                when {
                    n.isBlank() -> localError = "El nombre es obligatorio"
                    e.isBlank() || !e.contains("@") -> localError = "Email inválido, debe llevar @"
                    password.length < 6 -> localError = "La contraseña debe tener al menos 6 caracteres"
                    password != password2 -> localError = "Las contraseñas no coinciden"
                    else -> vm.register(n, e, password)
                }
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(if (uiState.isLoading) "Creando cuenta..." else "Crear cuenta")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = { vm.reset(); onBack() },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Volver") }
    }
}