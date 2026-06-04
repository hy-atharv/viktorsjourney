package com.example.viktorsjourney.endless.presentation


import android.R.attr.top
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.viktorsjourney.R
import com.example.viktorsjourney.core.data.models.PreloadedAssets
import com.example.viktorsjourney.core.data.navigation.EndlessAchievementsScreen
import com.example.viktorsjourney.core.data.navigation.EndlessGameplayScreen
import com.example.viktorsjourney.core.data.navigation.HomeScreen
import com.example.viktorsjourney.core.data.navigation.SelectMapsScreen
import com.example.viktorsjourney.ui.theme.GameTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun EndlessModeScreen(
    navigator: NavHostController,
    viewModel: EndlessModeScreenViewModel = koinViewModel<EndlessModeScreenViewModel>()
){
    val gameUIMusicPlayer = getKoin().get<PreloadedAssets>().bgmPlayer

    val endlessModeProgress by viewModel.endlessModeProgress.collectAsState()

    val scrollScale = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        scrollScale.animateTo(
            1f,
            animationSpec = tween(250)
        )
    }

    GameTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(R.drawable.game_uiux_screen),
                    contentScale = ContentScale.Crop
                ),
            color = Color.Transparent
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                    //.windowInsetsPadding(WindowInsets.systemBars),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        .border(width = 2.dp, color = MaterialTheme.colorScheme.secondary),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Back to MAIN MENU",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 50.dp, bottom = 10.dp, start = 15.dp, end = 15.dp).size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                navigator.navigate(HomeScreen){
                                    popUpTo(HomeScreen) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                    )
                    Text(
                        text = "Menu",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 50.dp, bottom = 10.dp)
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.achievements_icon),
                        contentDescription = "Endless Achievements",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 50.dp, bottom = 10.dp, start = 15.dp, end = 15.dp).size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                navigator.navigate(EndlessAchievementsScreen)
                            }
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer{
                                scaleY = scrollScale.value
                                scaleX = scrollScale.value
                            }
                    ) {
                        // SCROLL
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 35.dp, bottom = 5.dp)
                                .paint(
                                    painter = painterResource(R.drawable.scroll),
                                    contentScale = ContentScale.Crop,
                                ),
                            contentAlignment = Alignment.TopCenter
                        ) { // SCROLL INSIDE CONTENTS

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top,
                                modifier = Modifier.padding(top = 60.dp)
                            ) {
                                //Stats Title
                                Text(
                                    text = "Statistics",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(15.dp)
                                )
                                //Data
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.padding(top = 5.dp)
                                ) {
                                    //Fields Column
                                    Column(
                                        modifier = Modifier.padding(top = 10.dp),
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Text(
                                            text = "Highest Score:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                        Text(
                                            text = "Longest Distance:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                        Text(
                                            text = "Longest Time:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                        Text(
                                            text = "Most Enemies Defeated:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                    }
                                    //Values Column
                                    Column(
                                        modifier = Modifier.padding(top = 10.dp),
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Text(
                                            text = "${endlessModeProgress.highestScore}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                        Text(
                                            text = "${endlessModeProgress.longestDistance} m",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                        Text(
                                            text = endlessModeProgress.longestTime,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                        Text(
                                            text = "${endlessModeProgress.mostEnemiesDefeated}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(5.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "in a single flight...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 15.dp)
                                )
                            }
                        }
                    }
                    //Select Map Button
                    Box(
                        modifier = Modifier.padding(top = 50.dp, bottom = 20.dp)
                            .height(50.dp)
                            .width(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f))
                            .clickable {
                                navigator.navigate(SelectMapsScreen)
                            },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Select Map",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    //Play Button
                    Box(
                        modifier = Modifier.padding(bottom = 20.dp)
                            .height(50.dp)
                            .width(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f))
                            .clickable {
                                //Play Endless Logic
                                navigator.navigate(EndlessGameplayScreen){
                                    popUpTo(HomeScreen) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                                gameUIMusicPlayer.pause()
                            },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Play",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}