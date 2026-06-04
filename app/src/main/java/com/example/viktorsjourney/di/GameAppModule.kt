package com.example.viktorsjourney.di

import com.example.viktorsjourney.GameAppStartupViewModel
import com.example.viktorsjourney.core.data.GameDataSource
import com.example.viktorsjourney.core.data.GameRepository
import com.example.viktorsjourney.endless.presentation.EndlessAchievementsScreenViewModel
import com.example.viktorsjourney.endless.presentation.EndlessGameplayScreenViewModel
import com.example.viktorsjourney.endless.presentation.EndlessModeScreenViewModel
import com.example.viktorsjourney.endless.presentation.SelectMapScreenViewModel
import com.example.viktorsjourney.home.presentation.HomeScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val gameAppModule = module {
    viewModel { GameAppStartupViewModel(get(), get()) }
    single<GameRepository> { GameDataSource(get()) }
    viewModel { HomeScreenViewModel(get()) }
    viewModel { EndlessModeScreenViewModel(get()) }
    viewModel { EndlessAchievementsScreenViewModel(get()) }
    viewModel { SelectMapScreenViewModel(get()) }
    viewModel { EndlessGameplayScreenViewModel(get(), get()) }
}