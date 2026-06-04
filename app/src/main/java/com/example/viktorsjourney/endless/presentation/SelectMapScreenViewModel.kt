package com.example.viktorsjourney.endless.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viktorsjourney.core.data.GameDefaults
import com.example.viktorsjourney.core.data.GameRepository
import com.example.viktorsjourney.core.data.models.GameCountry
import com.example.viktorsjourney.core.data.models.Map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectMapScreenViewModel(
    private val repo: GameRepository
): ViewModel() {

    private val gameCountry = repo.getGameCountry()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), GameDefaults.DEFAULT_COUNTRY)

    val mapsList = listOf<Map>(
        Map.Croatia,
        Map.Japan,
        Map.Russia,
        Map.Egypt,
        Map.India
    )

    val hopCountForLastChosenMapIndex = gameCountry
        .map { country ->
            when (country) {
                GameCountry.CROATIA -> 0
                GameCountry.JAPAN   -> 1
                GameCountry.RUSSIA  -> 2
                GameCountry.EGYPT   -> 3
                GameCountry.INDIA   -> 4
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    fun updateGameCountry(country: GameCountry) {
        viewModelScope.launch {
            repo.setGameCountry(country)
        }
    }
}