package com.example.viktorsjourney.endless.presentation

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.viktorsjourney.R
import com.example.viktorsjourney.core.data.models.EndlessAchievement
import com.example.viktorsjourney.ui.theme.GameTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun EndlessAchievementsScreen(
    navigator: NavHostController,
    viewModel: EndlessAchievementsScreenViewModel = koinViewModel<EndlessAchievementsScreenViewModel>()
) {
    val sortedEndlessAchievements by viewModel.orderedEndlessAchievements.collectAsState()

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
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Endless Screen",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 50.dp, bottom = 10.dp, start = 15.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                navigator.popBackStack()
                            }
                    )
                    Text(
                        text = "Achievements",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 9.dp)
                            .padding(top = 50.dp, bottom = 10.dp)
                    )
                }
                // Achievements Column
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 20.dp, bottom = 30.dp)
                ) {
                    items(sortedEndlessAchievements.size){ achievementIndex ->
                        AchievementCard(sortedEndlessAchievements[achievementIndex])
                    }
                }
            }
        }
    }
}


@Composable
fun AchievementCard(
    achievement: EndlessAchievement
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding( horizontal = 20.dp, vertical = 7.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(achievement.badgeImage),
                contentDescription = achievement.title,
                tint = Color.Unspecified,
                modifier = Modifier.size(50.dp)
                    .alpha(
                        if (achievement.isCompleted) 1f
                        else 0.2f
                    )
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
                Text(
                    text = if(achievement.isCompleted) achievement.completedDescription else achievement.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                if(!achievement.isCompleted){
                    LinearProgressIndicator(
                        progress = { achievement.current/achievement.target },
                        trackColor = MaterialTheme.colorScheme.primary,
                        color = MaterialTheme.colorScheme.tertiary,
                        gapSize = (-5).dp,
                        strokeCap = StrokeCap.Round,
                        drawStopIndicator = {},
                        modifier = Modifier.fillMaxWidth().height(5.dp)
                    )
                }
            }
        }
    }
}
