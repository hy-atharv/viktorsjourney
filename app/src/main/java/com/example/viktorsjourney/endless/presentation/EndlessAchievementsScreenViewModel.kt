package com.example.viktorsjourney.endless.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viktorsjourney.core.data.GameDefaults
import com.example.viktorsjourney.core.data.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EndlessAchievementsScreenViewModel(
    private val repo: GameRepository
): ViewModel() {
    val orderedEndlessAchievements = repo.getEndlessAchievements()
        .map {
            it.values.sortedByDescending { endlessAchievement ->
                // ordered by Achievements Completion
                (endlessAchievement.current/endlessAchievement.target)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), GameDefaults.DEFAULT_ENDLESS_ACHIEVEMENTS.values.toList())
}