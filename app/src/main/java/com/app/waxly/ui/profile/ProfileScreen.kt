package com.app.waxly.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.waxly.repository.SessionManager

@Composable
fun ProfileScreen(
    navController: NavController,
    onLogoutRoute: String
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)

        OutlinedButton(
            onClick = {
                // Limpia sesión persistida
                session.clear()

                // Vuelve a Auth y limpia el backstack
                navController.navigate(onLogoutRoute) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Cerrar Sesión"
            )
            Spacer(Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }
    }
}