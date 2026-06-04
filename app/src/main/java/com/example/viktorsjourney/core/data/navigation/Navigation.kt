package com.example.viktorsjourney.core.data.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.viktorsjourney.core.data.models.EndlessAchievement
import com.example.viktorsjourney.endless.presentation.EndlessAchievementsScreen
import com.example.viktorsjourney.endless.presentation.EndlessAfterGameScreen
import com.example.viktorsjourney.endless.presentation.EndlessGameplayScreen
import com.example.viktorsjourney.endless.presentation.EndlessModeScreen
import com.example.viktorsjourney.endless.presentation.SelectMapScreen
import com.example.viktorsjourney.home.presentation.HomeScreen
import kotlin.reflect.typeOf

@Composable
fun GameMainNavigation(){
    val navigationController = rememberNavController()
    NavHost(navController = navigationController, startDestination = HomeScreen){
        // Home/Game Mode Screen
        composable<HomeScreen> {
            HomeScreen(navigator = navigationController)
        }

        // Endless Mode Screen
        composable<EndlessModeScreen>(
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 250), initialAlpha = 0f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 250), targetAlpha = 0f) }
        ) {
            EndlessModeScreen(navigator = navigationController)
        }
        // Endless Achievements Screen
        composable<EndlessAchievementsScreen>(
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 250), initialAlpha = 0f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 250), targetAlpha = 0f) }
        ) {
            EndlessAchievementsScreen(navigator = navigationController)
        }
        // Select Maps Screen
        composable<SelectMapsScreen>(
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 250), initialAlpha = 0f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 250), targetAlpha = 0f) }
        ) {
            SelectMapScreen(navigator = navigationController)
        }
        // Endless Gameplay Screen
        composable<EndlessGameplayScreen>(
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 250), initialAlpha = 0f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 250), targetAlpha = 0f) }
        ) {
            EndlessGameplayScreen(navigator = navigationController)
        }
        // Endless After Game Screen
        composable<EndlessAfterGameScreen>(
            typeMap = mapOf(
                typeOf<List<EndlessAchievement>>() to EndlessAchievementListNavType
            ),
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 250), initialAlpha = 0f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 250), targetAlpha = 0f) }
        ) {
            val args = it.toRoute<EndlessAfterGameScreen>()
            val score = args.score
            val isHighScore = args.isHighScore
            val distance = args.distance
            val achievementsCompleted = args.completedAchievements
            val flightNo = args.flightNo
            EndlessAfterGameScreen(
                navigator = navigationController,
                flightNo = flightNo,
                score = score,
                isHighScore = isHighScore,
                distance = distance,
                completedAchievements = achievementsCompleted
            )
        }
    }
}