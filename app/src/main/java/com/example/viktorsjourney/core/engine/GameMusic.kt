package com.example.viktorsjourney.core.engine

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes

fun MediaPlayer.safePlay(context: Context, @RawRes resId: Int, loop: Boolean = true) {
    try { stop() } catch (_: Exception) {}
    try { reset() } catch (_: Exception) {}

    val afd = context.resources.openRawResourceFd(resId)
    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
    afd.close()

    isLooping = loop
    prepare()
    start()
}

fun SoundPool.safePlay(
    soundID: Int,
    leftVolume: Float = 1f,
    rightVolume: Float = 1f,
    priority: Int = 1,
    loop: Int = 0,
    rate: Float = 1f,
    isSoundOn: Boolean
) {
    if (!isSoundOn) return
    this.play(soundID, leftVolume, rightVolume, priority, loop, rate)
}



enum class PlayingSFX{
    WINGS_FLAP,
    OBSTACLE_HIT,
    FUEL_CELL_PICKUP,
    POWERUP_PICKUP,
    FALLING_DOWN,
    FIRE_CANNONBALL,
    CANNONBALL_HIT_ON_ENEMY,
    HEALTH_DAMAGE_BY_ENEMY,
    AIRSTRIKE_JET_PASSBY,
    AIRSTRIKE_EXPLOSION,
    LOSE_FIGHT_MUSIC,
    WIN_FIGHT_MUSIC
}