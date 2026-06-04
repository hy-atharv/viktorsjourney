package com.example.viktorsjourney.core.data.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.example.viktorsjourney.core.data.models.EndlessAchievement
import com.example.viktorsjourney.core.data.models.EndlessModeProgress
import com.example.viktorsjourney.core.data.models.GameCountry
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable
object HomeScreen

@Serializable
object EndlessModeScreen

@Serializable
object EndlessAchievementsScreen

@Serializable
object SelectMapsScreen

@Serializable
object EndlessGameplayScreen

@Serializable
data class EndlessAfterGameScreen(
    val score: Int,
    val isHighScore: Boolean,
    val distance: Int,
    val completedAchievements: List<EndlessAchievement>,
    val flightNo: Int
)