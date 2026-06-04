package com.example.viktorsjourney.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.viktorsjourney.R

@Composable
fun Tutorial(
    showTutorial: MutableState<Boolean>,
    showSettings: MutableState<Boolean>,
){
    val tutorialList = listOf<Int>(
        R.drawable.tutorial_1,
        R.drawable.tutorial_2,
        R.drawable.tutorial_3,
        R.drawable.tutorial_4,
        R.drawable.tutorial_5,
        R.drawable.tutorial_6
    )

    val currentPage = remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
            .zIndex(5f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                // List Carousal
                Image(
                    painter = painterResource(tutorialList[currentPage.intValue]),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.FillWidth
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(70.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.primary)
                            .clickable {
                                if (currentPage.intValue > 0) {
                                    currentPage.intValue--
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Prev",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 20.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(70.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.primary)
                            .clickable {
                                if (currentPage.intValue < tutorialList.lastIndex) {
                                    currentPage.intValue++
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Next",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 20.sp
                        )
                    }
                }

                // Back Button
                Box(
                    modifier = Modifier.padding(top = 25.dp, bottom = 20.dp)
                        .height(50.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(color = MaterialTheme.colorScheme.primary)
                        .clickable {
                            // Back to Settings
                            showTutorial.value = false
                            showSettings.value = true
                        },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Back",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 25.sp
                    )
                }
            }
        }
    }
}