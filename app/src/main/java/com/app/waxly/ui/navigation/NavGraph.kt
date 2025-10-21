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
import com.app.waxly.ui.auth.AuthLandingScreen
import com.app.waxly.ui.auth.LoginScreen
import com.app.waxly.ui.auth.RegisterScreen
import com.app.waxly.ui.collection.CollectionScreen
import com.app.waxly.ui.home.HomeScreen
import com.app.waxly.ui.profile.ProfileScreen
import com.app.waxly.ui.wantlist.WantlistScreen

// Rutas centralizadas para evitar strings sueltos
object Routes {
    const val AUTH_LANDING = "authLanding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val COLLECTION = "collection"
    const val WANTLIST = "wantlist"
    const val PROFILE = "profile"
}

// Host de navegación + Scaffold con bottom bar solo en pantallas top-level
@Composable
fun NavGraph(navController: NavHostController) {
    val topLevelRoutes = setOf(Routes.HOME, Routes.COLLECTION, Routes.WANTLIST, Routes.PROFILE)
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    Scaffold(
        bottomBar = { if (current in topLevelRoutes) BottomNavBar(navController) }
    ) { inner ->
        // Árbol de navegación
        NavHost(
            navController = navController,
            startDestination = Routes.AUTH_LANDING,
            modifier = Modifier.padding(inner)
        ) {
            // Landing con botones Login/Register
            composable(Routes.AUTH_LANDING) {
                AuthLandingScreen(
                    onLogin = { navController.navigate(Routes.LOGIN) },
                    onRegister = { navController.navigate(Routes.REGISTER) }
                )
            }
            // Login: al éxito, limpiamos backstack hasta landing y vamos a HOME
            composable(Routes.LOGIN) {
                LoginScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH_LANDING) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            // Register: mismo flujo que Login
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH_LANDING) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            // Top-levels
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.COLLECTION) { CollectionScreen() }
            composable(Routes.WANTLIST) { WantlistScreen() }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    navController = navController,
                    onLogoutRoute = Routes.AUTH_LANDING
                )
            }
        }
    }
}