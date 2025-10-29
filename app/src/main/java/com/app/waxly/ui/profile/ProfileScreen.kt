package com.app.waxly.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.waxly.repository.SessionManager

// Perfil minimalista, avatar genérico, nombre/email guardados en sesión y botón de logout
@Composable
fun ProfileScreen(navController: NavController, onLogoutRoute: String) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Avatar",
            modifier = Modifier.size(96.dp).clip(CircleShape)
        )
        Spacer(Modifier.height(12.dp))
        Text(text = session.getName() ?: "Usuario", style = MaterialTheme.typography.titleLarge)
        Text(text = session.getEmail() ?: "-", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                // Limpiamos sesión
                session.clear()
                navController.navigate(onLogoutRoute) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar sesión") }
    }
}