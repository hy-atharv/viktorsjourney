package com.example.viktorsjourney.endless.presentation



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.viktorsjourney.R
import com.example.viktorsjourney.core.data.models.GameStatus
import com.example.viktorsjourney.core.data.models.PreloadedAssets
import com.example.viktorsjourney.core.engine.GameRNG
import com.example.viktorsjourney.core.engine.PlayingSFX
import com.example.viktorsjourney.core.engine.safePlay
import com.example.viktorsjourney.endless.presentation.gameplay.AnywhereMapPowerupPickedNotice
import com.example.viktorsjourney.endless.presentation.gameplay.DamageToCharacterNotice
import com.example.viktorsjourney.endless.presentation.gameplay.DamageToEnemyNotice
import com.example.viktorsjourney.endless.presentation.gameplay.EndlessGameplayCanvas
import com.example.viktorsjourney.endless.presentation.gameplay.EnemyAirstrikedNotice
import com.example.viktorsjourney.endless.presentation.gameplay.EnemyDefeatedNotice
import com.example.viktorsjourney.endless.presentation.gameplay.EnemyEntryNotice
import com.example.viktorsjourney.endless.presentation.gameplay.ExtraLifePowerupPickedNotice
import com.example.viktorsjourney.endless.presentation.gameplay.ExtraLifeRespawnNotice
import com.example.viktorsjourney.endless.presentation.gameplay.FellDownOverlay
import com.example.viktorsjourney.endless.presentation.gameplay.FuelCellNotice
import com.example.viktorsjourney.endless.presentation.gameplay.InvinciblePowerupPickedNotice
import com.example.viktorsjourney.endless.presentation.gameplay.LostToEnemyOverlay
import com.example.viktorsjourney.endless.presentation.gameplay.LowFuelNotice
import com.example.viktorsjourney.endless.presentation.gameplay.ObstacleHitOverlay
import com.example.viktorsjourney.endless.presentation.gameplay.PausedGameOverlay
import com.example.viktorsjourney.endless.presentation.gameplay.PortalTravelNotice
import com.example.viktorsjourney.endless.presentation.gameplay.WeaponPowerupPickedNotice
import com.example.viktorsjourney.ui.theme.GameTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun EndlessGameplayScreen(
    navigator: NavHostController,
    endlessGameViewModel: EndlessGameplayScreenViewModel = koinViewModel<EndlessGameplayScreenViewModel>()
) {
    // Init Location
    val location by endlessGameViewModel.gameCountry.collectAsState()

    // Portal
    val portalStatus = endlessGameViewModel.gameLocationPortalStatus

    // Data from game view model
    val endlessGameStatus = endlessGameViewModel.gameCurrentEndlessStatus

    val currentStatus by rememberUpdatedState(endlessGameStatus)

    val gameMusicPlayer = getKoin().get<PreloadedAssets>().bgmPlayer
    val context = LocalContext.current

    val currentTrackId = endlessGameViewModel.gameCurrentEndlessLocationTrack

    val sfxPool = endlessGameViewModel.sfxPool

    val sfxMap = endlessGameViewModel.sfxAssetsMap

    val soundStatus by endlessGameViewModel.gameSoundStatus.collectAsState()

    val currentLocationHeaderText = endlessGameViewModel.gameCurrentEndlessLocationHeaderText

    val endlessScore = endlessGameViewModel.gameCurrentEndlessScore

    val cannonBallsCounter = endlessGameViewModel.currentCannonBallsCounter

    val activePowerupsList = endlessGameViewModel.activePowerupsList

    val invinciblePowerupStatus = endlessGameViewModel.invinciblePowerupStatus

    val currentInvincibleStatus by rememberUpdatedState(invinciblePowerupStatus)

    val invinciblePowerupStatusBarValue = endlessGameViewModel.invinciblePowerupStatusBarValue

    val extraLifePowerupPicked = endlessGameViewModel.extraLifePowerupPickupStatus

    val extraLifeRespawnStatus = endlessGameViewModel.extraLifeRespawnNoticeStatus

    val weaponPowerupStatus = endlessGameViewModel.weaponPowerupStatus

    val currentWeaponStatus by rememberUpdatedState(weaponPowerupStatus)

    val weaponPowerupPicked = endlessGameViewModel.weaponPowerupPickupStatus

    val anywhereMapPowerupPicked = endlessGameViewModel.anywhereMapPowerupPickupStatus

    val anywhereMapPowerupCountry = endlessGameViewModel.gameLocationPortalCountry

    val enemyStatus = endlessGameViewModel.currentEndlessEnemySpawnStatus

    val enemyEntryStatus = endlessGameViewModel.enemyEntryStatus

    val enemyHealth = endlessGameViewModel.enemyHealth

    val enemyName = endlessGameViewModel.gameCurrentEndlessLocationEnemy.second

    val damageToEnemyNoticeStatus = endlessGameViewModel.damageToEnemyNoticeStatus

    val damageToCharacterNoticeStatus = endlessGameViewModel.damageToCharacterNoticeStatus

    val enemyDefeatedNoticeStatus = endlessGameViewModel.enemyDefeatedNoticeStatus

    val enemyAirstrikedNoticeStatus = endlessGameViewModel.enemyAirstrikedNoticeStatus

    val currentFuelCellStatus = endlessGameViewModel.gameCurrentFuelCellStatus

    val lowHoverboardFuelStatus = endlessGameViewModel.gameCurrentLowHoverboardFuelStatus

    val currentHealth = endlessGameViewModel.gameCurrentCharacterHealth

    val currentHoverboardFuel = endlessGameViewModel.gameCurrentCharacterHoverboardFuel


    // Init Flying
    LaunchedEffect(location) {
        // Init Location
        endlessGameViewModel.updateGameCurrentEndlessLocation(location)

        // Reseeding RNG before every new game
        GameRNG.reseed()

        endlessGameViewModel.engine.setPlatform(
            x = 0f,
            width = endlessGameViewModel.gameCurrentEndlessLocationPlatform.width,
            height = endlessGameViewModel.gameCurrentEndlessLocationPlatform.height
        )
        endlessGameViewModel.engine.setCharacter(
            x = 80f,
            y = 600f,
            width = endlessGameViewModel.flyingFrames[0].width,
            height = endlessGameViewModel.flyingFrames[0].height
        )
        endlessGameViewModel.startCharacterFlyingAnimation()
    }



    GameTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = Color.Black
        ) {
            // PLAYING SCREEN
            Column(
                Modifier
                    .fillMaxSize(),
                //.windowInsetsPadding(WindowInsets.systemBars),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f))
                        //.border(width = 2.dp, color = MaterialTheme.colorScheme.primary)
                        .zIndex(10f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentLocationHeaderText,
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 50.dp, bottom = 15.dp)
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                ){
                    // LEFT CONTROL PAD (FLYING)
                    Box(
                        modifier = Modifier.fillMaxHeight()
                            .fillMaxWidth(0.5f)
                            .align(Alignment.BottomStart)
                            .zIndex(10f)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        // FLYING CONTROL LOGIC
                                        when (currentStatus) {
                                            GameStatus.CANVAS_STARTED -> {
                                                gameMusicPlayer.safePlay(context, currentTrackId)
                                                endlessGameViewModel.engine.start()
                                                endlessGameViewModel.beginGameCurrentEndlessStatus()
                                                endlessGameViewModel.startObstacleSpawns()
                                                endlessGameViewModel.startFuelCellSpawns()
                                                endlessGameViewModel.startPowerupSpawns()
                                                endlessGameViewModel.engine.onFlyingControlTouch()
                                            }
                                            GameStatus.PLAYING -> {
                                                // Wings Flap
                                                sfxPool.safePlay(
                                                    sfxMap[PlayingSFX.WINGS_FLAP]!!,
                                                    1f,
                                                    1f,
                                                    1,
                                                    0,
                                                    1.5f,
                                                    soundStatus
                                                )
                                                endlessGameViewModel.engine.onFlyingControlTouch()
                                            }
                                            else -> {  }
                                        }
                                    }
                                )
                            }
                    )
                    // RIGHT CONTROL PAD (SHOOTING)
                    Box(
                        modifier = Modifier.fillMaxHeight()
                            .fillMaxWidth(0.5f)
                            .align(Alignment.BottomEnd)
                            .zIndex(10f)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        // SHOOTING CONTROL LOGIC
                                        when (currentStatus) {
                                            GameStatus.PLAYING -> {
                                                if (currentWeaponStatus && !currentInvincibleStatus){
                                                    sfxPool.safePlay(
                                                        sfxMap[PlayingSFX.FIRE_CANNONBALL]!!,
                                                        1f,
                                                        1f,
                                                        1,
                                                        0,
                                                        1.5f,
                                                        soundStatus
                                                    )
                                                    endlessGameViewModel.onFireCannonGunControlTouch()
                                                }
                                            }
                                            else -> { }
                                        }
                                    }
                                )
                            }
                    )

                    // MAIN GAMEPLAY RENDER CANVAS
                    EndlessGameplayCanvas(endlessGameViewModel)

                    // PAUSE BUTTON
                    if (currentStatus == GameStatus.PLAYING && !endlessGameViewModel.invinciblePowerupStatus){
                        Box(
                            modifier = Modifier.padding(bottom = 20.dp)
                                .clip(RoundedCornerShape(
                                    topStart = 20.dp,
                                    bottomStart = 20.dp
                                ))
                                .background(color = MaterialTheme.colorScheme.secondary)
                                .align(Alignment.BottomEnd)
                                .zIndex(12f)
                                .clickable {
                                    // Pause Game Logic
                                    endlessGameViewModel.pauseGameCurrentEndlessStatus()
                                }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.pause_icon),
                                contentDescription = "Pause",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(start = 9.dp, end = 9.dp, top = 5.dp, bottom = 5.dp).size(40.dp)
                            )
                        }
                    }

                    // Health, Stamina and Cannon Balls
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp)
                            .zIndex(10f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier.padding(bottom = 10.dp)
                                .width(120.dp)
                                .clip(RoundedCornerShape(
                                    topEnd = 20.dp,
                                    bottomEnd = 20.dp
                                ))
                                .background(color = MaterialTheme.colorScheme.secondary)
                        ){
                            Row (
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ){
                                Icon(
                                    painter = painterResource(R.drawable.health_heart_icon),
                                    contentDescription = "Health",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp).zIndex(1f)
                                )
                                // HEALTH BAR
                                LinearProgressIndicator(
                                    progress = { currentHealth/100f },
                                    trackColor = MaterialTheme.colorScheme.secondary,
                                    color = Color.Red,
                                    gapSize = (-5).dp,
                                    strokeCap = StrokeCap.Round,
                                    drawStopIndicator = {},
                                    modifier = Modifier.fillMaxWidth().height(5.dp).offset(x=(-8).dp)
                                        .border(width = 1.dp, color = Color.Black, RoundedCornerShape(2.5.dp))
                                )
                            }
                        }
                        // HOVERBOARD FUEL BAR
                        Box(
                            modifier = Modifier.padding(bottom = 10.dp)
                                .width(120.dp)
                                .clip(RoundedCornerShape(
                                    topEnd = 20.dp,
                                    bottomEnd = 20.dp
                                ))
                                .background(color = MaterialTheme.colorScheme.secondary)
                        ){
                            Row (
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 10.dp, end = 2.dp)
                            ){
                                Icon(
                                    painter = painterResource(R.drawable.hoverboard_icon),
                                    contentDescription = "Hoverboard Fuel",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(33.dp).offset(x=(-2).dp, y=2.dp).zIndex(1f)
                                )
                                // HOVERBOARD FUEL BAR
                                LinearProgressIndicator(
                                    progress = { currentHoverboardFuel/100f },
                                    trackColor = MaterialTheme.colorScheme.secondary,
                                    color = if (currentHoverboardFuel>30f) Color.Cyan else Color.Red,
                                    gapSize = (-5).dp,
                                    strokeCap = StrokeCap.Round,
                                    drawStopIndicator = {},
                                    modifier = Modifier.fillMaxWidth().height(5.dp).offset(x=(-16).dp)
                                        .border(width = 1.dp, color = Color.Black, RoundedCornerShape(2.5.dp))
                                )
                            }
                        }
                        // Cannon Balls if any
                        if(cannonBallsCounter!=0){
                            Box(
                                modifier = Modifier.padding(bottom = 10.dp)
                                    .clip(RoundedCornerShape(
                                        topEnd = 20.dp,
                                        bottomEnd = 20.dp
                                    ))
                                    .background(color = MaterialTheme.colorScheme.secondary)
                            ){
                                Row (
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ){
                                    Icon(
                                        painter = painterResource(R.drawable.cannonball_icon),
                                        contentDescription = "Cannon Balls Left",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(22.dp).offset(x=(-3).dp)
                                    )
                                    // Cannon Balls Left
                                    Text(
                                        text = "${cannonBallsCounter}/20",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(start = 3.dp, end = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                    // Score and Powerups Active
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp)
                            .align(Alignment.TopEnd)
                            .zIndex(10f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            modifier = Modifier.padding(bottom = 10.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 20.dp,
                                        bottomStart = 20.dp
                                    )
                                )
                                .background(color = MaterialTheme.colorScheme.secondary)
                        ) {
                            // Current Score
                            Text(
                                text = "${endlessScore.fastRoundToInt()}",
                                style = TextStyle(
                                    fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
                                    fontSize = 24.sp,
                                ),
                                color = MaterialTheme.colorScheme.tertiary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.padding(start = 25.dp,end = 15.dp, top = 8.dp, bottom = 8.dp)
                            )
                        }
                        // Current Active Powerups
                        if (!activePowerupsList.isEmpty()){
                            Box(
                                modifier = Modifier.padding(bottom = 10.dp)
                                    .clip(RoundedCornerShape(
                                        topStart = 20.dp,
                                        bottomStart = 20.dp
                                    ))
                                    .background(color = MaterialTheme.colorScheme.secondary)
                            ){
                                Row (
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 15.dp, end = 10.dp)
                                ){
                                    activePowerupsList.forEach { imageBitmap ->
                                        Icon(
                                            painter = BitmapPainter(imageBitmap),
                                            contentDescription = "Active Powerups",
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(37.dp)
                                        )
                                    }
                                }
                            }
                        }
                        // ENEMY HEALTH
                        if (enemyStatus){
                            Box(
                                modifier = Modifier.width(120.dp)
                                    .clip(RoundedCornerShape(
                                        topStart = 20.dp,
                                        bottomStart = 20.dp
                                    ))
                                    .background(color = MaterialTheme.colorScheme.secondary)
                            ){
                                Column (
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(start = 20.dp, end = 10.dp, top = 7.dp, bottom = 7.dp)
                                ){
                                    Text(
                                        text = enemyName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(bottom = 5.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = { enemyHealth/100f },
                                        trackColor = MaterialTheme.colorScheme.secondary,
                                        color = Color.Red,
                                        gapSize = (-5).dp,
                                        strokeCap = StrokeCap.Round,
                                        drawStopIndicator = {},
                                        modifier = Modifier.fillMaxWidth().height(5.dp)
                                            .border(width = 1.dp, color = Color.Black, RoundedCornerShape(2.5.dp))
                                    )
                                }
                            }
                        }
                    }
                    // FUEL CELL
                    FuelCellNotice(modifier = Modifier, currentStatus = currentFuelCellStatus)
                    // LOW HOVERBOARD FUEL
                    LowFuelNotice(modifier = Modifier, lowHoverboardFuelStatus = lowHoverboardFuelStatus)
                    // INVINCIBLE POWERUP
                    InvinciblePowerupPickedNotice(modifier = Modifier, invinciblePowerupStatus = invinciblePowerupStatus, statusBarValue = invinciblePowerupStatusBarValue)
                    // WEAPON POWERUP
                    WeaponPowerupPickedNotice(modifier = Modifier, currentWeaponPickedStatus = weaponPowerupPicked)
                    // EXTRA LIFE POWERUP
                    ExtraLifePowerupPickedNotice(modifier = Modifier, currentExtraLifePickedStatus = extraLifePowerupPicked)
                    ExtraLifeRespawnNotice(modifier = Modifier, currentExtraLifeRespawnStatus = extraLifeRespawnStatus)
                    // ANYWHERE MAP POWERUP AND PORTAL TRAVEL NOTICE
                    AnywhereMapPowerupPickedNotice(modifier = Modifier, currentAnywhereMapPickedStatus = anywhereMapPowerupPicked, portalCountry = anywhereMapPowerupCountry)
                    PortalTravelNotice(modifier = Modifier, portalStatus = portalStatus, currentLocationHeaderText = currentLocationHeaderText)
                    // ENEMY ENTRY NOTICE
                    EnemyEntryNotice(modifier = Modifier, enemyEntryStatus = enemyEntryStatus, enemyName = enemyName)
                    // DAMAGE TO CHARACTER NOTICE
                    DamageToCharacterNotice(modifier = Modifier, noticeStatus = damageToCharacterNoticeStatus)
                    // DAMAGE TO ENEMY NOTICE
                    DamageToEnemyNotice(modifier = Modifier, noticeStatus = damageToEnemyNoticeStatus)
                    // DEFEATED ENEMY NOTICE
                    EnemyDefeatedNotice(modifier = Modifier, noticeStatus = enemyDefeatedNoticeStatus, enemyName = enemyName)
                    // AIRSTRIKED ENEMY NOTICE
                    EnemyAirstrikedNotice(modifier = Modifier, noticeStatus = enemyAirstrikedNoticeStatus, enemyName = enemyName)
                }
            }

            // PAUSED GAME UI
            if(endlessGameStatus==GameStatus.PAUSED){
                PausedGameOverlay(navigator, endlessGameViewModel)
            }
            // OBSTACLE HIT
            else if(endlessGameStatus==GameStatus.ENDLESS_OBSTACLE_HIT_LOSS){
                ObstacleHitOverlay(navigator, endlessGameViewModel)
            }
            // FELL DOWN
            else if(endlessGameStatus==GameStatus.ENDLESS_FELL_DOWN_LOSS){
                FellDownOverlay(navigator, endlessGameViewModel)
            }
            // LOST TO ENEMY
            else if (endlessGameStatus==GameStatus.ENDLESS_DEFEATED_BY_ENEMY){
                LostToEnemyOverlay(navigator, endlessGameViewModel)
            }
        }
    }
}




