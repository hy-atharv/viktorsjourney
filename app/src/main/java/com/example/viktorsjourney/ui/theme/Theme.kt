package com.example.viktorsjourney.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable


private val GameColorPalette = darkColorScheme(
    primary = SapphireBlue,
    secondary = MidnightBlue,
    tertiary = EmeraldGreen,
)



//------------------------Game material themes-------------------------------

@Composable
fun GameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GameColorPalette,
        typography = Typography,
        content = content
    )
}