package com.example.viktorsjourney.core.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.viktorsjourney.core.data.models.AchievementState
import com.example.viktorsjourney.core.data.models.EndlessAchievement
import com.example.viktorsjourney.core.data.models.EndlessModeProgress
import com.example.viktorsjourney.core.data.models.GameCountry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore by preferencesDataStore(name = "game_data")

object GameDefaults {
    val DEFAULT_COUNTRY = GameCountry.RUSSIA
    val DEFAULT_ENDLESS = EndlessModeProgress(
        0,
        0,
        "0 mins",
        0,
        0
    )
    val DEFAULT_ENDLESS_ACHIEVEMENTS = mapOf(
        EndlessAchievement.RisingStar.title to EndlessAchievement.RisingStar,
        EndlessAchievement.StormBreaker.title to EndlessAchievement.StormBreaker,
        EndlessAchievement.SkyDominator.title to EndlessAchievement.SkyDominator,
        EndlessAchievement.Transcendent.title to EndlessAchievement.Transcendent,
        EndlessAchievement.FirstBloodline.title to EndlessAchievement.FirstBloodline,
        EndlessAchievement.Relentless.title to EndlessAchievement.Relentless,
        EndlessAchievement.Merciless.title to EndlessAchievement.Merciless,
        EndlessAchievement.Annihilator.title to EndlessAchievement.Annihilator,
        EndlessAchievement.Globetrotter.title to EndlessAchievement.Globetrotter,
        EndlessAchievement.Glider.title to EndlessAchievement.Glider,
        EndlessAchievement.Aviator.title to EndlessAchievement.Aviator,
        EndlessAchievement.Voyager.title to EndlessAchievement.Voyager,
        EndlessAchievement.Starfarer.title to EndlessAchievement.Starfarer,
    )
}


class GameDataSource(private val context: Context) : GameRepository {

    // Define preference keys
    private object Keys {
        val GAME_SOUND_STATUS = booleanPreferencesKey("game_sound_status")
        val GAME_COUNTRY = stringPreferencesKey("game_country")
        val ENDLESS_MODE_PROGRESS = stringPreferencesKey("endless_mode_progress_json")
        val ENDLESS_ACHIEVEMENTS = stringPreferencesKey("endless_achievements_json")
    }

    // Game Sound Status
    override fun getGameSoundStatus(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[Keys.GAME_SOUND_STATUS] ?: true
        }
    }

    override suspend fun setGameSoundStatus(status: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GAME_SOUND_STATUS] = status
        }
    }

    // Game Country
    override fun getGameCountry(): Flow<GameCountry> =
        context.dataStore.data.map { prefs ->
            val savedName = prefs[Keys.GAME_COUNTRY]
            savedName?.let {
                runCatching { GameCountry.valueOf(it) }.getOrDefault(GameDefaults.DEFAULT_COUNTRY)
            } ?: GameDefaults.DEFAULT_COUNTRY
        }

    override suspend fun setGameCountry(country: GameCountry) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GAME_COUNTRY] = country.name
        }
    }

    // Endless Mode Progress (serialize as JSON)
    override fun getEndlessModeProgress(): Flow<EndlessModeProgress> =
        context.dataStore.data.map { prefs ->
            val json = prefs[Keys.ENDLESS_MODE_PROGRESS]
            if (!json.isNullOrEmpty()) {
                runCatching { Json.decodeFromString<EndlessModeProgress>(json) }
                    .getOrDefault(GameDefaults.DEFAULT_ENDLESS)
            } else {
                GameDefaults.DEFAULT_ENDLESS
            }
        }

    override suspend fun setEndlessModeProgress(progress: EndlessModeProgress) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ENDLESS_MODE_PROGRESS] = Json.encodeToString(
                EndlessModeProgress.serializer(),
                progress
            )
        }
    }

    // Endless Achievements
    override fun getEndlessAchievements(): Flow<Map<String, EndlessAchievement>> =
        context.dataStore.data.map { prefs ->

            val achievements = GameDefaults.DEFAULT_ENDLESS_ACHIEVEMENTS

            val json = prefs[Keys.ENDLESS_ACHIEVEMENTS]

            if (!json.isNullOrEmpty()) {

                runCatching {
                    Json.decodeFromString<Map<String, AchievementState>>(json)
                }.onSuccess { savedStates ->

                    savedStates.forEach { (name, state) ->
                        achievements[name]?.apply {
                            current = state.current
                            isCompleted = state.isCompleted
                        }
                    }

                }.onFailure {
                    Log.e("DATASTORE", "Failed to load achievements", it)
                }
            }

            achievements
        }

    override suspend fun setEndlessAchievements(
        achievements: Map<String, EndlessAchievement>
    ) {

        val saveMap = achievements.mapValues { (_, achievement) ->
            AchievementState(
                current = achievement.current,
                isCompleted = achievement.isCompleted
            )
        }

        val json = Json.encodeToString(saveMap)

        Log.d("DATASTORE", "Saving = $json")

        context.dataStore.edit { prefs ->
            prefs[Keys.ENDLESS_ACHIEVEMENTS] = json
        }
    }

}