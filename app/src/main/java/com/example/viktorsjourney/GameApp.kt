package com.example.viktorsjourney

import android.app.Application
import com.example.viktorsjourney.di.gameAppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GameApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GameApp)
            androidLogger()
            modules(gameAppModule)
        }

    }

}