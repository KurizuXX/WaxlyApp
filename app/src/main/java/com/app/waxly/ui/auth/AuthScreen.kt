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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.waxly.model.local.AppDatabase
import com.app.waxly.repository.SessionManager
import com.app.waxly.repository.UserRepository
import com.app.waxly.viewmodel.AuthViewModel
import com.app.waxly.viewmodel.AuthViewModelFactory

/* Pantalla inicial simple: dos botones centrados */
@Composable
fun AuthLandingScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onLogin, modifier = Modifier.widthIn(min = 220.dp)) { Text("Iniciar Sesión") }
            OutlinedButton(onClick = onRegister, modifier = Modifier.widthIn(min = 220.dp)) { Text("Registrarse") }
        }
    }
}

/* Login: usa ViewModel + Room; guarda sesión al éxito */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }           // prefs para sesión
    val db = remember { AppDatabase.get(context) }               // instancia Room
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(UserRepository(db.userDao())))

    val ui by vm.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }

    // Al completar login, marcar sesión y navegar
    LaunchedEffect(ui.success) {
        if (ui.success) {
            session.setLoggedIn(true)
            session.saveUser("Usuario", email.trim())
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = { vm.login(email.trim(), pass) },
                enabled = !ui.isLoading && email.isNotBlank() && pass.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar") }

            if (ui.isLoading) CircularProgressIndicator()
            ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

/* Register: crea usuario, guarda datos mínimos en sesión y navega */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val db = remember { AppDatabase.get(context) }
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(UserRepository(db.userDao())))

    val ui by vm.uiState.collectAsState()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(ui.success) {
        if (ui.success) {
            session.saveUser(name.trim(), email.trim())
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrarse") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = { vm.register(name.trim(), email.trim(), pass) },
                enabled = !ui.isLoading && name.isNotBlank() && email.isNotBlank() && pass.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Crear cuenta") }

            if (ui.isLoading) CircularProgressIndicator()
            ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}