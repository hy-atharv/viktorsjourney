package com.example.viktorsjourney.core.engine

import com.example.viktorsjourney.core.data.models.CurrentActiveAirstrikeMissile
import com.example.viktorsjourney.core.data.models.CurrentActiveCharacterCannonBall
import com.example.viktorsjourney.core.data.models.CurrentActiveEnemyWeapon
import com.example.viktorsjourney.endless.presentation.EndlessGameplayScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.min

class EndlessGameEngine(
    private val vm: EndlessGameplayScreenViewModel
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null
    private var nextId = 1

    @Volatile
    var collisionsLockedStatus = false

    private val objects = mutableListOf<GameObject>()
    val obstacleHeads = mutableListOf<GameObject>()
    private val obstacleBodies = mutableListOf<GameObject>()
    private var fuelCell: GameObject? = null
    private var powerup: GameObject? = null
    private var character: GameObject? = null
    private var cVy = 0f
    private var enemy: GameObject? = null
    private var enemyAngle = 0f
    var enemyDirectionY = 0
    var enemySpeed = 0f
    private var characterCannonBalls = mutableListOf<CurrentActiveCharacterCannonBall>()
    private var characterCannonBallGameObjects = mutableListOf<GameObject>()
    private var enemyWeapons = mutableListOf<CurrentActiveEnemyWeapon>()
    private var enemyWeaponGameObjects = mutableListOf<GameObject>()
    private var airstrikeFighterJet: GameObject? = null
    private var airstrikeMissile: CurrentActiveAirstrikeMissile? = null
    private var airstrikeMissileGameObject: GameObject? = null
    private var platform: GameObject? = null
    var canvasWidth = 0f
    var canvasHeight = 0f
    private var distanceReferenceX = 0f
    var platformHeight = 0f
    private var platformWidth = 0f

    // Game World Physics Constants
    private val gravity = 1400f
    private val flapImpulse = -680f
    private val maxFallSpeed = 1400f

    // ------------------------------------------------------------------------------

    fun setCanvasSize(w: Float, h: Float) {
        canvasWidth = w
        canvasHeight = h
        distanceReferenceX = canvasWidth
    }

    fun setCharacter(x: Float, y: Float, width: Int, height: Int, collisionScale: Float = 0.42f) {
        val obj = GameObject(nextId++, GameObjectType.CHARACTER, x, y, width, height, collisionScale)
        character = obj
    }

    fun updateCharacterFrame(width: Int, height: Int) {
        val c = character ?: return
        c.width = width
        c.height = height
    }

    fun setCharacterCannonBall(x: Float, y: Float, angle: Float, width: Int, height: Int, id: Int, collisionScale: Float = 0.13f) {
        val obj = GameObject(id, GameObjectType.CHARACTER_CANNONBALL, x, y, width, height, collisionScale)
        objects += obj
        characterCannonBallGameObjects += obj
        characterCannonBalls += CurrentActiveCharacterCannonBall(obj.id, obj.x, obj.y, angle)
    }

    fun removeCharacterCannonBall(cannonBall: GameObject) {
        objects -= cannonBall
        characterCannonBallGameObjects -= cannonBall
        characterCannonBalls.removeIf { it.id == cannonBall.id }
    }

    fun setPlatform(x: Float, width: Int, height: Int, collisionScale: Float = 0f) {
        platformWidth = width.toFloat()
        platformHeight = height.toFloat()

        val y = canvasHeight - height
        val obj = GameObject(nextId++, GameObjectType.PLATFORM, x, y, width, height, collisionScale)
        platform = obj
    }

    fun addObstacleHead(x: Float, y: Float, width: Int, height: Int, id: Int, collisionScale: Float = 0.2f) {
        val obj = GameObject(id, GameObjectType.OBSTACLE, x, y, width, height, collisionScale)
        objects += obj
        obstacleHeads += obj
    }

    fun addObstacleBody(x: Float, y: Float, width: Int, height: Int, id: Int, collisionScale: Float = 0.18f) {
        val obj = GameObject(id, GameObjectType.OBSTACLE, x, y, width, height, collisionScale)
        objects += obj
        obstacleBodies += obj
    }

    fun removeObstacleHead(obstacle: GameObject) {
        objects -= obstacle
        obstacleHeads -= obstacle
    }

    fun removeObstacleBody(obstacle: GameObject) {
        objects -= obstacle
        obstacleBodies -= obstacle
    }

    fun clearAllObstacles() {
        objects.removeAll { it.type == GameObjectType.OBSTACLE }
        obstacleHeads.clear()
        obstacleBodies.clear()
    }

    fun setPowerup(x: Float, y: Float, width: Int, height: Int, id: Int, collisionScale: Float = 0.13f) {
        val obj = GameObject(id, GameObjectType.POWERUP, x, y, width, height, collisionScale)
        objects += obj
        powerup = obj
    }

    fun removePowerup(powerup: GameObject) {
        objects -= powerup
    }

    fun setFuelCell(x: Float, y: Float, width: Int, height: Int, id: Int, collisionScale: Float = 0.13f) {
        val obj = GameObject(id, GameObjectType.FUEL_CELL, x, y, width, height, collisionScale)
        objects += obj
        fuelCell = obj
    }

    fun removeFuelCell(fuelCell: GameObject) {
        objects -= fuelCell
    }

    fun setEnemy(x: Float, y: Float, width: Int, height: Int, collisionScale: Float = 0.31f) {
        val obj = GameObject(nextId++, GameObjectType.ENEMY, x, y, width, height, collisionScale)
        enemy = obj
    }

    fun removeEnemy() {
        enemy = null
    }

    fun setEnemyWeapon(x: Float, y: Float, angle: Float, width: Int, height: Int, id: Int, collisionScale: Float = 0.13f) {
        val obj = GameObject(id, GameObjectType.ENEMY_WEAPON, x, y, width, height, collisionScale)
        objects += obj
        enemyWeaponGameObjects += obj
        enemyWeapons += CurrentActiveEnemyWeapon(obj.id, obj.x, obj.y, angle)
    }

    fun removeEnemyWeapon(enemyWeapon: GameObject) {
        objects -= enemyWeapon
        enemyWeaponGameObjects -= enemyWeapon
        enemyWeapons.removeIf { it.id == enemyWeapon.id }
    }

    fun setAirstrikeFighterJet(x: Float, y: Float, width: Int, height: Int, id: Int, collisionScale: Float = 0.26f) {
        val obj = GameObject(id, GameObjectType.AIRSTRIKE_JET, x, y, width, height, collisionScale)
        airstrikeFighterJet = obj
    }

    fun removeAirstrikeFighterJet() {
        airstrikeFighterJet = null
    }

    fun setAirstrikeMissile(x: Float, y: Float, angle: Float, width: Int, height: Int, id: Int, collisionScale: Float = 0.26f) {
        val obj = GameObject(id, GameObjectType.AIRSTRIKE_MISSILE, x, y, width, height, collisionScale)
        objects += obj
        airstrikeMissileGameObject = obj
        airstrikeMissile = CurrentActiveAirstrikeMissile(obj.id, obj.x, obj.y, angle)
    }

    fun removeAirstrikeMissile() {
        airstrikeMissileGameObject?.let {
            objects -= it
        }
        airstrikeMissileGameObject = null
        airstrikeMissile = null
    }

    fun onFlyingControlTouch() {
        val c = character ?: return
        c.y -= 1     // ensure impulse triggers
        cVy = flapImpulse
    }


    // ------------------------------ ENGINE MANAGEMENT -------------------------------------------

    fun start() {
        if (job?.isActive == true) return

        job = scope.launch {
            var last = System.nanoTime()


            while (isActive) {

                val now = System.nanoTime()
                val dt = ((now - last) / 1_000_000_000f).coerceAtMost(0.05f)
                last = now

                updateCharacter(dt)
                updatePlatform()
                updateObstacles()
                updateDistanceReference()
                updateFuelCell()
                updatePowerup()
                updateCannonBall()
                updateEnemyWeapon()
                updateAirstrikeJet()
                updateAirstrikeMissile()
                missileCollision()
                if (vm.currentEndlessEnemySpawnStatus && vm.enemyEntryStatus){
                    updateEnemyEntry()
                }
                else if (vm.currentEndlessEnemySpawnStatus && !vm.enemyEntryStatus){
                    updateEnemy()
                    cannonBallCollisions()
                    enemyWeaponCollisions()
                }
                collisions()

                // update compose state
                withContext(Dispatchers.Main) {
                    updateVM()
                }

                delay(10)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    // --------------------------------- PLATFORM, OBSTACLES, CONSUMABLES, WEAPONS AND POWERUPS MOVEMENT AND DISTANCE REFERENCE -------------------------------------------

    private fun updatePlatform() {
        val p = platform ?: return
        p.x -= 8f

        if (p.x <= -p.width) {
            p.x += p.width
        }
    }

    private fun updateObstacles() {
        val heads = obstacleHeads.toList() // safe snapshot
        for (head in heads) {
            head.x -= 8f
        }

        val bodies = obstacleBodies.toList() // safe snapshot
        for (body in bodies) {
            body.x -= 8f
        }
    }

    private fun updateFuelCell() {
        val c = fuelCell ?: return
        c.x -= 8f
    }

    private fun updatePowerup() {
        val p = powerup ?: return
        p.x -= 8f
    }

    private fun updateCannonBall() {
        val cannonBallObjects = characterCannonBallGameObjects.toList() // safe snapshot
        for (cannonball in cannonBallObjects) {
            val angle = characterCannonBalls[cannonBallObjects.indexOf(cannonball)].angle
            val rad = Math.toRadians(angle.toDouble())
            val dx = kotlin.math.cos(rad).toFloat() * 9f
            val dy = kotlin.math.sin(rad).toFloat() * 9f

            cannonball.x += dx
            cannonball.y += dy
        }
    }

    private fun updateEnemyWeapon() {
        if (enemyWeaponGameObjects.isEmpty()) return

        val enemyWeaponObjects = enemyWeaponGameObjects.toList() // safe snapshot
        for (enemyWeapon in enemyWeaponObjects) {
            val angle = enemyWeapons[enemyWeaponObjects.indexOf(enemyWeapon)].angle
            val rad = Math.toRadians(angle.toDouble())
            val dx = kotlin.math.cos(rad).toFloat() * 9f
            val dy = kotlin.math.sin(rad).toFloat() * 9f

            enemyWeapon.x -= dx
            enemyWeapon.y += dy
        }
    }

    private fun updateDistanceReference() {
        distanceReferenceX -= 8f
    }

    fun resetDistanceReference() {
        distanceReferenceX = canvasWidth
    }


    // --------------------------------- CHARACTER AND ENEMY MOVEMENTS-------------------------------------------

    private fun updateCharacter(dt: Float) {
        val c = character ?: return

        cVy = (cVy + gravity * dt).coerceAtMost(maxFallSpeed)
        c.y += cVy * dt

        // Top boundary
        if (c.y < 0f) {
            c.y = 0f
            cVy = 0f
        }

        // Bottom Boundary
        val maxY = (canvasHeight - platformHeight)
        if (c.y > maxY) {
            c.y = maxY
            cVy = 0f
        }
    }

    private fun updateEnemyEntry() {
        val e = enemy ?: return
        e.x -= 8f
        if (e.x <= canvasWidth-(e.width*0.85f)) {
            vm.disableEnemyEntry()
        }
    }

    private fun updateEnemy() {
        val c = character ?: return
        val e = enemy ?: return

        val enemyThrowingRefX = e.x
        val enemyThrowingRefY = e.y + e.height * 0.5f

        val characterHitRefX = c.x + c.width
        val characterHitRefY = c.y + c.height * 0.5f

        // Random Enemy Y Movements
        if (enemyDirectionY==1 && (e.y + enemySpeed)>=(canvasHeight - (1.5*platformHeight))){
            e.y -= 100f
        }
        else if (enemyDirectionY==-1 && (e.y - enemySpeed)<=0f){
            e.y += 100f
        }
        else{
            e.y += enemySpeed*enemyDirectionY
        }

        // Enemy Angle Based on Player Reference
        val diffX = enemyThrowingRefX - characterHitRefX
        val diffY = enemyThrowingRefY - characterHitRefY

        enemyAngle = Math.toDegrees(atan2(diffY.toDouble(), diffX.toDouble())).toFloat().coerceIn(-12.5f, 12.5f)
    }

    private fun updateAirstrikeJet() {
        val j = airstrikeFighterJet ?: return
        j.x += 8f
    }

    private fun updateAirstrikeMissile() {
        val mObj = airstrikeMissileGameObject ?: return
        val m = airstrikeMissile ?: return
        val e = enemy ?: return

        val missileRefX = mObj.x
        val missileRefY = mObj.y + mObj.height * 0.5f

        val enemyHitRefX = e.x
        val enemyHitRefY = e.y + e.height * 0.5f

        // Missile Angle Locked on Enemy Position
        val dx = enemyHitRefX - missileRefX
        val dy = enemyHitRefY - missileRefY
        val missileAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()

        m.angle = missileAngle

        val rad = Math.toRadians(missileAngle.toDouble())
        val mdx = kotlin.math.cos(rad).toFloat() * 12f
        val mdy = kotlin.math.sin(rad).toFloat() * 12f

        m.x += mdx
        m.y += mdy
        mObj.x += mdx
        mObj.y += mdy
    }

    // --------------------------------- UPDATE VIEW MODEL -------------------------------------------

    private fun updateVM() {
        character?.let {
            vm.updateCharacterPosition(it.x, it.y)
            vm.updateCharacterAngle((cVy / maxFallSpeed) * 20f)
            if (it.y >= canvasHeight-platformHeight && !vm.gameLocationPortalStatus && !vm.invinciblePowerupStatus){
                vm.onObstacleHit()
            }
        }
        platform?.let {
            vm.updatePlatformPosition(it.x)
        }
        for (i in obstacleHeads.size - 1 downTo 0) {
            val head = obstacleHeads[i]
            vm.updateObstacleHeadPosition(head)
        }
        for (i in obstacleBodies.size - 1 downTo 0) {
            val body = obstacleBodies[i]
            vm.updateObstacleBodyPosition(body)
        }
        fuelCell?.let {
            vm.updateFuelCellPosition(it)
        }
        powerup?.let {
            vm.updatePowerupPosition(it)
        }
        for (i in characterCannonBallGameObjects.size - 1 downTo 0) {
            val cannonBall = characterCannonBallGameObjects[i]
            vm.updateCharacterCannonBallPosition(cannonBall)
        }
        enemy?.let {
            if (vm.enemyEntryStatus){
                vm.updateEnemyEntryPosition(it.x)
            }
            else{
                vm.updateEnemyPosition(it)
                vm.updateEnemyAngle(enemyAngle)
            }
        }
        for (i in enemyWeaponGameObjects.size - 1 downTo 0) {
            val enemyWeapon = enemyWeaponGameObjects[i]
            vm.updateEnemyWeaponPosition(enemyWeapon)
        }
        airstrikeFighterJet?.let {
            vm.updateAirstrikeJetPosition(it.x, it.y)
        }
        airstrikeMissile?.let {
            vm.updateAirstrikeMissilePosition(it)
        }
        vm.updateGameCurrentDistanceReferenceX(distanceReferenceX)
    }

    // -------------------------------- COLLISION HANDLING ------------------------------------------

    private fun collisions() {
        if (collisionsLockedStatus) return

        val c = character ?: return

        for (i in objects.size - 1 downTo 0) {
            if (collisionsLockedStatus) return
            val obj = objects.getOrNull(i) ?: continue
            if (!obj.active) continue
            if (!aabb(c, obj)) continue
            if (!circleCollision(c, obj)) continue

            obj.active = false

            when (obj.type) {
                GameObjectType.OBSTACLE -> vm.onObstacleHit()
                GameObjectType.POWERUP -> vm.onPowerupHit(obj)
                GameObjectType.FUEL_CELL -> vm.onFuelCellHit()
                else -> {}
            }
        }
    }

    private fun cannonBallCollisions() {
        val e = enemy ?: return

        for (i in characterCannonBallGameObjects.size - 1 downTo 0) {
            val cannonBallObj = characterCannonBallGameObjects.getOrNull(i) ?: continue
            if (!cannonBallObj.active) continue
            if (!aabb(cannonBallObj, e)) continue
            if (!circleCollision(cannonBallObj, e)) continue

            cannonBallObj.active = false

            vm.onEnemyHitByCharacterCannonBall(cannonBallObj)
        }
    }

    private fun enemyWeaponCollisions() {
        if (collisionsLockedStatus) return

        val c = character ?: return

        for (i in enemyWeaponGameObjects.size - 1 downTo 0) {
            val enemyWeaponObj = enemyWeaponGameObjects.getOrNull(i) ?: continue
            if (!enemyWeaponObj.active) continue
            if (!aabb(enemyWeaponObj, c)) continue
            if (!circleCollision(enemyWeaponObj, c)) continue

            enemyWeaponObj.active = false

            vm.onCharacterHitByEnemyWeapon(enemyWeaponObj)
        }
    }

    private fun missileCollision() {
        val e = enemy ?: return
        val m = airstrikeMissileGameObject ?: return

        if (!aabb(m, e)) return
        if (!circleCollision(m, e)) return

        m.active = false

        vm.onEnemyHitByAirstrikeMissile()

    }

    // ----------------------------- COLLISION CHECKS --------------------------------------------

    private fun aabb(a: GameObject, b: GameObject, paddingPx: Float = 2f): Boolean {
        // shrink both boxes by paddingPx on each side to make collision less "touchy"
        val ax0 = a.x + paddingPx
        val ay0 = a.y + paddingPx
        val ax1 = a.x + a.width - paddingPx
        val ay1 = a.y + a.height - paddingPx

        val bx0 = b.x + paddingPx
        val by0 = b.y + paddingPx
        val bx1 = b.x + b.width - paddingPx
        val by1 = b.y + b.height - paddingPx

        return !(ax1 < bx0 || ax0 > bx1 || ay1 < by0 || ay0 > by1)
    }


    private fun circleCollision(a: GameObject, b: GameObject): Boolean {

        val ax = a.x + a.width * 0.5f
        val ay = a.y + a.height * 0.5f
        val bx = b.x + b.width * 0.5f
        val by = b.y + b.height * 0.5f

        val ar = min(a.width, a.height) * a.collisionScale  // radius
        val br = min(b.width, b.height) * b.collisionScale

        val dx = ax - bx
        val dy = ay - by
        val distSq = dx * dx + dy * dy
        val radiusSum = ar + br

        return distSq < radiusSum * radiusSum
    }


}