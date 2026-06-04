package com.example.viktorsjourney.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.viktorsjourney.R

// Set of Game typography
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 20.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 39.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 34.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 30.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 46.sp
    ),

    labelSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 16.sp
    ),

    labelMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 29.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 17.sp
    ),

    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.determination_sans)),
        fontSize = 11.sp
    )
)