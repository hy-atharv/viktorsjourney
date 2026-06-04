package com.example.viktorsjourney

import android.app.Application
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.viktorsjourney.core.data.GameDefaults
import com.example.viktorsjourney.core.data.GameRepository
import com.example.viktorsjourney.core.data.models.CharacterAssets
import com.example.viktorsjourney.core.data.models.EnemyAssets
import com.example.viktorsjourney.core.data.models.LocationAssets
import com.example.viktorsjourney.core.data.models.MapAssets
import com.example.viktorsjourney.core.data.models.ObstacleAssets
import com.example.viktorsjourney.core.data.models.PreloadedAssets
import com.example.viktorsjourney.core.data.models.SFXAssets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import androidx.core.graphics.scale

class GameAppStartupViewModel(
    application: Application,
    private val repo: GameRepository
) : AndroidViewModel(application) {

    private var storedCountry = GameDefaults.DEFAULT_COUNTRY

    private var storedSoundStatus = true

    var isLoading by mutableStateOf(true)
        private set

    lateinit var assets: PreloadedAssets
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            storedCountry = repo.getGameCountry().first()
            storedSoundStatus = repo.getGameSoundStatus().first()
            assets = loadAssets()
            // making assets globally available by loading assets module
            getKoin().loadModules(
                listOf(
                    module {
                        single { assets }
                    }
                )
            )
            // Mute Sound Preference
            if (!storedSoundStatus){
                assets.bgmPlayer.setVolume(0.0f, 0.0f)
            }
            assets.bgmPlayer.isLooping = true // looping background music
            assets.bgmPlayer.start() // start background music
            isLoading = false     // Splash screen ends here
        }
    }

    private fun loadAssets(): PreloadedAssets {
        val context = getApplication<Application>().applicationContext
        val res = context.resources

        val soundPool = SoundPool.Builder().setMaxStreams(10).build()

        fun load(id: Int) = loadImageBitmap(res, id)
        fun scaledLoad(id: Int) = loadScaledImageBitmap(res, id)
        fun scaledCharLoad(id: Int) = loadCharacterScaledImageBitmap(res, id)
        fun powersScaledLoad(id: Int) = loadConsumablesAndPowerupsScaledImageBitmap(res, id)
        fun enemiesScaledLoad(id: Int) = loadEnemiesScaledImageBitmap(res, id)

        val flying1Image = scaledCharLoad(R.drawable.flying_1_frame)
        val flying2Image = scaledCharLoad(R.drawable.flying_2_frame)
        val flyingFirmImage = scaledCharLoad(R.drawable.flying_firm_frame)
        val flyingPowerupImage = scaledCharLoad(R.drawable.powerup_effect_frame)
        val firingCannonGunImage = scaledCharLoad(R.drawable.firing_frame)
        val obstacleHitFrameImage = scaledCharLoad(R.drawable.obstacle_hit_frame)
        val noWingsFrameImage = scaledCharLoad(R.drawable.no_wings_frame)

        val characterAssets = CharacterAssets(
            flying1 = flying1Image,
            flying2 = flying2Image,
            flyingFirm = flyingFirmImage,
            flyingPowerup = flyingPowerupImage,
            firingCannonGun = firingCannonGunImage,
            obstacleHitFrame = obstacleHitFrameImage,
            noWingsFrame = noWingsFrameImage
        )

        fun loadMap(
            dayBackgroundId: Int,
            nightBackgroundId: Int,
            platformId: Int,
            obstacleHeadId: Int,
            obstacleBodyId: Int,
            track: Int,
            enemyId: Int,
            enemyName: String,
            enemyWeaponId: Int
        ): MapAssets {

            val dayBackgroundImage = load(dayBackgroundId)
            val nightBackgroundImage = load(nightBackgroundId)
            val platformImage = load(platformId)
            val obstacleHeadImage = scaledLoad(obstacleHeadId)
            val obstacleBodyImage = scaledLoad(obstacleBodyId)
            val enemyImage = enemiesScaledLoad(enemyId)
            val enemyWeaponImage = powersScaledLoad(enemyWeaponId)

            return MapAssets(
                locationAssets = LocationAssets(
                    dayBackground = dayBackgroundImage,
                    nightBackground = nightBackgroundImage,
                    platform = platformImage,
                    trackId = track
                ),
                obstacleAssets = ObstacleAssets(
                    obstacleHead = obstacleHeadImage,
                    obstacleBody = obstacleBodyImage,
                ),
                enemyAssets = EnemyAssets(
                    enemyFrame = enemyImage,
                    enemyWeapon = enemyWeaponImage,
                    enemyName = enemyName
                )
            )
        }

        val croatiaMapAssets = loadMap(
            R.drawable.croatia_map_day_background,
            R.drawable.croatia_map_night_background,
            R.drawable.croatia_map_platform,
            R.drawable.croatia_map_obstacle_head,
            R.drawable.croatia_map_obstacle_body,
            R.raw.croatia_map_music,
            R.drawable.croatia_map_enemy_frame,
            "Nikica",
            R.drawable.croatia_map_enemy_weapon
        )

        val japanMapAssets = loadMap(
            R.drawable.japan_map_day_background,
            R.drawable.japan_map_night_background,
            R.drawable.japan_map_platform,
            R.drawable.japan_map_obstacle_head,
            R.drawable.japan_map_obstacle_body,
            R.raw.japan_map_music,
            R.drawable.japan_map_enemy_frame,
            "Miko",
            R.drawable.japan_map_enemy_weapon
        )

        val russiaMapAssets = loadMap(
            R.drawable.russia_map_day_background,
            R.drawable.russia_map_night_background,
            R.drawable.russia_map_platform,
            R.drawable.russia_map_obstacle_head,
            R.drawable.russia_map_obstacle_body,
            R.raw.russia_map_music,
            R.drawable.russia_map_enemy_frame,
            "Timovski",
            R.drawable.russia_map_enemy_weapon
        )

        val egyptMapAssets = loadMap(
            R.drawable.egypt_map_day_background,
            R.drawable.egypt_map_night_background,
            R.drawable.egypt_map_platform,
            R.drawable.egypt_map_obstacle_head,
            R.drawable.egypt_map_obstacle_body,
            R.raw.egypt_map_music,
            R.drawable.egypt_map_enemy_frame,
            "Bariqa",
            R.drawable.egypt_map_enemy_weapon
        )

        val indiaMapAssets = loadMap(
            R.drawable.india_map_day_background,
            R.drawable.india_map_night_background,
            R.drawable.india_map_platform,
            R.drawable.india_map_obstacle_head,
            R.drawable.india_map_obstacle_body,
            R.raw.india_map_music,
            R.drawable.india_map_enemy_frame,
            "Tanya",
            R.drawable.india_map_enemy_weapon
        )

        val shieldImage = powersScaledLoad(R.drawable.invincible_shield_icon)
        val ammunitionImage = powersScaledLoad(R.drawable.ammunition_icon)
        val extraLifeImage = powersScaledLoad(R.drawable.extra_life_icon)
        val anywhereMapImage = powersScaledLoad(R.drawable.map_icon)
        val fuelCellImage = powersScaledLoad(R.drawable.fuel_cell_icon)
        val cannonBallImage = powersScaledLoad(R.drawable.cannonball_icon)
        val atharvAirstrikeImage = scaledLoad(R.drawable.atharv_airstrike_frame)
        val airstrikeMissileImage = powersScaledLoad(R.drawable.airstrike_missile)
        val airstrikeMissileExplosionImage = enemiesScaledLoad(R.drawable.missile_explosion_frame)
        val endlessPortalBackground = load(R.drawable.endless_portal_background)

        return PreloadedAssets(
            croatiaMap = croatiaMapAssets,
            japanMap = japanMapAssets,
            russiaMap = russiaMapAssets,
            egyptMap = egyptMapAssets,
            indiaMap = indiaMapAssets,
            character = characterAssets,
            shieldIcon = shieldImage,
            ammunitionIcon = ammunitionImage,
            fuelCell = fuelCellImage,
            extraLifeIcon = extraLifeImage,
            anywhereMap = anywhereMapImage,
            cannonBall = cannonBallImage,
            endlessPortalBackground = endlessPortalBackground,
            atharvAirstrike = atharvAirstrikeImage,
            airstrikeMissile = airstrikeMissileImage,
            airstrikeMissileExplosion = airstrikeMissileExplosionImage,
            bgmPlayer = MediaPlayer.create(context, R.raw.game_music),
            portalMusicId = R.raw.portal_music,
            homeScreenCountry = storedCountry,
            sfxPool = soundPool,
            sfxAssets = SFXAssets(
                wingsFlapId = soundPool.load(context, R.raw.wings_flap_sfx, 1),
                obstacleHitId = soundPool.load(context, R.raw.obstacle_hit_sfx, 1),
                powerupPickupId = soundPool.load(context, R.raw.powerup_sfx, 1),
                fuelCellPickupId = soundPool.load(context, R.raw.item_pickup_sfx, 1),
                fallingDownId = soundPool.load(context, R.raw.falling_sfx, 1),
                firingCannonBallId = soundPool.load(context, R.raw.cannon_shot_sfx, 1),
                cannonBallHitId = soundPool.load(context, R.raw.cannonball_hit_sfx, 1),
                healthDamageId = soundPool.load(context, R.raw.fight_damage_sfx, 1),
                loseFightMusicId = soundPool.load(context, R.raw.lose_fight_sfx, 1),
                winFightMusicId = soundPool.load(context, R.raw.completion_sfx, 1),
                airstrikeJetPassById = soundPool.load(context, R.raw.airstrike_jet_passby_sfx, 1),
                airstrikeExplosionId = soundPool.load(context, R.raw.airstrike_explosion_sfx, 1)
            )
        )
    }

    private fun loadImageBitmap(
        res: Resources,
        drawableId: Int
    ): ImageBitmap {

        val bitmap = BitmapFactory.decodeResource(res, drawableId)
        return bitmap.asImageBitmap()
    }

    private fun loadScaledImageBitmap(
        res: Resources,
        drawableId: Int,
        scale: Float = 0.32f
    ): ImageBitmap {

        val originalBitmap = BitmapFactory.decodeResource(res, drawableId)

        val origWidth = originalBitmap.width
        val origHeight = originalBitmap.height

        val newWidth = (origWidth * scale).toInt().coerceAtLeast(1)
        val newHeight = (origHeight * scale).toInt().coerceAtLeast(1)

        val scaledBitmap = originalBitmap.scale(newWidth, newHeight)

        return scaledBitmap.asImageBitmap()
    }

    private fun loadCharacterScaledImageBitmap(
        res: Resources,
        drawableId: Int,
        scale: Float = 0.38f
    ): ImageBitmap {

        val originalBitmap = BitmapFactory.decodeResource(res, drawableId)

        val origWidth = originalBitmap.width
        val origHeight = originalBitmap.height

        val newWidth = (origWidth * scale).toInt().coerceAtLeast(1)
        val newHeight = (origHeight * scale).toInt().coerceAtLeast(1)

        val scaledBitmap = originalBitmap.scale(newWidth, newHeight)

        return scaledBitmap.asImageBitmap()
    }

    private fun loadConsumablesAndPowerupsScaledImageBitmap(
        res: Resources,
        drawableId: Int,
        scale: Float = 0.16f
    ): ImageBitmap {

        val originalBitmap = BitmapFactory.decodeResource(res, drawableId)

        val origWidth = originalBitmap.width
        val origHeight = originalBitmap.height

        val newWidth = (origWidth * scale).toInt().coerceAtLeast(1)
        val newHeight = (origHeight * scale).toInt().coerceAtLeast(1)

        val scaledBitmap = originalBitmap.scale(newWidth, newHeight)

        return scaledBitmap.asImageBitmap()
    }

    private fun loadEnemiesScaledImageBitmap(
        res: Resources,
        drawableId: Int,
        scale: Float = 0.4f
    ): ImageBitmap {

        val originalBitmap = BitmapFactory.decodeResource(res, drawableId)

        val origWidth = originalBitmap.width
        val origHeight = originalBitmap.height

        val newWidth = (origWidth * scale).toInt().coerceAtLeast(1)
        val newHeight = (origHeight * scale).toInt().coerceAtLeast(1)

        val scaledBitmap = originalBitmap.scale(newWidth, newHeight)

        return scaledBitmap.asImageBitmap()
    }

}
