package com.example.viktorsjourney.endless.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.navigation.NavHostController
import com.example.viktorsjourney.R
import com.example.viktorsjourney.core.data.models.EndlessAchievement
import com.example.viktorsjourney.core.data.models.EndlessAchievementType
import com.example.viktorsjourney.core.data.navigation.EndlessModeScreen
import com.example.viktorsjourney.core.data.navigation.HomeScreen
import com.example.viktorsjourney.ui.theme.GameTheme

@Composable
fun EndlessAfterGameScreen(
    navigator: NavHostController,
    flightNo: Int,
    score: Int,
    isHighScore: Boolean,
    distance: Int,
    completedAchievements: List<EndlessAchievement>
){

    val scoreNumber = remember { mutableFloatStateOf(0f) }

    val distanceNumber = remember { mutableFloatStateOf(0f) }


    LaunchedEffect(score) {

        val scoreAnimatable = Animatable(0f)
        scoreAnimatable.animateTo(
            targetValue = score.toFloat(),
            animationSpec = tween(
                durationMillis = 500,
                easing = LinearEasing
            )
        ) {
            scoreNumber.floatValue = this.value
        }

        val distanceAnimatable = Animatable(0f)
        distanceAnimatable.animateTo(
            targetValue = distance.toFloat(),
            animationSpec = tween(
                durationMillis = 500,
                easing = LinearEasing
            )
        ) {
            distanceNumber.floatValue = this.value
        }
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
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nice flight!",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 50.dp, bottom = 10.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.padding(bottom = 15.dp)
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
                                    text = "Flight #${flightNo}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(15.dp)
                                )
                                //Data
                                // Score Label
                                Text(
                                    text = if (isHighScore && scoreNumber.floatValue.fastRoundToInt()==score) "New High Score" else "Score",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isHighScore && scoreNumber.floatValue.fastRoundToInt()==score) Color(0xFF990000).copy(alpha = 0.8f) else MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 10.dp).border(
                                        width = 2.dp,
                                        color = if (isHighScore && scoreNumber.floatValue.fastRoundToInt()==score) Color(0xFF990000).copy(alpha = 0.8f) else Color.Transparent,
                                        shape = RoundedCornerShape(7.dp)
                                    )
                                        .padding(vertical = if (isHighScore && scoreNumber.floatValue.fastRoundToInt()==score) 5.dp else 0.dp, horizontal = if (isHighScore && scoreNumber.floatValue.fastRoundToInt()==score) 10.dp else 0.dp)
                                )
                                // Score Value
                                Text(
                                    text = "${scoreNumber.floatValue.fastRoundToInt()}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 7.dp, bottom = 15.dp)
                                )
                                // Distance Label
                                Text(
                                    text = "Distance",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                )
                                // Distance Value
                                Text(
                                    text = "${distanceNumber.floatValue.fastRoundToInt()} m",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 7.dp, bottom = 15.dp)
                                )
                            }
                        }
                    }
                    // Achievements Completed
                    AnimatedVisibility(
                        visible = completedAchievements.isNotEmpty() && distanceNumber.floatValue.fastRoundToInt() == distance,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 30.dp, vertical = 10.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(all = 10.dp)
                            ) {
                                Text(
                                    text = "Achievements Unlocked",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 15.dp)
                                ) {
                                    completedAchievements.forEach {
                                        Icon(
                                            painter = painterResource(it.badgeImage),
                                            contentDescription = it.title,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.padding(horizontal = 10.dp).size(45.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    //Back to Endless Menu Button
                    AnimatedVisibility(
                        visible = distanceNumber.floatValue.fastRoundToInt() == distance,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
                    ) {
                        Box(
                            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                                .height(50.dp)
                                .width(120.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f))
                                .clickable {
                                    navigator.navigate(EndlessModeScreen) {
                                        popUpTo(HomeScreen) {
                        inclusive = false
                    }
                                        launchSingleTop = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Back",
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
}