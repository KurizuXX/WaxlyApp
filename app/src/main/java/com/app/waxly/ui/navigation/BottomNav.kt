package com.app.waxly.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// Estructura simple para item de la bottom bar
data class NavItem(val route: String, val label: String, val icon: ImageVector)

// Barra inferior: resalta seleccionado y preserva estado al cambiar tabs
@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem(Routes.HOME, "Home", Icons.Filled.Home),
        NavItem(Routes.COLLECTION, "Coleccion", Icons.Filled.Menu),
        NavItem(Routes.WANTLIST, "Wantlist", Icons.Filled.Favorite),
        NavItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val backStack by navController.currentBackStackEntryAsState()
        val current = backStack?.destination?.route

        items.forEach { item ->
            val selected = current == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        // Navegación recomendada para tabs (save/restore state)
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = { Text(item.label) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                // Colores para que el seleccionado quede clarísimo
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}