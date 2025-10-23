package com.app.waxly.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.app.waxly.model.local.AppDatabase
import com.app.waxly.repository.SessionManager
import com.app.waxly.repository.UserRepository
import kotlinx.coroutines.launch

/* Pantalla inicial: dos botones centrados */
@Composable
fun AuthLandingScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onLogin, modifier = Modifier.widthIn(min = 220.dp)) {
                Text("Iniciar Sesión")
            }
            OutlinedButton(onClick = onRegister, modifier = Modifier.widthIn(min = 220.dp)) {
                Text("Registrarse")
            }
        }
    }
}

/* Login MUY simple: repo directo, sin ViewModel */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val db = remember { AppDatabase.get(context) }
    val repo = remember { UserRepository(db.userDao()) }
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pass, onValueChange = { pass = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true; error = null
                        val r = repo.login(email.trim(), pass)
                        r.fold(
                            onSuccess = {
                                session.setLoggedIn(true)
                                session.saveUser("Usuario", email.trim())
                                onSuccess()
                            },
                            onFailure = { error = it.message ?: "Error al iniciar sesión" }
                        )
                        isLoading = false
                    }
                },
                enabled = !isLoading && email.isNotBlank() && pass.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar") }

            if (isLoading) CircularProgressIndicator()
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

/* Register MUY simple: validación básica + repo directo */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val db = remember { AppDatabase.get(context) }
    val repo = remember { UserRepository(db.userDao()) }
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Validaciones simples
    val emailValid = email.isNotBlank() && '@' in email
    val passValid = pass.length >= 6
    val canSubmit = !isLoading && name.isNotBlank() && emailValid && passValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrarse") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, singleLine = true,
                isError = email.isNotEmpty() && !emailValid,
                supportingText = {
                    if (email.isNotEmpty() && !emailValid)
                        Text("El email debe contener '@'", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pass, onValueChange = { pass = it },
                label = { Text("Contraseña (mín. 6)") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = pass.isNotEmpty() && !passValid,
                supportingText = {
                    if (pass.isNotEmpty() && !passValid)
                        Text("La contraseña debe tener 6 caracteres o más", color = MaterialTheme.colorScheme.error)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true; error = null
                        val r = repo.register(name.trim(), email.trim(), pass)
                        r.fold(
                            onSuccess = {
                                session.saveUser(name.trim(), email.trim())
                                onSuccess()
                            },
                            onFailure = { error = it.message ?: "Error al registrarse" }
                        )
                        isLoading = false
                    }
                },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Crear cuenta") }

            if (isLoading) CircularProgressIndicator()
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}