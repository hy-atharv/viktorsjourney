package com.example.viktorsjourney.core.data.models

import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.ui.graphics.ImageBitmap

data class PreloadedAssets(
    val croatiaMap: MapAssets,
    val japanMap: MapAssets,
    val russiaMap: MapAssets,
    val egyptMap: MapAssets,
    val indiaMap: MapAssets,
    val character: CharacterAssets,
    val shieldIcon: ImageBitmap,
    val ammunitionIcon: ImageBitmap,
    val extraLifeIcon: ImageBitmap,
    val anywhereMap: ImageBitmap,
    val fuelCell: ImageBitmap,
    val cannonBall: ImageBitmap,
    val endlessPortalBackground: ImageBitmap,
    val atharvAirstrike: ImageBitmap,
    val airstrikeMissile: ImageBitmap,
    val airstrikeMissileExplosion: ImageBitmap,
    val bgmPlayer: MediaPlayer,
    val portalMusicId: Int,
    val homeScreenCountry: GameCountry,
    val sfxPool: SoundPool,
    val sfxAssets: SFXAssets
)

data class SFXAssets(
    val wingsFlapId: Int,
    val obstacleHitId: Int,
    val powerupPickupId: Int,
    val fuelCellPickupId: Int,
    val fallingDownId: Int,
    val firingCannonBallId: Int,
    val cannonBallHitId: Int,
    val healthDamageId: Int,
    val airstrikeJetPassById: Int,
    val airstrikeExplosionId: Int,
    val loseFightMusicId: Int,
    val winFightMusicId: Int
)

data class MapAssets(
    val locationAssets: LocationAssets,
    val obstacleAssets: ObstacleAssets,
    val enemyAssets: EnemyAssets
)

data class LocationAssets(
    val dayBackground: ImageBitmap,
    val nightBackground: ImageBitmap,
    val platform: ImageBitmap,
    val trackId: Int
)

data class ObstacleAssets(
    val obstacleHead: ImageBitmap,
    val obstacleBody: ImageBitmap,
)

data class EnemyAssets(
    val enemyFrame: ImageBitmap,
    val enemyName: String,
    val enemyWeapon: ImageBitmap
)

data class CharacterAssets(
    val flying1: ImageBitmap,
    val flying2: ImageBitmap,
    val flyingFirm: ImageBitmap,
    val flyingPowerup: ImageBitmap,
    val firingCannonGun: ImageBitmap,
    val obstacleHitFrame: ImageBitmap,
    val noWingsFrame: ImageBitmap
)
