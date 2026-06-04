package com.example.viktorsjourney.core.engine

import kotlin.random.Random

object GameRNG {
    var rng = Random(System.nanoTime())

    fun reseed() {
        rng = Random(System.nanoTime())
    }

    fun nextInt(min: Int, max: Int): Int {
        return rng.nextInt(min, max+1)
    }

    fun nextFloat(): Float {
        return rng.nextFloat() // 0 to 1 (1 is exclusive)
    }
}