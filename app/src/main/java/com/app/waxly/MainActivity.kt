package com.app.waxly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.app.waxly.ui.navigation.NavGraph
import com.app.waxly.ui.theme.WaxlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaxlyTheme {
                val navController = rememberNavController()
                // solo pasamos el navController
                NavGraph(navController = navController)
            }
        }
    }
}
