package com.app.waxly.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.waxly.model.local.AppDatabase
import com.app.waxly.repository.UserRepository
import com.app.waxly.viewmodel.AuthViewModel
import com.app.waxly.viewmodel.AuthViewModelFactory

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val repo = remember { UserRepository(db.userDao()) }
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(repo))
    val ui by vm.uiState.collectAsState()

    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(ui.success) { if (ui.success) onLoginSuccess() }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = isLogin, onClick = { isLogin = true }, label = { Text("Login") })
                FilterChip(selected = !isLogin, onClick = { isLogin = false }, label = { Text("Registro") })
            }
            Spacer(Modifier.height(16.dp))
            if (!isLogin) {
                OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
            }
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(password, { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { if (isLogin) vm.login(email, password) else vm.register(name, email, password) },
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (isLogin) "Iniciar sesión" else "Crear cuenta") }

            if (ui.isLoading) { Spacer(Modifier.height(12.dp)); CircularProgressIndicator() }
            ui.error?.let { Spacer(Modifier.height(12.dp)); Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}
