package com.example.viktorsjourney.home.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viktorsjourney.R
import com.example.viktorsjourney.core.data.GameRepository
import com.example.viktorsjourney.core.data.models.GameCountry
import com.example.viktorsjourney.core.data.models.PreloadedAssets
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin

class HomeScreenViewModel(
    private val repo: GameRepository
) : ViewModel() {

    private val gameMusicPlayer = getKoin().get<PreloadedAssets>().bgmPlayer

    private val defaultCountry = getKoin().get<PreloadedAssets>().homeScreenCountry

    val gameCountry = repo.getGameCountry()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), defaultCountry)

    val countryImageId: StateFlow<Int> = gameCountry
        .map { country -> getCountryScreenImage(country) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), getCountryScreenImage(defaultCountry))

    private fun getCountryScreenImage(country: GameCountry): Int {
        val resId = when (country) {
            GameCountry.INDIA -> R.drawable.india_homescreen
            GameCountry.JAPAN -> R.drawable.japan_homescreen
            GameCountry.EGYPT -> R.drawable.egypt_homescreen
            GameCountry.CROATIA -> R.drawable.croatia_homescreen
            GameCountry.RUSSIA -> R.drawable.russia_homescreen
        }
        return resId
    }

    val gameSoundStatus = repo.getGameSoundStatus()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun switchGameSoundStatus() {
        if (gameSoundStatus.value){
            gameMusicPlayer.setVolume(0.0f, 0.0f)
        }
        else{
            gameMusicPlayer.setVolume(1.0f, 1.0f)
        }
        viewModelScope.launch {
            repo.setGameSoundStatus(!gameSoundStatus.value)
        }
    }
}
