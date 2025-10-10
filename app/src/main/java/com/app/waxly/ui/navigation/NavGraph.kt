package com.app.waxly.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.waxly.ui.auth.AuthScreen
import com.app.waxly.ui.collection.CollectionScreen
import com.app.waxly.ui.home.HomeScreen
import com.app.waxly.ui.profile.ProfileScreen
import com.app.waxly.ui.wantlist.WantlistScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = "auth"
) {
    // rutas donde se muestra el bottom bar
    val topLevelRoutes = setOf("home", "collection", "wantlist", "profile")

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in topLevelRoutes) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ---------- Auth ----------
            composable("auth") {
                AuthScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            // elimina Auth del back stack para no volver con back
                            popUpTo("auth") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // ---------- Top-level (BottomNav) ----------
            composable("home") { HomeScreen() }
            composable("collection") { CollectionScreen() }
            composable("wantlist") { WantlistScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}
