package com.example.viktorsjourney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.viktorsjourney.core.data.navigation.GameMainNavigation
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Game App Splash Screen to display while loading assets in background
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        val startupViewModel: GameAppStartupViewModel by viewModel()

        splashScreen.setKeepOnScreenCondition {
            startupViewModel.isLoading
        }

        // Game App Lifecycle
        lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onPause(owner: LifecycleOwner) { // Foreground
                                                   // Dont Attempt to trigger changes if Orientation Changes
                if (!startupViewModel.isLoading && !this@MainActivity.isChangingConfigurations) {
                    startupViewModel.assets.bgmPlayer.pause()
                }
            }

            override fun onResume(owner: LifecycleOwner) {
                if (!startupViewModel.isLoading && !this@MainActivity.isChangingConfigurations) {
                    if (!startupViewModel.assets.bgmPlayer.isPlaying) {
                        startupViewModel.assets.bgmPlayer.start()
                    }
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                if (!startupViewModel.isLoading && !this@MainActivity.isChangingConfigurations) {
                    startupViewModel.assets.bgmPlayer.release()
                }
            }
        })

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb()
            )
        )
        setContent {
            if(!startupViewModel.isLoading){
                GameMainNavigation()
            }
        }
    }
}

