package com.example.viktorsjourney.core.engine



data class GameObject(
    val id: Int,
    val type: GameObjectType,
    var x: Float,
    var y: Float,
    var width: Int,
    var height: Int,
    var collisionScale: Float,
    var active: Boolean = true,
    var scored: Boolean = false
)
