package com.example.viktorsjourney.endless.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viktorsjourney.core.data.GameDefaults
import com.example.viktorsjourney.core.data.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class EndlessModeScreenViewModel(
    private val repo: GameRepository
): ViewModel() {

    val endlessModeProgress = repo.getEndlessModeProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), GameDefaults.DEFAULT_ENDLESS)
}