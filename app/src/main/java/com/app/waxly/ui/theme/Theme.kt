package com.app.waxly.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/* Paleta de colores */
private val LightGrayScheme = lightColorScheme(
    primary = Color(0xFF3D3D3D),
    onPrimary = Color(0xFFF7F7F7),
    primaryContainer = Color(0xFFE6E6E6),
    onPrimaryContainer = Color(0xFF1A1A1A),

    secondary = Color(0xFF5A5A5A),
    onSecondary = Color(0xFFF5F5F5),
    secondaryContainer = Color(0xFFEAEAEA),
    onSecondaryContainer = Color(0xFF222222),

    tertiary = Color(0xFF8A8A8A),
    onTertiary = Color(0xFF111111),
    tertiaryContainer = Color(0xFFF0F0F0),
    onTertiaryContainer = Color(0xFF232323),

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF161616),

    surface = Color(0xFFF4F4F4),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF2A2A2A),

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFD9D9D9),

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

private val DarkGrayScheme = darkColorScheme(
    primary = Color(0xFFE0E0E0),
    onPrimary = Color(0xFF111111),
    primaryContainer = Color(0xFF2B2B2B),
    onPrimaryContainer = Color(0xFFEFEFEF),

    secondary = Color(0xFFC9C9C9),
    onSecondary = Color(0xFF111111),
    secondaryContainer = Color(0xFF333333),
    onSecondaryContainer = Color(0xFFEAEAEA),

    tertiary = Color(0xFFB3B3B3),
    onTertiary = Color(0xFF121212),
    tertiaryContainer = Color(0xFF3A3A3A),
    onTertiaryContainer = Color(0xFFEDEDED),

    background = Color(0xFF111111),
    onBackground = Color(0xFFEDEDED),

    surface = Color(0xFF141414),
    onSurface = Color(0xFFEAEAEA),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFCFCFCF),

    outline = Color(0xFF6B6B6B),
    outlineVariant = Color(0xFF454545),

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
)


@Composable
fun WaxlyTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // false para forzar colores
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val ctx = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        } else {
            if (useDarkTheme) DarkGrayScheme else LightGrayScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}