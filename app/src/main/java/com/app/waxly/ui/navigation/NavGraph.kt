package com.app.waxly.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.waxly.repository.SessionManager
import com.app.waxly.ui.auth.AuthLandingScreen
import com.app.waxly.ui.auth.LoginScreen
import com.app.waxly.ui.auth.RegisterScreen
import com.app.waxly.ui.collection.CollectionScreen
import com.app.waxly.ui.home.HomeScreen
import com.app.waxly.ui.location.LocationSearchScreen
import com.app.waxly.ui.profile.ProfileScreen
import com.app.waxly.ui.wantlist.WantlistScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    val startDestination = if (session.isLoggedIn()) Routes.HOME else Routes.AUTH_LANDING

    val topLevelRoutes = setOf(
        Routes.HOME,
        Routes.COLLECTION,
        Routes.WANTLIST,
        Routes.LOCATION_SEARCH,
        Routes.PROFILE
    )

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

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
            // Auth
            composable(Routes.AUTH_LANDING) {
                AuthLandingScreen(
                    onLoginClick = { navController.navigate(Routes.LOGIN) },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) }
                )
            }

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

            // Main
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.COLLECTION) { CollectionScreen() }
            composable(Routes.WANTLIST) { WantlistScreen() }
            composable(Routes.LOCATION_SEARCH) { LocationSearchScreen() }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    navController = navController,
                    onLogoutRoute = Routes.AUTH_LANDING
                )
            }
        }
    }
}
