package com.example.viktorsjourney.endless.presentation.gameplay

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.viktorsjourney.core.data.models.GameCountry

@Composable
fun FuelCellNotice(
    modifier: Modifier,
    currentStatus: Boolean,
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = currentStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Hoverboard Refueled",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun InvinciblePowerupPickedNotice(
    modifier: Modifier,
    invinciblePowerupStatus: Boolean,
    statusBarValue: Float
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = invinciblePowerupStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp, start = 50.dp, end = 50.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = modifier.padding(vertical = 10.dp, horizontal = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Invincible for 10 seconds",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = modifier
                            .padding(bottom = 5.dp)
                    )
                    LinearProgressIndicator(
                        progress = { statusBarValue },
                        trackColor = MaterialTheme.colorScheme.secondary,
                        color = if (statusBarValue>0.3f) Color(0xFFFF8981) else Color.Red,
                        gapSize = (-5).dp,
                        strokeCap = StrokeCap.Round,
                        drawStopIndicator = {},
                        modifier = Modifier.fillMaxWidth().height(6.dp)
                            .border(width = 1.dp, color = Color.Black, RoundedCornerShape(2.5.dp))
                    )
                }
            }
        }
    }
}


@Composable
fun WeaponPowerupPickedNotice(
    modifier: Modifier,
    currentWeaponPickedStatus: Boolean,
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = currentWeaponPickedStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Ammunition Loaded",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun ExtraLifePowerupPickedNotice(
    modifier: Modifier,
    currentExtraLifePickedStatus: Boolean,
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = currentExtraLifePickedStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Extra Life",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun ExtraLifeRespawnNotice(
    modifier: Modifier,
    currentExtraLifeRespawnStatus: Boolean,
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = currentExtraLifeRespawnStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "New Life",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun AnywhereMapPowerupPickedNotice(
    modifier: Modifier,
    currentAnywhereMapPickedStatus: Boolean,
    portalCountry: GameCountry
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = currentAnywhereMapPickedStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Anywhere Map takes you to ${portalCountry.countryName}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun EnemyEntryNotice(
    modifier: Modifier,
    enemyEntryStatus: Boolean,
    enemyName: String
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = enemyEntryStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Defeat ${enemyName}\nor\nSurvive until Airstrike",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun DamageToCharacterNotice(
    modifier: Modifier,
    noticeStatus: Boolean,
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopStart
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = noticeStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopStart)
                    .padding(top = 130.dp, start = 50.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "-20 HP",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Red
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun DamageToEnemyNotice(
    modifier: Modifier,
    noticeStatus: Boolean,
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopEnd
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = noticeStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopEnd)
                    .padding(top = 130.dp, end = 50.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "-20 HP",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun EnemyDefeatedNotice(
    modifier: Modifier,
    noticeStatus: Boolean,
    enemyName: String
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(12f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = noticeStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Defeated $enemyName",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun EnemyAirstrikedNotice(
    modifier: Modifier,
    noticeStatus: Boolean,
    enemyName: String
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(12f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = noticeStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "$enemyName was nuked",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun LowFuelNotice(
    modifier: Modifier,
    lowHoverboardFuelStatus: Boolean
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = lowHoverboardFuelStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.TopCenter)
                    .padding(top = 130.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Low Fuel",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Red
                    ),
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}


@Composable
fun BoxScope.PortalTravelNotice(
    modifier: Modifier,
    portalStatus: Boolean,
    currentLocationHeaderText: String
){
    Box(
        modifier = modifier.fillMaxWidth().zIndex(11f).align(Alignment.BottomCenter),
        contentAlignment = Alignment.TopCenter
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = portalStatus,
            enter = scaleIn(initialScale = 1.4f) + fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Teleporting to " +
                            currentLocationHeaderText.substring(currentLocationHeaderText.lastIndexOf(' ')+1) +
                            "...\nStay Aloft"
                    ,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(vertical = 10.dp, horizontal = 25.dp)
                )
            }
        }
    }
}