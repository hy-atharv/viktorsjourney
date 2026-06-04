package com.example.viktorsjourney.endless.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.util.fastRoundToInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.viktorsjourney.core.data.GameDefaults
import com.example.viktorsjourney.core.data.GameRepository
import com.example.viktorsjourney.core.data.models.CurrentActiveAirstrikeMissile
import com.example.viktorsjourney.core.data.models.CurrentActiveCharacterCannonBall
import com.example.viktorsjourney.core.data.models.CurrentActiveFuelCell
import com.example.viktorsjourney.core.data.models.CurrentActiveEnemyWeapon
import com.example.viktorsjourney.core.data.models.CurrentActiveFighterJet
import com.example.viktorsjourney.core.data.models.CurrentActiveObstacleBody
import com.example.viktorsjourney.core.data.models.CurrentActiveObstacleHead
import com.example.viktorsjourney.core.data.models.CurrentActivePowerup
import com.example.viktorsjourney.core.data.models.EndlessAchievement
import com.example.viktorsjourney.core.data.models.EndlessAchievementType
import com.example.viktorsjourney.core.data.models.EndlessModeProgress
import com.example.viktorsjourney.core.data.models.EndlessScorePoints
import com.example.viktorsjourney.core.data.models.GameCountry
import com.example.viktorsjourney.core.data.models.GameStatus
import com.example.viktorsjourney.core.data.models.ObstacleSourceType
import com.example.viktorsjourney.core.data.models.Powerups
import com.example.viktorsjourney.core.data.models.PreloadedAssets
import com.example.viktorsjourney.core.engine.EndlessGameEngine
import com.example.viktorsjourney.core.engine.GameObject
import com.example.viktorsjourney.core.engine.GameRNG
import com.example.viktorsjourney.core.engine.PlayingSFX
import com.example.viktorsjourney.core.engine.safePlay
import com.example.viktorsjourney.core.util.parseTimeToMinutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin

