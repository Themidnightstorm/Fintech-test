package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VaultColorScheme = darkColorScheme(
    primary = SovereignGold,
    onPrimary = ObsidianBlack,
    primaryContainer = DeepOakBrown,
    onPrimaryContainer = LustrousGold,
    secondary = LustrousGold,
    onSecondary = DarkOakBrown,
    secondaryContainer = DarkOakBrown,
    onSecondaryContainer = ParchmentCream,
    tertiary = BurnishedGold,
    onTertiary = ObsidianBlack,
    background = RichBurgundy,
    onBackground = ParchmentCream,
    surface = DarkOakBrown,
    onSurface = ParchmentCream,
    surfaceVariant = DeepOakBrown,
    onSurfaceVariant = MutedCream,
    outline = SovereignGold,
    outlineVariant = RichOakBorder
)

@Composable
fun TheVaultTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VaultColorScheme,
        typography = VaultTypography,
        content = content
    )
}
