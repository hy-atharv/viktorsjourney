package com.example.viktorsjourney.core.data.models

import java.util.concurrent.atomic.AtomicInteger

data class CurrentActiveObstacleHead(
    val id: Int = ObstacleIdGen.nextId(),
    var x: Float,
    var y: Float,
    val type: ObstacleSourceType
)

data class CurrentActiveObstacleBody(
    val id: Int = ObstacleIdGen.nextId(),
    var x: Float,
    var y: Float,
    val type: ObstacleSourceType
)

enum class ObstacleSourceType {
    TOP,
    BOTTOM
}

data class CurrentActiveFuelCell(
    val id: Int = ObstacleIdGen.nextId(),
    var x: Float,
    var y: Float
)

data class CurrentActivePowerup(
    val id: Int = ObstacleIdGen.nextId(),
    var x: Float,
    var y: Float,
    val type: Powerups
)

data class CurrentActiveCharacterCannonBall(
    val id: Int = ObstacleIdGen.nextId(),
    var x: Float,
    var y: Float,
    var angle: Float
)


data class CurrentActiveEnemyWeapon(
    val id: Int = ObstacleIdGen.nextId(),
    var x: Float,
    var y: Float,
    var angle: Float
)

data class CurrentActiveFighterJet(
    val id: Int = ObstacleIdGen.nextId(),
    var x: Float,
    var y: Float,
)

data class CurrentActiveAirstrikeMissile(
    val id: Int = ObstacleIdGen.nextId(),
    var x: Float,
    var y: Float,
    var angle: Float
)

object ObstacleIdGen {
    private val counter = AtomicInteger(0)
    fun nextId() = counter.getAndIncrement()
}