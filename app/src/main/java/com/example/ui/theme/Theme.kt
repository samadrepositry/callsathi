package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CallSathiDarkColorScheme = darkColorScheme(
    primary = ElectricBlueBright,
    onPrimary = Color.White,
    primaryContainer = ElectricBlueSoft,
    onPrimaryContainer = Color.White,
    secondary = RoyalPurpleBright,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B1F6E),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = CyanAccent,
    onTertiary = NavyDeep,
    background = NavyDeep,
    onBackground = DarkTextPrimary,
    surface = NavySurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = NavyCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = NavyBorder,
    outlineVariant = Color(0xFF1E293B),
    error = RoseError,
    onError = Color.White,
    errorContainer = RoseSoft,
    onErrorContainer = Color(0xFFFECACA)
)

private val CallSathiLightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = RoyalPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = CyanAccent,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = RoseError,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B)
)

val CallSathiShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun CallSathiTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CallSathiDarkColorScheme else CallSathiLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = CallSathiShapes,
        content = content
    )
}
