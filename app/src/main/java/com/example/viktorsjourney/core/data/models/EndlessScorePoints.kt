package com.example.viktorsjourney.core.data.models

enum class EndlessScorePoints(
    val points: Float
) {
    ESCAPE_OBSTACLE(points = 0.5f),
    KILL_ENEMY(points = 5f),
    NEW_LOCATION(points = 10f)
}