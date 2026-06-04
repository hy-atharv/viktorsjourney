package com.example.viktorsjourney.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.viktorsjourney.R

@Composable
fun Credits(
    showCredits: MutableState<Boolean>,
    showSettings: MutableState<Boolean>,
){
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.5f))
            .zIndex(5f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.credits),
                contentDescription = "Credits",
                modifier = Modifier.fillMaxWidth()
            )
            // Back Button
            Box(
                modifier = Modifier.padding(top = 25.dp)
                    .height(50.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.primary)
                    .clickable {
                        // Back to Settings
                        showCredits.value = false
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