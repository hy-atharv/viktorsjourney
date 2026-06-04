package com.example.viktorsjourney.endless.presentation.gameplay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.viktorsjourney.R
import com.example.viktorsjourney.core.data.models.PreloadedAssets
import com.example.viktorsjourney.core.data.navigation.EndlessAfterGameScreen
import com.example.viktorsjourney.core.data.navigation.EndlessGameplayScreen
import com.example.viktorsjourney.core.data.navigation.HomeScreen
import com.example.viktorsjourney.core.engine.safePlay
import com.example.viktorsjourney.endless.presentation.EndlessGameplayScreenViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun ObstacleHitOverlay(
    navigator: NavHostController,
    endlessGameViewModel: EndlessGameplayScreenViewModel = koinViewModel<EndlessGameplayScreenViewModel>()
) {
    val gameMusicPlayer = getKoin().get<PreloadedAssets>().bgmPlayer
    val context = LocalContext.current

    var show by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1000)
        show = true
    }

    AnimatedVisibility(
        visible = show,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f))
                .zIndex(15f),
            contentAlignment = Alignment.Center
        ){
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(color = MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.TopCenter
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(vertical = 15.dp)
                ) {
                    Text(
                        text = "Watch the skies...",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.headlineMedium.fontFamily,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.hit_by_obstacle_frame),
                        contentDescription = "Obstacle Hit Game Frame",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(bottom = 5.dp).size(190.dp)
                    )
                    Box(
                        modifier = Modifier.padding(bottom = 15.dp)
                            .height(40.dp)
                            .width(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.primary)
                            .clickable {
                                // Save progress and achievements first
                                endlessGameViewModel.updateEndlessProgressAndAchievements()
                                // Restart to new Gameplay Screen
                                navigator.navigate(EndlessGameplayScreen) {
                                    popUpTo(EndlessGameplayScreen) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                                gameMusicPlayer.stop()
                            },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Play Again",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .height(40.dp)
                            .width(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.primary)
                            .clickable {
                                // Save progress and achievements first
                                val (completedAchievements, isHighScore, flightNo) = endlessGameViewModel.updateEndlessProgressAndAchievements()
                                // Navigate to Results/Stats Screen
                                navigator.navigate(
                                    EndlessAfterGameScreen(
                                        score = endlessGameViewModel.gameCurrentEndlessScore.fastRoundToInt(),
                                        isHighScore = isHighScore,
                                        distance = endlessGameViewModel.gameCurrentEndlessDistance,
                                        completedAchievements = completedAchievements,
                                        flightNo = flightNo
                                    )
                                ) {
                                    popUpTo(HomeScreen) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                                // Switch to UI Music
                                gameMusicPlayer.safePlay(context, R.raw.game_music)
                            },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Leave",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}