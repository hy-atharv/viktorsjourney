package com.example.viktorsjourney.home.presentation


import android.app.ProgressDialog.show
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.viktorsjourney.R
import com.example.viktorsjourney.core.data.navigation.EndlessModeScreen
import com.example.viktorsjourney.core.util.sendEmailIntent
import com.example.viktorsjourney.ui.theme.GameTheme
import org.koin.androidx.compose.koinViewModel
import com.example.viktorsjourney.BuildConfig.VERSION_NAME as BuildC

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = koinViewModel<HomeScreenViewModel>(),
    navigator: NavHostController
){
    val country by viewModel.gameCountry.collectAsState()
    val countryScreenImage by viewModel.countryImageId.collectAsState()


    val infiniteTransition = rememberInfiniteTransition(label = "buttonPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonScaleAnim"
    )

    val showSettings = remember { mutableStateOf(false) }

    val showTutorial = remember { mutableStateOf(false) }

    val showCredits = remember { mutableStateOf(false) }

    val soundStatus by viewModel.gameSoundStatus.collectAsState()

    val contentInteractionSource = remember { MutableInteractionSource() }

    val context = LocalContext.current

    GameTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(countryScreenImage),
                    contentScale = ContentScale.Crop
                ),
            color = Color.Transparent
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Viktor's Journey",
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.primary,
                            blurRadius = 1f,
                            offset = Offset(0f,-4.0f)
                        )
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 40.dp, bottom = 20.dp)
                )
                //Play Button
                Box(
                    modifier = Modifier.padding(top = 20.dp)
                ){
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(shape = CircleShape)
                            .background(color = MaterialTheme.colorScheme.primary)
                            .clickable {
                                navigator.navigate(EndlessModeScreen)
                            }
                    ){
                        Icon(
                            painter = painterResource(R.drawable.play_icon),
                            contentDescription = "Play Button",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(50.dp).padding(start = 5.dp)
                                .align(Alignment.Center)
                                .graphicsLayer(
                                    scaleY = scale,
                                    scaleX = scale
                                ),
                        )
                    }
                }
            }
            // Screen Place Info
            Box(
                modifier = Modifier.padding(20.dp),
                contentAlignment = Alignment.BottomCenter
            ){
                Text(
                    text = "${country.placeName}, ${country.countryName}",
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 50.dp, bottom = 20.dp)
                )
            }
            // Settings Button
            Box(
                modifier = Modifier.padding(end = 20.dp, bottom = 100.dp),
                contentAlignment = Alignment.BottomEnd
            ){
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primary)
                        .clickable {
                            showSettings.value = true
                        }
                ){
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings Button",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(25.dp)
                            .align(Alignment.Center),
                    )
                }
            }
            // Settings Panel
            AnimatedVisibility(
                visible = showSettings.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(color = Color.Black.copy(alpha = 0.5f))
                        .zIndex(5f)
                        .clickable(
                            indication = null,
                            interactionSource = contentInteractionSource
                        ){
                            showSettings.value = false
                        },
                    contentAlignment = Alignment.Center
                ){
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(color = MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier.padding(vertical = 15.dp)
                        ) {
                            // Credits
                            Box(
                                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                                    .height(50.dp)
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(color = MaterialTheme.colorScheme.primary)
                                    .clickable {
                                        showCredits.value = true
                                        showSettings.value = false
                                    },
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = "Credits",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontSize = 25.sp
                                )
                            }
                            // Tutorial
                            Box(
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                                    .height(50.dp)
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(color = MaterialTheme.colorScheme.primary)
                                    .clickable {
                                        showTutorial.value = true
                                        showSettings.value = false
                                    },
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = "Tutorial",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontSize = 25.sp
                                )
                            }
                            // Sound
                            Box(
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                                    .height(50.dp)
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(color = MaterialTheme.colorScheme.primary)
                                    .clickable {
                                        viewModel.switchGameSoundStatus()
                                    },
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = if (soundStatus) "Turn Sound Off" else "Turn Sound On",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontSize = 25.sp
                                )
                            }
                            // Feedback
                            Box(
                                modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                                    .height(50.dp)
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(color = MaterialTheme.colorScheme.primary)
                                    .clickable {
                                        sendEmailIntent(context, "atharvkumartiwari@gmail.com", "Viktor's Journey Feedback")
                                    },
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = "Share Feedback",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontSize = 25.sp
                                )
                            }


                            // Version
                            Text(
                                text = "Version $BuildC",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            // Tutorial Panel
            AnimatedVisibility(
                visible = showTutorial.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Tutorial(showTutorial, showSettings)
            }
            // Credits Panel
            AnimatedVisibility(
                visible = showCredits.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Credits(showCredits, showSettings)
            }
        }
    }
}