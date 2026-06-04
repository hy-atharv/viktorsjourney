package com.example.viktorsjourney.core.data.models


import com.example.viktorsjourney.R
import kotlinx.serialization.Serializable

@Serializable
data class EndlessModeProgress(
    var highestScore: Int,
    var longestDistance: Int,
    var longestTime: String,
    var mostEnemiesDefeated: Int,
    var totalEndlessFlights: Int
)

@Serializable
enum class EndlessAchievementType {
    SCORE_BASED,
    DISTANCE_BASED,
    ENEMY_DEFEATS_BASED,
    MAPS_BASED
}

@Serializable
sealed class EndlessAchievement(
    var title: String,
    var description: String,
    var completedDescription: String,
    var target: Float,
    var current: Float,
    var badgeImage: Int,
    var isCompleted: Boolean,
    var achievementType: EndlessAchievementType
) {
    // High Score in one flight
    @Serializable
    data object RisingStar: EndlessAchievement(
        title = "Rising Star",
        description = "Score 500 Points in one flight",
        completedDescription = "Scored 500 Points in one flight",
        target = 500f,
        current = 0f,
        badgeImage = R.drawable.rising_star_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.SCORE_BASED
    )
    @Serializable
    data object StormBreaker: EndlessAchievement(
        title = "Storm Breaker",
        description = "Score 1,000 Points in one flight",
        completedDescription = "Scored 1,000 Points in one flight",
        target = 1000f,
        current = 0f,
        badgeImage = R.drawable.storm_breaker_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.SCORE_BASED
    )
    @Serializable
    data object SkyDominator: EndlessAchievement(
        title = "Sky Dominator",
        description = "Score 5,000 Points in one flight",
        completedDescription = "Scored 5,000 Points in one flight",
        target = 5000f,
        current = 0f,
        badgeImage = R.drawable.sky_dominator_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.SCORE_BASED
    )
    @Serializable
    data object Transcendent: EndlessAchievement(
        title = "Transcendent",
        description = "Score 10,000 Points in one flight",
        completedDescription = "Scored 10,000 Points in one flight",
        target = 10000f,
        current = 0f,
        badgeImage = R.drawable.transcendent_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.SCORE_BASED
    )
    // Total Enemies Killed
    @Serializable
    data object FirstBloodline: EndlessAchievement(
        title = "First Bloodline",
        description = "Defeat 100 Enemies in total",
        completedDescription = "Defeated 100 Enemies in total",
        target = 100f,
        current = 0f,
        badgeImage = R.drawable.first_bloodline_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.ENEMY_DEFEATS_BASED
    )
    @Serializable
    data object Relentless: EndlessAchievement(
        title = "Relentless",
        description = "Defeat 500 Enemies in total",
        completedDescription = "Defeated 500 Enemies in total",
        target = 500f,
        current = 0f,
        badgeImage = R.drawable.relentless_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.ENEMY_DEFEATS_BASED
    )
    @Serializable
    data object Merciless: EndlessAchievement(
        title = "Merciless",
        description = "Defeat 1,000 Enemies in total",
        completedDescription = "Defeated 1,000 Enemies in total",
        target = 1000f,
        current = 0f,
        badgeImage = R.drawable.merciless_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.ENEMY_DEFEATS_BASED
    )
    @Serializable
    data object Annihilator: EndlessAchievement(
        title = "Annihilator",
        description = "Defeat 5,000 Enemies in total",
        completedDescription = "Defeated 5,000 Enemies in total",
        target = 5000f,
        current = 0f,
        badgeImage = R.drawable.annihilator_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.ENEMY_DEFEATS_BASED
    )
    // Visit all maps in one flight
    @Serializable
    data object Globetrotter: EndlessAchievement(
        title = "Globetrotter",
        description = "Visit all Maps in one flight",
        completedDescription = "Visited all Maps in one flight",
        target = 5f,
        current = 0f,
        badgeImage = R.drawable.globetrotter_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.MAPS_BASED
    )
    // Total Distance Travelled
    @Serializable
    data object Glider: EndlessAchievement(
        title = "Glider",
        description = "Fly 10,000 Metres in total",
        completedDescription = "Flew 10,000 Metres in total",
        target = 10000f,
        current = 0f,
        badgeImage = R.drawable.glider_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.DISTANCE_BASED
    )
    @Serializable
    data object Aviator: EndlessAchievement(
        title = "Aviator",
        description = "Fly 100,000 Metres in total",
        completedDescription = "Flew 100,000 Metres in total",
        target = 100000f,
        current = 0f,
        badgeImage = R.drawable.aviator_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.DISTANCE_BASED
    )
    @Serializable
    data object Voyager: EndlessAchievement(
        title = "Voyager",
        description = "Fly 1,000,000 Metres in total",
        completedDescription = "Flew 1,000,000 Metres in total",
        target = 1000000f,
        current = 0f,
        badgeImage = R.drawable.voyager_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.DISTANCE_BASED
    )
    @Serializable
    data object Starfarer: EndlessAchievement(
        title = "Starfarer",
        description = "Fly 10,000,000 Metres in total",
        completedDescription = "Flew 10,000,000 Metres in total",
        target = 10000000f,
        current = 0f,
        badgeImage = R.drawable.starfarer_badge_icon,
        isCompleted = false,
        achievementType = EndlessAchievementType.DISTANCE_BASED
    )
}

@Serializable
data class AchievementState(
    val current: Float,
    val isCompleted: Boolean
)
