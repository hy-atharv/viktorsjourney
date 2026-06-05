package com.example.viktorsjourney

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
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
    // Fixing Viewport Density for any device and overriding device zoom scale
    override fun attachBaseContext(newBase: Context) {
        val overrideConfig = Configuration(newBase.resources.configuration).apply {
            val windowManager = newBase.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            val pixelWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                windowManager.currentWindowMetrics.bounds.width()
            } else {
                // Legacy fallback for old Android devices
                @Suppress("DEPRECATION")
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.getRealMetrics(displayMetrics)
                displayMetrics.widthPixels
            }

            val targetDpWidth = 360

            // Compute structural viewport scale density transformations
            densityDpi = (pixelWidth.toFloat() / targetDpWidth * 160f).toInt()

            fontScale = 1.0f

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                fontWeightAdjustment = 0
            }
        }

        val context = newBase.createConfigurationContext(overrideConfig)
        super.attachBaseContext(context)
    }


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

