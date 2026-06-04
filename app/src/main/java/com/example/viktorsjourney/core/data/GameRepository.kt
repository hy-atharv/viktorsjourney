package com.example.viktorsjourney.core.data

import com.example.viktorsjourney.core.data.models.EndlessAchievement
import com.example.viktorsjourney.core.data.models.EndlessModeProgress
import com.example.viktorsjourney.core.data.models.GameCountry
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getGameSoundStatus(): Flow<Boolean>
    suspend fun setGameSoundStatus(status: Boolean)
    fun getGameCountry(): Flow<GameCountry>
    suspend fun setGameCountry(country: GameCountry)
    fun getEndlessModeProgress(): Flow<EndlessModeProgress>
    suspend fun setEndlessModeProgress(progress: EndlessModeProgress)
    fun getEndlessAchievements(): Flow<Map<String, EndlessAchievement>>
    suspend fun setEndlessAchievements(achievements: Map<String, EndlessAchievement>)
}