class EndlessGameplayScreenViewModel(
    application: Application,
    private val repo: GameRepository,
): AndroidViewModel(application) {

    //--------------------- Prerequisite Endless Game Data & Assets ---------------------------------------

    private val appContext = getApplication<Application>().applicationContext

    var currentEndlessGameStartTimestamp by mutableLongStateOf(0L)
        private set

    var currentEndlessGameStopTimestamp by mutableLongStateOf(0L)
        private set

    private val gameMusicPlayer = getKoin().get<PreloadedAssets>().bgmPlayer

    val sfxPool = getKoin().get<PreloadedAssets>().sfxPool

    val gameSoundStatus = repo.getGameSoundStatus()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val sfxAssetsMap = mapOf<PlayingSFX, Int>(
        PlayingSFX.OBSTACLE_HIT to getKoin().get<PreloadedAssets>().sfxAssets.obstacleHitId,
        PlayingSFX.POWERUP_PICKUP to getKoin().get<PreloadedAssets>().sfxAssets.powerupPickupId,
        PlayingSFX.FUEL_CELL_PICKUP to getKoin().get<PreloadedAssets>().sfxAssets.fuelCellPickupId,
        PlayingSFX.WINGS_FLAP to getKoin().get<PreloadedAssets>().sfxAssets.wingsFlapId,
        PlayingSFX.FALLING_DOWN to getKoin().get<PreloadedAssets>().sfxAssets.fallingDownId,
        PlayingSFX.FIRE_CANNONBALL to getKoin().get<PreloadedAssets>().sfxAssets.firingCannonBallId,
        PlayingSFX.CANNONBALL_HIT_ON_ENEMY to getKoin().get<PreloadedAssets>().sfxAssets.cannonBallHitId,
        PlayingSFX.HEALTH_DAMAGE_BY_ENEMY to getKoin().get<PreloadedAssets>().sfxAssets.healthDamageId,
        PlayingSFX.AIRSTRIKE_JET_PASSBY to getKoin().get<PreloadedAssets>().sfxAssets.airstrikeJetPassById,
        PlayingSFX.AIRSTRIKE_EXPLOSION to getKoin().get<PreloadedAssets>().sfxAssets.airstrikeExplosionId,
        PlayingSFX.LOSE_FIGHT_MUSIC to getKoin().get<PreloadedAssets>().sfxAssets.loseFightMusicId,
        PlayingSFX.WIN_FIGHT_MUSIC to getKoin().get<PreloadedAssets>().sfxAssets.winFightMusicId
    )

    private val endlessPortalBackgroundBitmap = getKoin().get<PreloadedAssets>().endlessPortalBackground

    private val locationTrackMap = mapOf<GameCountry, Int>(
        GameCountry.CROATIA to getKoin().get<PreloadedAssets>().croatiaMap.locationAssets.trackId,
        GameCountry.JAPAN   to getKoin().get<PreloadedAssets>().japanMap.locationAssets.trackId,
        GameCountry.RUSSIA  to getKoin().get<PreloadedAssets>().russiaMap.locationAssets.trackId,
        GameCountry.EGYPT   to getKoin().get<PreloadedAssets>().egyptMap.locationAssets.trackId,
        GameCountry.INDIA   to getKoin().get<PreloadedAssets>().indiaMap.locationAssets.trackId
    )

    private val portalMusicId = getKoin().get<PreloadedAssets>().portalMusicId

    private val locationsHeaderTextMap = mapOf<GameCountry, String>(
        GameCountry.CROATIA to "${GameCountry.CROATIA.placeName}, ${GameCountry.CROATIA.countryName}",
        GameCountry.JAPAN   to "${GameCountry.JAPAN.placeName}, ${GameCountry.JAPAN.countryName}",
        GameCountry.RUSSIA  to "${GameCountry.RUSSIA.placeName}, ${GameCountry.RUSSIA.countryName}",
        GameCountry.EGYPT   to "${GameCountry.EGYPT.placeName}, ${GameCountry.EGYPT.countryName}",
        GameCountry.INDIA   to "${GameCountry.INDIA.placeName}, ${GameCountry.INDIA.countryName}"
    )

    private val locationsDayBackgroundMap = mapOf<GameCountry, ImageBitmap>(
        GameCountry.CROATIA to getKoin().get<PreloadedAssets>().croatiaMap.locationAssets.dayBackground,
        GameCountry.JAPAN   to getKoin().get<PreloadedAssets>().japanMap.locationAssets.dayBackground,
        GameCountry.RUSSIA  to getKoin().get<PreloadedAssets>().russiaMap.locationAssets.dayBackground,
        GameCountry.EGYPT   to getKoin().get<PreloadedAssets>().egyptMap.locationAssets.dayBackground,
        GameCountry.INDIA   to getKoin().get<PreloadedAssets>().indiaMap.locationAssets.dayBackground
    )

    private val locationsNightBackgroundMap = mapOf<GameCountry, ImageBitmap>(
        GameCountry.CROATIA to getKoin().get<PreloadedAssets>().croatiaMap.locationAssets.nightBackground,
        GameCountry.JAPAN   to getKoin().get<PreloadedAssets>().japanMap.locationAssets.nightBackground,
        GameCountry.RUSSIA  to getKoin().get<PreloadedAssets>().russiaMap.locationAssets.nightBackground,
        GameCountry.EGYPT   to getKoin().get<PreloadedAssets>().egyptMap.locationAssets.nightBackground,
        GameCountry.INDIA   to getKoin().get<PreloadedAssets>().indiaMap.locationAssets.nightBackground
    )

    private val locationsPlatformMap = mapOf<GameCountry, ImageBitmap>(
        GameCountry.CROATIA to getKoin().get<PreloadedAssets>().croatiaMap.locationAssets.platform,
        GameCountry.JAPAN   to getKoin().get<PreloadedAssets>().japanMap.locationAssets.platform,
        GameCountry.RUSSIA  to getKoin().get<PreloadedAssets>().russiaMap.locationAssets.platform,
        GameCountry.EGYPT   to getKoin().get<PreloadedAssets>().egyptMap.locationAssets.platform,
        GameCountry.INDIA   to getKoin().get<PreloadedAssets>().indiaMap.locationAssets.platform
    )

    private val locationsObstacleHeadMap = mapOf<GameCountry, ImageBitmap>(
        GameCountry.CROATIA to getKoin().get<PreloadedAssets>().croatiaMap.obstacleAssets.obstacleHead,
        GameCountry.JAPAN   to getKoin().get<PreloadedAssets>().japanMap.obstacleAssets.obstacleHead,
        GameCountry.RUSSIA  to getKoin().get<PreloadedAssets>().russiaMap.obstacleAssets.obstacleHead,
        GameCountry.EGYPT   to getKoin().get<PreloadedAssets>().egyptMap.obstacleAssets.obstacleHead,
        GameCountry.INDIA   to getKoin().get<PreloadedAssets>().indiaMap.obstacleAssets.obstacleHead
    )

    private val locationsObstacleBodyMap = mapOf<GameCountry, ImageBitmap>(
        GameCountry.CROATIA to getKoin().get<PreloadedAssets>().croatiaMap.obstacleAssets.obstacleBody,
        GameCountry.JAPAN   to getKoin().get<PreloadedAssets>().japanMap.obstacleAssets.obstacleBody,
        GameCountry.RUSSIA  to getKoin().get<PreloadedAssets>().russiaMap.obstacleAssets.obstacleBody,
        GameCountry.EGYPT   to getKoin().get<PreloadedAssets>().egyptMap.obstacleAssets.obstacleBody,
        GameCountry.INDIA   to getKoin().get<PreloadedAssets>().indiaMap.obstacleAssets.obstacleBody
    )

    val powerupsMap = mapOf<Powerups, ImageBitmap>(
        Powerups.INVINCIBLE to getKoin().get<PreloadedAssets>().shieldIcon,
        Powerups.AMMUNITION to getKoin().get<PreloadedAssets>().ammunitionIcon,
        Powerups.EXTRA_LIFE to getKoin().get<PreloadedAssets>().extraLifeIcon,
        Powerups.ANYWHERE_MAP to getKoin().get<PreloadedAssets>().anywhereMap
    )

    val enemiesAndWeaponsMap = mapOf<GameCountry, Triple<ImageBitmap, String, ImageBitmap>>(
        GameCountry.CROATIA to Triple(
            getKoin().get<PreloadedAssets>().croatiaMap.enemyAssets.enemyFrame,
            getKoin().get<PreloadedAssets>().croatiaMap.enemyAssets.enemyName,
            getKoin().get<PreloadedAssets>().croatiaMap.enemyAssets.enemyWeapon
        ),
        GameCountry.JAPAN to Triple(
            getKoin().get<PreloadedAssets>().japanMap.enemyAssets.enemyFrame,
            getKoin().get<PreloadedAssets>().japanMap.enemyAssets.enemyName,
            getKoin().get<PreloadedAssets>().japanMap.enemyAssets.enemyWeapon
        ),
        GameCountry.RUSSIA to Triple(
            getKoin().get<PreloadedAssets>().russiaMap.enemyAssets.enemyFrame,
            getKoin().get<PreloadedAssets>().russiaMap.enemyAssets.enemyName,
            getKoin().get<PreloadedAssets>().russiaMap.enemyAssets.enemyWeapon
        ),
        GameCountry.EGYPT to Triple(
            getKoin().get<PreloadedAssets>().egyptMap.enemyAssets.enemyFrame,
            getKoin().get<PreloadedAssets>().egyptMap.enemyAssets.enemyName,
            getKoin().get<PreloadedAssets>().egyptMap.enemyAssets.enemyWeapon
        ),
        GameCountry.INDIA to Triple(
            getKoin().get<PreloadedAssets>().indiaMap.enemyAssets.enemyFrame,
            getKoin().get<PreloadedAssets>().indiaMap.enemyAssets.enemyName,
            getKoin().get<PreloadedAssets>().indiaMap.enemyAssets.enemyWeapon
        )
    )

    val healthPowerupsList = listOf<Powerups>(
        Powerups.INVINCIBLE,
        Powerups.EXTRA_LIFE
    )

    val normalPowerupsList = listOf<Powerups>(
        Powerups.INVINCIBLE,
        Powerups.AMMUNITION,
        Powerups.EXTRA_LIFE
    )
    
    val fuelCellImage = getKoin().get<PreloadedAssets>().fuelCell

    val cannonBall = getKoin().get<PreloadedAssets>().cannonBall

    val airstrikeFrame = getKoin().get<PreloadedAssets>().atharvAirstrike

    val airstrikeMissile = getKoin().get<PreloadedAssets>().airstrikeMissile

    val airstrikeMissileExplosion = getKoin().get<PreloadedAssets>().airstrikeMissileExplosion

    val flyingFrames = listOf<ImageBitmap>(
        getKoin().get<PreloadedAssets>().character.flying1,
        getKoin().get<PreloadedAssets>().character.flying2
    )

    val flyingFirmFrame = getKoin().get<PreloadedAssets>().character.flyingPowerup

    val obstacleHitFrame = getKoin().get<PreloadedAssets>().character.obstacleHitFrame

    val fallingDownFrame = getKoin().get<PreloadedAssets>().character.noWingsFrame

    val invincibleFrame = getKoin().get<PreloadedAssets>().character.flyingPowerup

    val firingCannonGunFrame = getKoin().get<PreloadedAssets>().character.firingCannonGun


    //--------------------------- Game Engine----------------------------------------------------------------

    val engine = EndlessGameEngine(this)



    //----------------------- Game State and Data Management -------------------------------------------------------

    // Game Locations Data State Management

    val gameCountry = repo.getGameCountry()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), GameDefaults.DEFAULT_COUNTRY)

    private val endlessModeProgress = repo.getEndlessModeProgress()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameDefaults.DEFAULT_ENDLESS)

    private val endlessAchievementsMap = repo.getEndlessAchievements()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameDefaults.DEFAULT_ENDLESS_ACHIEVEMENTS)

    var gameCurrentEndlessLocation by mutableStateOf<GameCountry>(GameDefaults.DEFAULT_COUNTRY)
        private set // Privately Set by Class Functions, but Publicly gettable

    var gameCurrentEndlessLocationTrack by mutableIntStateOf(
        locationTrackMap[gameCurrentEndlessLocation]!!
    )

    var gameCurrentEndlessLocationHeaderText by mutableStateOf<String>(
        locationsHeaderTextMap[gameCurrentEndlessLocation]!!
    )
        private set

    var gameCurrentEndlessLocationBackgroundBitmap by mutableStateOf<ImageBitmap>(
        locationsDayBackgroundMap[gameCurrentEndlessLocation]!!
    )
        private set

    private var gameCurrentEndlessLocationNightModeStatus by mutableStateOf<Boolean>(false)

    var gameLocationPortalStatus by mutableStateOf<Boolean>(false)
        private set

    var gameLocationPortalCountry by mutableStateOf<GameCountry>(GameDefaults.DEFAULT_COUNTRY)
        private set

    var gameLocationsTrackerSet = mutableStateSetOf<GameCountry>(gameCurrentEndlessLocation)
        private set

    fun enableGameLocationPortal() {
        stopObstacleSpawns()
        gameLocationPortalStatus = true
        gameCurrentEndlessLocationBackgroundBitmap = endlessPortalBackgroundBitmap
        gameCurrentEndlessLocationHeaderText = "Portal to ${gameLocationPortalCountry.countryName}"
        gameCurrentEndlessLocationTrack = portalMusicId
        gameMusicPlayer.safePlay(appContext, gameCurrentEndlessLocationTrack)
    }

    fun disableGameLocationPortal() {
        viewModelScope.launch {
            repo.setGameCountry(gameLocationPortalCountry)
        }
        gameLocationsTrackerSet.add(gameLocationPortalCountry)
        gameLocationPortalStatus = false
        anywhereMapPowerupStatus = false
        removePowerupFromActivePowerupsList(Powerups.ANYWHERE_MAP)
        updateGameCurrentEndlessLocation(gameLocationPortalCountry)
        updateScoreByPoints(EndlessScorePoints.NEW_LOCATION)
        gameMusicPlayer.safePlay(appContext, gameCurrentEndlessLocationTrack)
        startObstacleSpawns()
    }

    fun updateGameCurrentEndlessLocation(location: GameCountry) {
        gameCurrentEndlessLocation = location

        gameCurrentEndlessLocationTrack = locationTrackMap[gameCurrentEndlessLocation]!!

        gameCurrentEndlessLocationHeaderText = locationsHeaderTextMap[gameCurrentEndlessLocation]!!

        gameCurrentEndlessLocationBackgroundBitmap = locationsDayBackgroundMap[gameCurrentEndlessLocation]!!

        gameCurrentEndlessLocationPlatform = locationsPlatformMap[gameCurrentEndlessLocation]!!

        gameCurrentEndlessLocationObstacleHead = locationsObstacleHeadMap[gameCurrentEndlessLocation]!!

        gameCurrentEndlessLocationObstacleBody = locationsObstacleBodyMap[gameCurrentEndlessLocation]!!

        gameCurrentEndlessLocationEnemy = enemiesAndWeaponsMap[gameCurrentEndlessLocation]!!

    }

    fun switchGameCurrentEndlessLocationNightMode() {
        if(gameCurrentEndlessLocationNightModeStatus) { // NIGHT -> DAY
            gameCurrentEndlessLocationBackgroundBitmap = locationsDayBackgroundMap[gameCurrentEndlessLocation]!!
        }
        else { // DAY -> NIGHT
            gameCurrentEndlessLocationBackgroundBitmap = locationsNightBackgroundMap[gameCurrentEndlessLocation]!!
        }
        gameCurrentEndlessLocationNightModeStatus = !gameCurrentEndlessLocationNightModeStatus
    }

    var gameCurrentEndlessStatus by mutableStateOf<GameStatus>(GameStatus.CANVAS_STARTED)
        private set

    fun beginGameCurrentEndlessStatus() {
        gameCurrentEndlessStatus = GameStatus.PLAYING
        currentEndlessGameStartTimestamp = System.currentTimeMillis()
    }

    fun pauseGameCurrentEndlessStatus() {
        stopCharacterFlyingAnimation()
        engine.stop()
        gameCurrentEndlessStatus = GameStatus.PAUSED
    }

    fun endGameCurrentEndlessStatus(lossType: GameStatus) {
        if (lossType == GameStatus.ENDLESS_OBSTACLE_HIT_LOSS){
            stopCharacterFlyingAnimation()
            stopObstacleSpawns()
            stopFuelCellSpawns()
            stopPowerupSpawns()
            engine.stop()
            gameCurrentEndlessStatus = lossType
        }
        else if (lossType == GameStatus.ENDLESS_FELL_DOWN_LOSS){
            gameCurrentEndlessStatus = GameStatus.ENDLESS_WINGS_DISAPPEARED
            stopCharacterFlyingAnimation()
            stopObstacleSpawns()
            stopFuelCellSpawns()
            stopPowerupSpawns()
            currentCharacterFrame = fallingDownFrame
            sfxPool.safePlay(
                sfxAssetsMap[PlayingSFX.FALLING_DOWN]!!,
                1f,
                1f,
                1,
                0,
                1.2f,
                gameSoundStatus.value
            )
            viewModelScope.launch {
                delay(800L)
                engine.stop()
                gameCurrentEndlessStatus = lossType
            }
        }
        else if (lossType == GameStatus.ENDLESS_DEFEATED_BY_ENEMY){
            stopCharacterFlyingAnimation()
            stopObstacleSpawns()
            stopFuelCellSpawns()
            stopPowerupSpawns()
            currentCharacterFrame = fallingDownFrame
            sfxPool.safePlay(
                sfxAssetsMap[PlayingSFX.LOSE_FIGHT_MUSIC]!!,
                1f,
                1f,
                1,
                0,
                1f,
                gameSoundStatus.value
            )
            viewModelScope.launch {
                delay(800L)
                engine.stop()
                gameCurrentEndlessStatus = lossType
            }
        }
    }

    fun resumeGameCurrentEndlessStatus() {
        gameCurrentEndlessStatus = GameStatus.PLAYING
        startCharacterFlyingAnimation()
        engine.start()
    }

    fun respawnExtraLife() {
        // lock collision checks first
        engine.collisionsLockedStatus = true
        // clear all obstacles
        currentOnScreenObstacleHeadsMap.clear()
        currentOnScreenObstacleBodiesMap.clear()
        engine.clearAllObstacles()
        // respawn character again
        gameCurrentCharacterHealth = 100f
        gameCurrentCharacterHoverboardFuel = 100f
        viewModelScope.launch {
            delay(700L)
            currentCharacterFrame = flyingFirmFrame
            delay(700L)
            extraLifeRespawnNoticeStatus = true
            engine.setCharacter(
                x = 80f,
                y = 600f,
                width = flyingFrames[0].width,
                height = flyingFrames[0].height
            )
            startCharacterFlyingAnimation()
            engine.start()
            if (!currentEndlessEnemySpawnStatus){
                startObstacleSpawns()
            }
            startFuelCellSpawns()
            startPowerupSpawns()
            removePowerupFromActivePowerupsList(Powerups.EXTRA_LIFE)
            extraLifePowerupStatus = false
            delay(2000L)
            extraLifeRespawnNoticeStatus = false
            engine.collisionsLockedStatus = false
        }
    }


    // Game Location Based Platform and Obstacles Management
    var gameCurrentEndlessLocationPlatform by mutableStateOf<ImageBitmap>(
        locationsPlatformMap[gameCurrentEndlessLocation]!!
    )
        private set


    var gameCurrentEndlessLocationObstacleHead by mutableStateOf<ImageBitmap>(
        locationsObstacleHeadMap[gameCurrentEndlessLocation]!!
    )
        private set

    var gameCurrentEndlessLocationObstacleBody by mutableStateOf<ImageBitmap>(
        locationsObstacleBodyMap[gameCurrentEndlessLocation]!!
    )
        private set

    var platformX by mutableFloatStateOf(0f)
        private set

    fun updatePlatformPosition(x: Float) {
        platformX = x
    }


    // Game Score and Distance State Management
    var gameCurrentEndlessScore by mutableFloatStateOf(0f)
        private set

    private var lastDayNightScore = 0f
    private var lastDifficultyScore = 0f
    private var lastEnemySpawnScore = 0f

    fun updateScoreByPoints(pointsType: EndlessScorePoints) {
        gameCurrentEndlessScore += pointsType.points

        if (gameCurrentEndlessScore >= lastDifficultyScore + 5f){
            updateObstacleDifficulty()
            lastDifficultyScore = gameCurrentEndlessScore
        }
        else if (gameCurrentEndlessScore >= lastDayNightScore + 50f){
            switchGameCurrentEndlessLocationNightMode()
            lastDayNightScore = gameCurrentEndlessScore
        }
        // SPAWN ENEMY EVERY 105 POINTS
        else if (gameCurrentEndlessScore >= lastEnemySpawnScore + 105f){
            stopObstacleSpawns()
            viewModelScope.launch {
                delay(1000L)
                startEnemyJob()
            }
            lastEnemySpawnScore = gameCurrentEndlessScore
            lastEnemySpawnDistance = gameCurrentEndlessDistance
        }
    }

    var gameCurrentEndlessDistance by mutableIntStateOf(0)
        private set

    private var lastEnemySpawnDistance = 0

    fun updateCurrentEndlessDistance() {
        gameCurrentEndlessDistance += 2
        // Regenerate Health if low
        if (gameCurrentCharacterHealth < 100f){
            increaseCharacterHealth()
        }
        if (gameCurrentEndlessDistance >= lastEnemySpawnDistance+30 && currentEndlessEnemySpawnStatus && !gameCurrentEndlessAirstrikeStatus){
            startAirstrike()
        }
    }

    var gameCurrentDistanceReferenceX by mutableFloatStateOf(engine.canvasWidth)
        private set

    fun updateGameCurrentDistanceReferenceX(x: Float) {
        gameCurrentDistanceReferenceX = x

        if (gameCurrentDistanceReferenceX <= 0f){
            updateCurrentEndlessDistance()
            engine.resetDistanceReference()
        }
    }


    // Game Powerups and FuelCells Data State Management
    var invinciblePowerupStatus by mutableStateOf<Boolean>(false)
        private set

    var invinciblePowerupStatusBarValue by mutableFloatStateOf(1f)
        private set

    private var invinciblePowerupStatusBarJob: Job? = null

    fun startInvinciblePowerupStatusBarJob(){
        if (invinciblePowerupStatusBarJob?.isActive == true) return
        invinciblePowerupStatusBarJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(500L)
                invinciblePowerupStatusBarValue -= 0.05f
            }
        }
    }

    fun stopInvinciblePowerupStatusBarJob(){
        invinciblePowerupStatusBarJob?.cancel()
    }

    var weaponPowerupStatus by mutableStateOf<Boolean>(false)
        private set

    var extraLifePowerupStatus by mutableStateOf<Boolean>(false)
        private set

    var anywhereMapPowerupStatus by mutableStateOf<Boolean>(false)
        private set

    var weaponPowerupPickupStatus by mutableStateOf<Boolean>(false)
        private set

    var extraLifePowerupPickupStatus by mutableStateOf<Boolean>(false)
        private set

    var extraLifeRespawnNoticeStatus by mutableStateOf<Boolean>(false)

    var anywhereMapPowerupPickupStatus by mutableStateOf<Boolean>(false)
        private set

    var activePowerupsList = mutableStateListOf<ImageBitmap>()
        private set

    fun addPowerupToActivePowerupsList(powerup: Powerups) {
        powerupsMap[powerup]?. let{ powerupBitmap ->
            activePowerupsList.add(powerupBitmap)
        }
    }

    fun removePowerupFromActivePowerupsList(powerup: Powerups) {
        powerupsMap[powerup]?. let{ powerupBitmap ->
            activePowerupsList.remove(powerupBitmap)
        }
    }

    var currentOnScreenPowerup by mutableStateOf<CurrentActivePowerup?>(null)
        private set

    var powerupRespawnStatus by mutableStateOf<Boolean>(true)
        private set

    fun updatePowerupPosition(updatedPowerup: GameObject) {
        currentOnScreenPowerup?.let {
            it.x = updatedPowerup.x
        }
        if (updatedPowerup.x <= -updatedPowerup.width) {
            currentOnScreenPowerup = null
            engine.removePowerup(updatedPowerup)
        }
    }

    private var powerupsJob: Job? = null

    fun startPowerupSpawns(){
        if (powerupsJob?.isActive == true) return
        var lastPowerupSpawnTimeScore = 0f
        powerupsJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                if (powerupRespawnStatus){
                    val (powerup, shouldSpawn) = shouldSpawnWhichPowerup()
                    if (shouldSpawn){
                        spawnPowerup(powerup)
                        lastPowerupSpawnTimeScore = gameCurrentEndlessScore
                    }
                }
                powerupRespawnStatus = gameCurrentEndlessScore >= lastPowerupSpawnTimeScore + 10f
                delay(5000L)
            }
        }
    }

    fun stopPowerupSpawns() {
        powerupsJob?.cancel()
    }

    fun spawnPowerup(powerupType: Powerups) {

        println("SPAWN CALLED")
        val canvasHeight = engine.canvasHeight
        val canvasWidth = engine.canvasWidth

        if (engine.obstacleHeads.isNotEmpty()){
            val obstacleTopHead = engine.obstacleHeads
                .toList()
                .asReversed()
                .first { head ->
                    currentOnScreenObstacleHeadsMap[head.id]?.type == ObstacleSourceType.TOP
                }

            val minX = obstacleTopHead.x.fastRoundToInt() + gameCurrentEndlessLocationObstacleHead.width + 10
            val maxX = minX + currentObstacleHorizontalGap.fastRoundToInt() - 10

            val minY = obstacleTopHead.y.fastRoundToInt() + gameCurrentEndlessLocationObstacleHead.height + 10
            val maxY = minY + currentObstacleVerticalGap.fastRoundToInt() - 10

            val randomX = GameRNG.nextInt(minX, maxX).toFloat()
            val randomY = GameRNG.nextInt(minY, maxY).toFloat()

            val powerup = CurrentActivePowerup(
                x = randomX,
                y = randomY,
                type = powerupType
            )

            currentOnScreenPowerup = powerup

            engine.setPowerup(
                powerup.x,
                powerup.y,
                powerupsMap[powerupType]!!.width,
                powerupsMap[powerupType]!!.height,
                powerup.id
            )

            powerupRespawnStatus = false
        }
    }

    fun shouldSpawnWhichPowerup(): Pair<Powerups, Boolean> {
        val currentScore = gameCurrentEndlessScore
        val currentHealth = gameCurrentCharacterHealth
        val currentFuel = gameCurrentCharacterHoverboardFuel

        fun chance65() = GameRNG.nextInt(1, 100) <= 65
        fun chance30() = GameRNG.nextInt(1, 100) <= 30

        // SPAWN EXTRA LIFE, FUEL CELL & INVINCIBLE POWERUPS
        if (currentFuel <= 25f || currentHealth <= 30f) {
            val randomBool = chance65()
            val powerup = if (randomBool) {
                val idx = if (GameRNG.nextInt(1, 100) <= 70) 0 else 1 // 70% Chance to Invincible and 30% to Extra Life
                // Only 1 Extra Life at one time
                if (healthPowerupsList[idx]==Powerups.EXTRA_LIFE && extraLifePowerupStatus)
                {
                    healthPowerupsList[0]
                }
                else {
                    healthPowerupsList[idx]
                }
            } else {
                healthPowerupsList[0]
            }
            return powerup to randomBool
        }


        // SPAWN WEAPON POWERUP
        if (currentScore in (lastEnemySpawnScore+90..lastEnemySpawnScore+100)) {
            val randomBool = GameRNG.nextInt(1, 100) <= 80 // 80% Chance for spawning weapon
            val powerup = if (randomBool) Powerups.AMMUNITION else healthPowerupsList[0]
            return powerup to randomBool
        }
        // SPAWN ANYWHERE MAP
        else if (currentScore in (lastEnemySpawnScore+10..lastEnemySpawnScore+20) && !(currentScore < 100f)){
            return Powerups.ANYWHERE_MAP to true
        }

        // SPAWN RANDOM POWERUP EXCEPT MAP POWERUP
        val randomBool = chance30()
        val powerup = if (randomBool) {
            val idx = GameRNG.nextInt(0, normalPowerupsList.size - 1)
            // Only 1 Extra Life at one time
            if (extraLifePowerupStatus && normalPowerupsList[idx]==Powerups.EXTRA_LIFE){
                val newIdx = if (GameRNG.nextInt(1, 100) <= 50) 0 else 1 // 50-50 Chances
                normalPowerupsList[newIdx]
            }
            else{
                normalPowerupsList[idx]
            }
        } else {
            normalPowerupsList[0]
        }

        return powerup to randomBool
    }

    //-------- Character Cannon Balls and Enemies Weapons
    var currentOnScreenCharacterCannonBallsMap = mutableStateMapOf<Int, CurrentActiveCharacterCannonBall>()
        private set

    fun updateCharacterCannonBallPosition(updatedCannonBall: GameObject) {
        currentOnScreenCharacterCannonBallsMap[updatedCannonBall.id]?.let {
            it.x = updatedCannonBall.x
            it.y = updatedCannonBall.y
        }
        if (updatedCannonBall.x > (cannonBall.width.toFloat() + engine.canvasWidth)
            || updatedCannonBall.y < -cannonBall.height.toFloat() || updatedCannonBall.y > (engine.canvasHeight - engine.platformHeight)) {
            engine.removeCharacterCannonBall(updatedCannonBall)
            currentOnScreenCharacterCannonBallsMap.remove(updatedCannonBall.id)
        }
    }

    var currentOnScreenEnemyWeaponsMap = mutableStateMapOf<Int, CurrentActiveEnemyWeapon>()
        private set

    fun updateEnemyWeaponPosition(updatedWeapon: GameObject) {
        currentOnScreenEnemyWeaponsMap[updatedWeapon.id]?.let {
            it.x = updatedWeapon.x
            it.y = updatedWeapon.y
        }
        if (updatedWeapon.x < -gameCurrentEndlessLocationEnemy.third.width.toFloat()
            || updatedWeapon.y < -gameCurrentEndlessLocationEnemy.third.height.toFloat() || updatedWeapon.y > (engine.canvasHeight - engine.platformHeight)) {
            engine.removeEnemyWeapon(updatedWeapon)
            currentOnScreenEnemyWeaponsMap.remove(updatedWeapon.id)
        }
    }


    //---------------- Enemy Movements and State Management ----------
    var gameCurrentEndlessLocationEnemy by mutableStateOf<Triple<ImageBitmap, String, ImageBitmap>>(
        enemiesAndWeaponsMap[gameCurrentEndlessLocation]!!
    )
        private set

    var currentEndlessEnemySpawnStatus by mutableStateOf(false)
        private set

    var enemyEntryStatus by mutableStateOf<Boolean>(false)
        private set

    fun disableEnemyEntry(){
        enemyEntryStatus = false
    }

    var enemyX by mutableFloatStateOf(-(gameCurrentEndlessLocationEnemy.first.width.toFloat()))
        private set

    var enemyY by mutableFloatStateOf(600f)
        private set

    var enemyAngle by mutableFloatStateOf(0f)
        private set

    var enemyHealth by mutableFloatStateOf(100f)
        private set

    var damageToEnemyNoticeStatus by mutableStateOf<Boolean>(false)
        private set

    var damageToCharacterNoticeStatus by mutableStateOf<Boolean>(false)
        private set

    private var enemyAndTheirWeaponsJob: Job? = null

    fun startEnemyJob(){
        if (enemyAndTheirWeaponsJob?.isActive == true) return
        val enemyFrame = gameCurrentEndlessLocationEnemy.first
        val enemyWeapon = gameCurrentEndlessLocationEnemy.third
        val canvasWidth = engine.canvasWidth
        val canvasHeight = engine.canvasHeight
        enemyAndTheirWeaponsJob = viewModelScope.launch(Dispatchers.Default) {
            // Set Enemy and Their Entry
            engine.setEnemy(
                canvasWidth + enemyFrame.width.toFloat()*3f,
                enemyY,
                enemyFrame.width,
                enemyFrame.height
            )
            currentEndlessEnemySpawnStatus = true
            enemyEntryStatus = true
            while (isActive){
                // Random Vertical Movements and Angle as per Character Position
                val directionToCharacter = when {
                    characterY > enemyY -> 1
                    characterY < enemyY -> -1
                    else -> 0
                }
                val randomYSpeed = GameRNG.nextFloat()*10f.coerceIn(2f, 6f)
                engine.enemyDirectionY = directionToCharacter
                engine.enemySpeed = randomYSpeed
                // Throwing Weapons Logic
                if (!enemyEntryStatus){ // Throwing only after entry if finished
                    val enemyWeaponObj = CurrentActiveEnemyWeapon(
                        x = enemyX + enemyWeapon.width.toFloat()/2,
                        y = enemyY + (enemyFrame.height.toFloat()/2 - enemyWeapon.height.toFloat()/2),
                        angle = enemyAngle
                    )
                    currentOnScreenEnemyWeaponsMap[enemyWeaponObj.id] = enemyWeaponObj
                    engine.setEnemyWeapon(
                        enemyWeaponObj.x,
                        enemyWeaponObj.y,
                        characterAngle,
                        cannonBall.width,
                        cannonBall.height,
                        enemyWeaponObj.id
                    )
                }
                delay(700L)
            }
        }
    }

    fun stopEnemyJob(){
        enemyAndTheirWeaponsJob?.cancel()
    }

    fun resetEnemyPositionAndStatus() {
        enemyX = -(gameCurrentEndlessLocationEnemy.first.width.toFloat())
        enemyY = 600f
        enemyAngle = 0f
        enemyHealth = 100f
    }

    fun updateEnemyEntryPosition(x: Float) {
        enemyX = x
    }

    fun updateEnemyPosition(updatedEnemy: GameObject) {
        enemyX = updatedEnemy.x
        enemyY = updatedEnemy.y
    }

    fun updateEnemyAngle(a: Float) {
        enemyAngle = a
    }

    fun damageEnemyHealth() {
        enemyHealth -= 20f
        if (enemyHealth <= 0f){
            enemyHealth = 0f
            // ENEMY GOT DEFEATED LOGIC
            stopEnemyJob()
            enemiesDefeatedCounter += 1
            currentEndlessEnemySpawnStatus = false
            resetEnemyPositionAndStatus()
            sfxPool.safePlay(
                sfxAssetsMap[PlayingSFX.WIN_FIGHT_MUSIC]!!,
                1f,
                1f,
                1,
                0,
                1f,
                gameSoundStatus.value
            )
            updateScoreByPoints(EndlessScorePoints.KILL_ENEMY)
            viewModelScope.launch {
                enemyDefeatedNoticeStatus = true
                delay(2000L)
                enemyDefeatedNoticeStatus = false
            }
            startObstacleSpawns()
            engine.removeEnemy()
        }
    }

    fun airstrikeEnemy() {
        enemyHealth = 0f
        // ENEMY GOT AIRSTRIKED LOGIC
        stopEnemyJob()
        viewModelScope.launch {
            enemyAirstrikedNoticeStatus = true
            delay(1500L)
            missileExplosionStatus = false
            resetEnemyPositionAndStatus()
            currentEndlessEnemySpawnStatus = false
            delay(1000L)
            enemyAirstrikedNoticeStatus = false
            startObstacleSpawns()
            engine.removeEnemy()
        }
    }

    var enemyDefeatedNoticeStatus by mutableStateOf<Boolean>(false)
        private set

    var enemiesDefeatedCounter by mutableIntStateOf(0)
        private set


    //--------------- AIRSTRIKE -----------------------------

    var gameCurrentEndlessAirstrikeStatus by mutableStateOf<Boolean>(false)
        private set

    var currentOnScreenAirstrikeFighterJet by mutableStateOf<CurrentActiveFighterJet?>(null)
        private set

    var enemyAirstrikedNoticeStatus by mutableStateOf<Boolean>(false)
        private set

    var currentOnScreenAirstrikeMissile by mutableStateOf<CurrentActiveAirstrikeMissile?>(null)
        private set

    fun startAirstrike(){
        sfxPool.safePlay(
            sfxAssetsMap[PlayingSFX.AIRSTRIKE_JET_PASSBY]!!,
            0.8f,
            0.8f,
            1,
            0,
            0.9f,
            gameSoundStatus.value
        )
        val jetObj = CurrentActiveFighterJet(
            x = -(2*airstrikeFrame.width.toFloat()),
            y = 100f
        )
        currentOnScreenAirstrikeFighterJet = jetObj
        engine.setAirstrikeFighterJet(
            jetObj.x,
            jetObj.y,
            airstrikeFrame.width,
            airstrikeFrame.height,
            jetObj.id
        )
        gameCurrentEndlessAirstrikeStatus = true
        viewModelScope.launch {
            delay(1200L)
            // Deploy Missile
            val missileObj = CurrentActiveAirstrikeMissile(
                x = jetObj.x + (airstrikeFrame.width.toFloat()/2 - airstrikeMissile.width.toFloat()/2),
                y = (jetObj.y + airstrikeFrame.height.toFloat()/2) + airstrikeMissile.height.toFloat()/4,
                angle = 0f
            )
            currentOnScreenAirstrikeMissile = missileObj
            engine.setAirstrikeMissile(
                missileObj.x,
                missileObj.y,
                missileObj.angle,
                airstrikeMissile.width,
                airstrikeMissile.height,
                missileObj.id
            )
        }
    }

    fun updateAirstrikeJetPosition(x: Float, y: Float){
        currentOnScreenAirstrikeFighterJet?.let {
            it.x = x
        }

        if(x > (engine.canvasWidth + airstrikeFrame.width.toFloat())){
            currentOnScreenAirstrikeFighterJet = null
            engine.removeAirstrikeFighterJet()
        }
    }

    fun updateAirstrikeMissilePosition(updatedMissile: CurrentActiveAirstrikeMissile){
        currentOnScreenAirstrikeMissile?.let {
            it.x = updatedMissile.x
            it.y = updatedMissile.y
            it.angle = updatedMissile.angle
        }
    }


    //------Hoverboard Fuel Cell----------------------

    var currentOnScreenFuelCell by mutableStateOf<CurrentActiveFuelCell?>(null)
        private set

    var fuelCellRespawnStatus by mutableStateOf<Boolean>(true)
        private set

    fun updateFuelCellPosition(updatedFuelCell: GameObject) {
        currentOnScreenFuelCell?.let {
            it.x = updatedFuelCell.x
        }
        if (updatedFuelCell.x <= -(11*engine.canvasWidth)) {
            fuelCellRespawnStatus = true
            engine.removeFuelCell(updatedFuelCell)
        }
    }

    private var fuelCellsJob: Job? = null

    fun startFuelCellSpawns() {
        if (fuelCellsJob?.isActive == true) return
        fuelCellsJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                if (engine.obstacleHeads.isNotEmpty() && fuelCellRespawnStatus){
                    val obstacleTopHead = engine.obstacleHeads
                        .toList()
                        .asReversed()
                        .first { head ->
                            currentOnScreenObstacleHeadsMap[head.id]?.type == ObstacleSourceType.TOP
                        }

                    val minX = obstacleTopHead.x.fastRoundToInt() + gameCurrentEndlessLocationObstacleHead.width + 10
                    val maxX = minX + currentObstacleHorizontalGap.fastRoundToInt() - 10

                    val minY = obstacleTopHead.y.fastRoundToInt() + gameCurrentEndlessLocationObstacleHead.height + 10
                    val maxY = minY + currentObstacleVerticalGap.fastRoundToInt() - 10

                    val randomX = GameRNG.nextInt(minX, maxX).toFloat()
                    val randomY = GameRNG.nextInt(minY, maxY).toFloat()

                    val fuelCell = CurrentActiveFuelCell(
                        x = randomX,
                        y = randomY
                    )

                    currentOnScreenFuelCell = fuelCell
                    engine.setFuelCell(
                        fuelCell.x,
                        fuelCell.y,
                        fuelCellImage.width,
                        fuelCellImage.height,
                        fuelCell.id
                    )

                    fuelCellRespawnStatus = false
                }

                delay(1000L)
            }
        }
    }

    fun stopFuelCellSpawns() {
        fuelCellsJob?.cancel()
    }

    // Game Cannon Fireballs State Management
    var currentCannonBallsCounter by mutableIntStateOf(0)
        private set

    fun onFireCannonGunControlTouch() {
        if (currentCannonBallsCounter>=1){
            // Shooting Logic
            val cannonBallObj = CurrentActiveCharacterCannonBall(
                x = characterX + (firingCannonGunFrame.width.toFloat() - cannonBall.width.toFloat()/2),
                y = characterY + (firingCannonGunFrame.height.toFloat()/2 - cannonBall.height.toFloat()/2),
                angle = characterAngle
            )
            currentOnScreenCharacterCannonBallsMap[cannonBallObj.id] = cannonBallObj
            engine.setCharacterCannonBall(
                cannonBallObj.x,
                cannonBallObj.y,
                characterAngle,
                cannonBall.width,
                cannonBall.height,
                cannonBallObj.id
            )
            if (!isCharacterFiring){
                viewModelScope.launch {
                    isCharacterFiring = true
                    currentCharacterFrame = firingCannonGunFrame
                    delay(300L)
                    isCharacterFiring = false
                }
            }
            currentCannonBallsCounter -= 1
            if (currentCannonBallsCounter==0){
                weaponPowerupStatus = false
            }
        }
    }

    fun incrementCannonBallsCounter() {
        currentCannonBallsCounter += 10
        if (currentCannonBallsCounter > 20){
            currentCannonBallsCounter = 20
        }
    }


    // Character Flying Animation and Frames Management
    var currentCharacterFrame by mutableStateOf<ImageBitmap>(flyingFrames[0])
        private set

    var isCharacterFiring by mutableStateOf<Boolean>(false)
        private set

    private var currentCharacterFlyingAnimationFrameIndex by mutableIntStateOf(0)

    private var flyingJob: Job? = null

    fun startCharacterFlyingAnimation() {
        if (flyingJob?.isActive == true) return

        flyingJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                if (!isCharacterFiring){
                    currentCharacterFlyingAnimationFrameIndex =
                        (currentCharacterFlyingAnimationFrameIndex + 1) % flyingFrames.size

                    currentCharacterFrame = flyingFrames[currentCharacterFlyingAnimationFrameIndex]

                    val imageBitmap = flyingFrames[currentCharacterFlyingAnimationFrameIndex]

                    engine.updateCharacterFrame(
                        width = imageBitmap.width,
                        height = imageBitmap.height
                    )
                }

                delay(250L)
            }
        }
    }

    fun stopCharacterFlyingAnimation() {
        flyingJob?.cancel()
    }


    // Character Movement and Position Management
    var characterX by mutableFloatStateOf(80f)
        private set

    var characterY by mutableFloatStateOf(600f)
        private set

    var characterAngle by mutableFloatStateOf(0f)
        private set

    fun updateCharacterPosition(x: Float, y: Float) {
        characterX = x
        characterY = y
    }

    fun updateCharacterAngle(a: Float) {
        if (gameCurrentEndlessStatus != GameStatus.ENDLESS_WINGS_DISAPPEARED){
            characterAngle = a
        }
    }


    // Obstacles Spawns, State and Movement
    var currentOnScreenObstacleHeadsMap = mutableStateMapOf<Int, CurrentActiveObstacleHead>()
        private set

    var currentOnScreenObstacleBodiesMap = mutableStateMapOf<Int, CurrentActiveObstacleBody>()
        private set

    var currentObstacleVerticalGap by mutableFloatStateOf(flyingFrames[0].height.toFloat() * 1.6f) // hardest 0.8
        private set  // Initial Gap between Pipe Heads

    var currentObstacleHorizontalGap by mutableFloatStateOf(flyingFrames[0].width.toFloat() * 1.6f) // hardest 0.8
        private set  // Initial Gap between Pipes

    fun updateObstacleHeadPosition(updatedObstacleHead: GameObject) {
        currentOnScreenObstacleHeadsMap[updatedObstacleHead.id]?.let { currentActiveObstacleHead ->
            currentActiveObstacleHead.x = updatedObstacleHead.x
            if (currentActiveObstacleHead.x <= -updatedObstacleHead.width) {
                removeObstacleFromActiveObstacleHeadsMap(currentActiveObstacleHead)
                engine.removeObstacleHead(updatedObstacleHead)
            }
            else if (!updatedObstacleHead.scored && currentActiveObstacleHead.x <= characterX){
                updateScoreByPoints(EndlessScorePoints.ESCAPE_OBSTACLE)
                updatedObstacleHead.scored = true
                if (!invinciblePowerupStatus){
                    spendHoverboardFuel()
                }
            }
        }
    }

    fun updateObstacleBodyPosition(updatedObstacleBody: GameObject) {
        currentOnScreenObstacleBodiesMap[updatedObstacleBody.id]?.let { currentActiveObstacleBody ->
            currentActiveObstacleBody.x = updatedObstacleBody.x
            if (currentActiveObstacleBody.x <= -updatedObstacleBody.width) {
                removeObstacleFromActiveObstacleBodiesMap(currentActiveObstacleBody)
                engine.removeObstacleBody(updatedObstacleBody)
            }
        }
    }

    private var obstaclesJob: Job? = null

    fun startObstacleSpawns() {
        if (obstaclesJob?.isActive == true) return

        val canvasHeight = engine.canvasHeight
        val canvasWidth = engine.canvasWidth

        obstaclesJob = viewModelScope.launch(Dispatchers.Default) {
            spawnInitialObstacle(canvasWidth, canvasHeight)
            while (isActive) {
                if (engine.obstacleHeads.isNotEmpty()){
                    val remainingCanvasWidth = canvasWidth - (engine.obstacleHeads.last().x + gameCurrentEndlessLocationObstacleHead.width.toFloat())
                    if(remainingCanvasWidth.fastRoundToInt() >= currentObstacleHorizontalGap.fastRoundToInt()){
                        spawnConsecutiveObstacle(
                            engine.obstacleHeads.last().x + gameCurrentEndlessLocationObstacleHead.width.toFloat() + currentObstacleHorizontalGap,
                            canvasHeight
                        )
                    }
                }
                delay(150L)
            }
        }
    }

    private fun spawnInitialObstacle(
        canvasWidth: Float,
        canvasHeight: Float
    ) {
        val startingX = canvasWidth + gameCurrentEndlessLocationObstacleBody.width.toFloat()
        val randomVerticalGapY = returnRandomVerticalGapY(canvasHeight.fastRoundToInt())
        // Top Head
        val topHead = CurrentActiveObstacleHead(
            x = startingX,
            y = randomVerticalGapY,
            type = ObstacleSourceType.TOP
        )
        addObstacleToActiveObstacleHeadsMap(topHead)
        engine.addObstacleHead(
            startingX,
            randomVerticalGapY - gameCurrentEndlessLocationObstacleHead.height.toFloat(),
            gameCurrentEndlessLocationObstacleHead.width,
            gameCurrentEndlessLocationObstacleHead.height,
            topHead.id
        )

        var remainingTopHeight = randomVerticalGapY - gameCurrentEndlessLocationObstacleHead.height.toFloat()

        // Top Body
        while (remainingTopHeight>0){
            val topBody = CurrentActiveObstacleBody(
                x = startingX,
                y = remainingTopHeight,
                type = ObstacleSourceType.TOP
            )
            addObstacleToActiveObstacleBodiesMap(topBody)
            engine.addObstacleBody(
                startingX,
                remainingTopHeight - gameCurrentEndlessLocationObstacleBody.height.toFloat(),
                gameCurrentEndlessLocationObstacleBody.width,
                gameCurrentEndlessLocationObstacleBody.height,
                topBody.id
            )
            remainingTopHeight -= gameCurrentEndlessLocationObstacleBody.height.toFloat()
        }

        // Bottom Head
        val bottomHead = CurrentActiveObstacleHead(
            x = startingX,
            y = randomVerticalGapY + currentObstacleVerticalGap,
            type = ObstacleSourceType.BOTTOM
        )
        addObstacleToActiveObstacleHeadsMap(bottomHead)
        engine.addObstacleHead(
            startingX,
            randomVerticalGapY + currentObstacleVerticalGap,
            gameCurrentEndlessLocationObstacleHead.width,
            gameCurrentEndlessLocationObstacleHead.height,
            bottomHead.id
        )

        var remainingBottomHeight = randomVerticalGapY + currentObstacleVerticalGap + gameCurrentEndlessLocationObstacleHead.height

        // Bottom Body
        while (remainingBottomHeight<canvasHeight){
            val bottomBody = CurrentActiveObstacleBody(
                x = startingX,
                y = remainingBottomHeight,
                type = ObstacleSourceType.BOTTOM
            )
            addObstacleToActiveObstacleBodiesMap(bottomBody)
            engine.addObstacleBody(
                startingX,
                remainingBottomHeight,
                gameCurrentEndlessLocationObstacleBody.width,
                gameCurrentEndlessLocationObstacleBody.height,
                bottomBody.id
            )
            remainingBottomHeight += gameCurrentEndlessLocationObstacleBody.height.toFloat()
        }
    }

    private fun spawnConsecutiveObstacle(
        startingX: Float,
        canvasHeight: Float
    ) {

        val randomVerticalGapY = returnRandomVerticalGapY(canvasHeight.fastRoundToInt())
        // Top Head
        val topHead = CurrentActiveObstacleHead(
            x = startingX,
            y = randomVerticalGapY,
            type = ObstacleSourceType.TOP
        )
        addObstacleToActiveObstacleHeadsMap(topHead)
        engine.addObstacleHead(
            startingX,
            randomVerticalGapY - gameCurrentEndlessLocationObstacleHead.height.toFloat(),
            gameCurrentEndlessLocationObstacleHead.width,
            gameCurrentEndlessLocationObstacleHead.height,
            topHead.id
        )

        var remainingTopHeight = randomVerticalGapY - gameCurrentEndlessLocationObstacleHead.height.toFloat()

        // Top Body
        while (remainingTopHeight>0){
            val topBody = CurrentActiveObstacleBody(
                x = startingX,
                y = remainingTopHeight,
                type = ObstacleSourceType.TOP
            )
            addObstacleToActiveObstacleBodiesMap(topBody)
            engine.addObstacleBody(
                startingX,
                remainingTopHeight - gameCurrentEndlessLocationObstacleBody.height.toFloat(),
                gameCurrentEndlessLocationObstacleBody.width,
                gameCurrentEndlessLocationObstacleBody.height,
                topBody.id
            )
            remainingTopHeight -= gameCurrentEndlessLocationObstacleBody.height.toFloat()
        }

        // Bottom Head
        val bottomHead = CurrentActiveObstacleHead(
            x = startingX,
            y = randomVerticalGapY + currentObstacleVerticalGap,
            type = ObstacleSourceType.BOTTOM
        )
        addObstacleToActiveObstacleHeadsMap(bottomHead)
        engine.addObstacleHead(
            startingX,
            randomVerticalGapY + currentObstacleVerticalGap,
            gameCurrentEndlessLocationObstacleHead.width,
            gameCurrentEndlessLocationObstacleHead.height,
            bottomHead.id
        )

        var remainingBottomHeight = randomVerticalGapY + currentObstacleVerticalGap + gameCurrentEndlessLocationObstacleHead.height

        // Bottom Body
        while (remainingBottomHeight<canvasHeight){
            val bottomBody = CurrentActiveObstacleBody(
                x = startingX,
                y = remainingBottomHeight,
                type = ObstacleSourceType.BOTTOM
            )
            addObstacleToActiveObstacleBodiesMap(bottomBody)
            engine.addObstacleBody(
                startingX,
                remainingBottomHeight,
                gameCurrentEndlessLocationObstacleBody.width,
                gameCurrentEndlessLocationObstacleBody.height,
                bottomBody.id
            )
            remainingBottomHeight += gameCurrentEndlessLocationObstacleBody.height.toFloat()
        }
    }

    fun stopObstacleSpawns() {
        obstaclesJob?.cancel()
    }

    fun addObstacleToActiveObstacleHeadsMap(obstacleHead: CurrentActiveObstacleHead) {
        currentOnScreenObstacleHeadsMap[obstacleHead.id] = obstacleHead
    }

    fun removeObstacleFromActiveObstacleHeadsMap(obstacleHead: CurrentActiveObstacleHead) {
        currentOnScreenObstacleHeadsMap.remove(obstacleHead.id)
    }

    fun addObstacleToActiveObstacleBodiesMap(obstacleBody: CurrentActiveObstacleBody) {
        currentOnScreenObstacleBodiesMap[obstacleBody.id] = obstacleBody
    }

    fun removeObstacleFromActiveObstacleBodiesMap(obstacleBody: CurrentActiveObstacleBody) {
        currentOnScreenObstacleBodiesMap.remove(obstacleBody.id)
    }

    fun returnRandomVerticalGapY(
        canvasHeight: Int,
        min: Int = gameCurrentEndlessLocationObstacleHead.height,
        max: Int = canvasHeight - gameCurrentEndlessLocationObstacleHead.height - currentObstacleVerticalGap.fastRoundToInt()
    ): Float {
        return GameRNG.nextInt(min, max).toFloat()
    }

    private fun updateObstacleDifficulty() { // EVERY 10 SCORE POINTS
        if (currentObstacleVerticalGap > (flyingFrames[0].height.toFloat() * 0.8f)
            && currentObstacleHorizontalGap > (flyingFrames[0].width.toFloat() * 0.8f)
        ) {
            currentObstacleVerticalGap -= (flyingFrames[0].height.toFloat() * 0.001f) //0.0001
            currentObstacleHorizontalGap -= (flyingFrames[0].width.toFloat() * 0.001f)
        }
    }

    // Health and Hoverboard Fuel Management
    var gameCurrentCharacterHealth by mutableFloatStateOf(100f)
        private set

    var gameCurrentCharacterHoverboardFuel by mutableFloatStateOf(100f)
        private set

    var gameCurrentFuelCellStatus by mutableStateOf<Boolean>(false)
        private set

    var gameCurrentLowHoverboardFuelStatus by mutableStateOf<Boolean>(false)
        private set


    fun increaseCharacterHealth() {
        gameCurrentCharacterHealth += 2.5f
        if (gameCurrentCharacterHealth >= 100f){
            gameCurrentCharacterHealth = 100f
        }
    }

    fun decreaseCharacterHealth() {
        gameCurrentCharacterHealth -= 20f
        if (gameCurrentCharacterHealth <= 0f) {
            gameCurrentCharacterHealth = 0f
            if (extraLifePowerupStatus){
                stopCharacterFlyingAnimation()
                stopObstacleSpawns()
                stopFuelCellSpawns()
                stopPowerupSpawns()
                currentCharacterFrame = fallingDownFrame
                sfxPool.safePlay(
                    sfxAssetsMap[PlayingSFX.LOSE_FIGHT_MUSIC]!!,
                    1f,
                    1f,
                    1,
                    0,
                    1f,
                    gameSoundStatus.value
                )
                viewModelScope.launch {
                    delay(800L)
                    engine.stop()
                }
                respawnExtraLife()
                incrementCannonBallsCounter()
                weaponPowerupStatus = true
            }
            else {
                endGameCurrentEndlessStatus(GameStatus.ENDLESS_DEFEATED_BY_ENEMY)
            }
        }
    }

    fun refuelHoverboardWithFuelCell(){
        gameCurrentCharacterHoverboardFuel += 50f
        if (gameCurrentCharacterHoverboardFuel>=100f){
            gameCurrentCharacterHoverboardFuel = 100f
        }
    }

    fun spendHoverboardFuel() {
        gameCurrentCharacterHoverboardFuel -= 2f
        if (gameCurrentCharacterHoverboardFuel <= 0f){
            if (extraLifePowerupStatus){
                stopCharacterFlyingAnimation()
                stopObstacleSpawns()
                stopFuelCellSpawns()
                stopPowerupSpawns()
                currentCharacterFrame = fallingDownFrame
                sfxPool.safePlay(
                    sfxAssetsMap[PlayingSFX.FALLING_DOWN]!!,
                    1f,
                    1f,
                    1,
                    0,
                    1.2f,
                    gameSoundStatus.value
                )
                viewModelScope.launch {
                    delay(800L)
                    engine.stop()
                }
                respawnExtraLife()
            }
            else {
                endGameCurrentEndlessStatus(GameStatus.ENDLESS_FELL_DOWN_LOSS)
            }
        }
        else if (gameCurrentCharacterHoverboardFuel in 30f..35f){
            viewModelScope.launch {
                gameCurrentLowHoverboardFuelStatus = true
                delay(1000L)
                gameCurrentLowHoverboardFuelStatus = false
            }
        }
    }

    // Collisions Management
    fun onObstacleHit() {
        if (gameCurrentEndlessStatus != GameStatus.ENDLESS_WINGS_DISAPPEARED){
            sfxPool.safePlay(
                sfxAssetsMap[PlayingSFX.OBSTACLE_HIT]!!,
                1f,
                1f,
                1,
                0,
                1f,
                gameSoundStatus.value
            )
            currentCharacterFrame = obstacleHitFrame
            gameCurrentCharacterHealth = 10f

            if (extraLifePowerupStatus){
                stopCharacterFlyingAnimation()
                stopObstacleSpawns()
                stopFuelCellSpawns()
                stopPowerupSpawns()
                engine.stop()
                respawnExtraLife()
            }
            else {
                endGameCurrentEndlessStatus(GameStatus.ENDLESS_OBSTACLE_HIT_LOSS)
            }
        }
    }

    fun onPowerupHit(powerupObject: GameObject) {
        sfxPool.safePlay(
            sfxAssetsMap[PlayingSFX.POWERUP_PICKUP]!!,
            1f,
            1f,
            1,
            0,
            1f,
            gameSoundStatus.value
        )
        currentOnScreenPowerup?.let {
            when(it.type){
                Powerups.INVINCIBLE -> {
                    gameCurrentCharacterHealth = 100f
                    gameCurrentCharacterHoverboardFuel = 100f
                    invinciblePowerupStatus = true
                    engine.collisionsLockedStatus = true
                    addPowerupToActivePowerupsList(it.type)
                    stopCharacterFlyingAnimation()
                    currentCharacterFrame = invincibleFrame
                    // Job for Status Bar
                    startInvinciblePowerupStatusBarJob()
                    viewModelScope.launch {
                        // Powerup lasts 10 seconds
                        delay(10000L)
                        stopInvinciblePowerupStatusBarJob()
                        invinciblePowerupStatusBarValue = 0f
                        delay(500L) // Delay for Status Bar to disappear and player to react
                        invinciblePowerupStatus = false
                        engine.collisionsLockedStatus = false
                        // Remove powerup from active powerups list
                        removePowerupFromActivePowerupsList(it.type)
                        currentCharacterFrame = flyingFrames[0]
                        startCharacterFlyingAnimation()
                        // Refresh for another time
                        delay(1500L)
                        invinciblePowerupStatusBarValue = 1f
                    }
                }
                Powerups.AMMUNITION -> {
                    incrementCannonBallsCounter()
                    weaponPowerupStatus = true
                    // No need to add family heart as cannon gun counter already shows up
                    currentCharacterFrame = firingCannonGunFrame
                    viewModelScope.launch {
                        // Notice for 2 seconds
                        weaponPowerupPickupStatus = true
                        delay(2000L)
                        weaponPowerupPickupStatus = false
                    }
                }
                Powerups.EXTRA_LIFE -> {
                    extraLifePowerupStatus = true
                    addPowerupToActivePowerupsList(it.type)
                    viewModelScope.launch {
                        // Notice for 2 seconds
                        extraLifePowerupPickupStatus = true
                        delay(2000L)
                        extraLifePowerupPickupStatus = false
                    }
                }
                Powerups.ANYWHERE_MAP -> {
                    anywhereMapPowerupStatus = true
                    addPowerupToActivePowerupsList(it.type)
                    val availableChoices = GameCountry.entries.toMutableList()
                    availableChoices -= gameCurrentEndlessLocation
                    availableChoices -= gameLocationPortalCountry
                    gameLocationPortalCountry = availableChoices[GameRNG.nextInt(0, availableChoices.size-1)]
                    viewModelScope.launch {
                        // Notice for 2.5 seconds
                        anywhereMapPowerupPickupStatus = true
                        delay(2500L)
                        anywhereMapPowerupPickupStatus = false
                        enableGameLocationPortal()
                        delay(15000L)
                        disableGameLocationPortal()
                    }
                }
            }
        }
        currentOnScreenPowerup = null
        engine.removePowerup(powerupObject)
    }

    fun onFuelCellHit() {
        sfxPool.safePlay(
            sfxAssetsMap[PlayingSFX.FUEL_CELL_PICKUP]!!,
            0.6f,
            0.6f,
            1,
            0,
            1f,
            gameSoundStatus.value
        )
        currentOnScreenFuelCell?.let {
            refuelHoverboardWithFuelCell()
        }
        // Keeping fuelCell on screen null but in background in engine to respawn only after 5*canWidth
        currentOnScreenFuelCell = null
        viewModelScope.launch {
            gameCurrentFuelCellStatus = true
            delay(1000L)
            gameCurrentFuelCellStatus = false
        }
    }

    fun onCharacterHitByEnemyWeapon(enemyWeaponObject: GameObject) {
        sfxPool.safePlay(
            sfxAssetsMap[PlayingSFX.HEALTH_DAMAGE_BY_ENEMY]!!,
            1f,
            1f,
            1,
            0,
            1.2f,
            gameSoundStatus.value
        )
        viewModelScope.launch {
            damageToCharacterNoticeStatus = true
            decreaseCharacterHealth()
            delay(1000L)
            damageToCharacterNoticeStatus = false
        }
        currentOnScreenEnemyWeaponsMap.remove(enemyWeaponObject.id)
        engine.removeEnemyWeapon(enemyWeaponObject)
    }

    fun onEnemyHitByCharacterCannonBall(characterCannonBallObject: GameObject) {
        sfxPool.safePlay(
            sfxAssetsMap[PlayingSFX.CANNONBALL_HIT_ON_ENEMY]!!,
            1f,
            1f,
            1,
            0,
            1.2f,
            gameSoundStatus.value
        )
        viewModelScope.launch {
            damageToEnemyNoticeStatus = true
            damageEnemyHealth()
            delay(1000L)
            damageToEnemyNoticeStatus = false
        }
        currentOnScreenCharacterCannonBallsMap.remove(characterCannonBallObject.id)
        engine.removeCharacterCannonBall(characterCannonBallObject)
    }

    var missileExplosionStatus by mutableStateOf<Boolean>(false)

    fun onEnemyHitByAirstrikeMissile() {
        sfxPool.safePlay(
            sfxAssetsMap[PlayingSFX.AIRSTRIKE_EXPLOSION]!!,
            0.8f,
            0.8f,
            1,
            0,
            1.2f,
            gameSoundStatus.value
        )
        missileExplosionStatus = true
        airstrikeEnemy()
        currentOnScreenAirstrikeMissile = null
        engine.removeAirstrikeMissile()
    }


    fun updateEndlessProgressAndAchievements(): Triple<List<EndlessAchievement>, Boolean, Int> {
        // PROGRESS
        var isHighScore = false
        val newEndlessProgress = EndlessModeProgress(
            highestScore = 0,
            longestDistance = 0,
            longestTime = "",
            mostEnemiesDefeated = 0,
            totalEndlessFlights = 0
        )
        // Time
        currentEndlessGameStopTimestamp = System.currentTimeMillis()
        val timeDifference = currentEndlessGameStopTimestamp - currentEndlessGameStartTimestamp
        val gameplayMinutes = (timeDifference / 1000) / 60
        val currentGameplayMinutes = gameplayMinutes.toFloat()
        val formattedGameTimeString = if(gameplayMinutes >= 60){
            val gameplayHours = gameplayMinutes.toFloat() / 60f
            String.format(java.util.Locale.US, "%.1f", gameplayHours) +
                    if (gameplayHours >= 1.1f) " hrs" else " hr"
        }
        else{
            "$gameplayMinutes mins"
        }
        val existingGameplayMinutes = parseTimeToMinutes(endlessModeProgress.value.longestTime)
        if (currentGameplayMinutes > existingGameplayMinutes) {
            newEndlessProgress.longestTime = formattedGameTimeString
        }
        else{
            newEndlessProgress.longestTime = endlessModeProgress.value.longestTime
        }

        // Distance
        if (gameCurrentEndlessDistance > endlessModeProgress.value.longestDistance) {
            newEndlessProgress.longestDistance = gameCurrentEndlessDistance
        }
        else{
            newEndlessProgress.longestDistance = endlessModeProgress.value.longestDistance
        }

        // Score
        if (gameCurrentEndlessScore.fastRoundToInt() > endlessModeProgress.value.highestScore) {
            newEndlessProgress.highestScore = gameCurrentEndlessScore.fastRoundToInt()
            isHighScore = true
        }
        else{
            newEndlessProgress.highestScore = endlessModeProgress.value.highestScore
        }

        // Enemies Defeated
        if (enemiesDefeatedCounter > endlessModeProgress.value.mostEnemiesDefeated) {
            newEndlessProgress.mostEnemiesDefeated = enemiesDefeatedCounter
        }
        else{
            newEndlessProgress.mostEnemiesDefeated = endlessModeProgress.value.mostEnemiesDefeated
        }

        // Flight No
        newEndlessProgress.totalEndlessFlights = endlessModeProgress.value.totalEndlessFlights + 1
        val flightNo = endlessModeProgress.value.totalEndlessFlights + 1

        //Update to new progress
        viewModelScope.launch {
            repo.setEndlessModeProgress(newEndlessProgress)
        }

        // ACHIEVEMENTS
        val completedAchievementsList = mutableListOf<EndlessAchievement>()
        val newAchievementsMap = endlessAchievementsMap.value
        newAchievementsMap.filter { !it.value.isCompleted }.forEach {
            when(it.value.achievementType){
                EndlessAchievementType.SCORE_BASED -> {
                    if (gameCurrentEndlessScore >= it.value.target){
                        it.value.isCompleted = true
                        completedAchievementsList.add(it.value)
                    }
                    else{
                        it.value.current = 0f
                    }
                }
                EndlessAchievementType.DISTANCE_BASED -> {
                    if (it.value.current + gameCurrentEndlessDistance.toFloat() >= it.value.target){
                        it.value.isCompleted = true
                        completedAchievementsList.add(it.value)
                    }
                    else{
                        it.value.current += gameCurrentEndlessDistance.toFloat()
                    }
                }
                EndlessAchievementType.ENEMY_DEFEATS_BASED -> {
                    if (it.value.current + enemiesDefeatedCounter.toFloat() >= it.value.target){
                        it.value.isCompleted = true
                        completedAchievementsList.add(it.value)
                    }
                    else{
                        it.value.current += enemiesDefeatedCounter.toFloat()
                    }
                }
                EndlessAchievementType.MAPS_BASED -> {
                    if (gameLocationsTrackerSet.size.toFloat() == it.value.target){
                        it.value.isCompleted = true
                        completedAchievementsList.add(it.value)
                    }
                    else{
                        it.value.current = 0f
                    }
                }
            }
        }

        // Update to new achievements
        viewModelScope.launch {
            repo.setEndlessAchievements(newAchievementsMap)
        }

        return Triple(completedAchievementsList, isHighScore, flightNo)
    }
}








