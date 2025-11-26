package com.app.waxly.ui.navigation

// Este objeto sellado contiene todas las rutas de tu app.
// Probablemente ya tengas uno similar. Si es así, solo añade la nueva ruta.
sealed class AppScreens(val route: String) {
    // Estas son ejemplos de rutas que podrías tener
    object SplashScreen : AppScreens("splash_screen")
    object HomeScreen : AppScreens("home_screen")

    // 👇 AÑADE ESTA NUEVA RUTA
    object LocationSearchScreen : AppScreens("location_search_screen")
}
