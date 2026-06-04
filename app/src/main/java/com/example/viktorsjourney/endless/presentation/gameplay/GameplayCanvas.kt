package com.example.viktorsjourney.endless.presentation.gameplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import com.example.viktorsjourney.core.data.models.GameStatus
import com.example.viktorsjourney.core.data.models.ObstacleSourceType
import com.example.viktorsjourney.endless.presentation.EndlessGameplayScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EndlessGameplayCanvas(
    endlessGameViewModel: EndlessGameplayScreenViewModel = koinViewModel<EndlessGameplayScreenViewModel>()
) {

    val background = endlessGameViewModel.gameCurrentEndlessLocationBackgroundBitmap

    val portalStatus = endlessGameViewModel.gameLocationPortalStatus

    var canvasSize by remember { mutableStateOf<IntSize>(IntSize.Zero) }

    val characterX = endlessGameViewModel.characterX
    val characterY = endlessGameViewModel.characterY
    val characterAngle = endlessGameViewModel.characterAngle
    val currentFlyingFrame = endlessGameViewModel.currentCharacterFrame

    val currentPlatform = endlessGameViewModel.gameCurrentEndlessLocationPlatform
    val platformX = endlessGameViewModel.platformX
    val platformWidth = endlessGameViewModel.gameCurrentEndlessLocationPlatform.width
    val platformHeight = endlessGameViewModel.gameCurrentEndlessLocationPlatform.height

    val currentObstacleHead = endlessGameViewModel.gameCurrentEndlessLocationObstacleHead
    val currentObstacleBody = endlessGameViewModel.gameCurrentEndlessLocationObstacleBody
    val currentActiveObstacleHeads = endlessGameViewModel.currentOnScreenObstacleHeadsMap
    val currentActiveObstacleBodies = endlessGameViewModel.currentOnScreenObstacleBodiesMap

    val fuelCell = endlessGameViewModel.fuelCellImage
    val currentActiveFuelCell = endlessGameViewModel.currentOnScreenFuelCell

    val powerupsMap = endlessGameViewModel.powerupsMap
    val currentActivePowerup = endlessGameViewModel.currentOnScreenPowerup

    val characterCannonBall = endlessGameViewModel.cannonBall
    val currentActiveCharacterCannonBalls = endlessGameViewModel.currentOnScreenCharacterCannonBallsMap

    val currentEndlessEnemy = endlessGameViewModel.gameCurrentEndlessLocationEnemy.first
    val currentEndlessEnemyX = endlessGameViewModel.enemyX
    val currentEndlessEnemyY = endlessGameViewModel.enemyY
    val currentEndlessEnemyAngle = endlessGameViewModel.enemyAngle

    val currentEndlessEnemyWeapon = endlessGameViewModel.gameCurrentEndlessLocationEnemy.third
    val currentActiveEnemyWeapons = endlessGameViewModel.currentOnScreenEnemyWeaponsMap

    val enemySpawnStatus = endlessGameViewModel.currentEndlessEnemySpawnStatus

    val airstrikeJet = endlessGameViewModel.airstrikeFrame
    val airstrikeMissile = endlessGameViewModel.airstrikeMissile
    val airstrikeExplosion = endlessGameViewModel.airstrikeMissileExplosion
    val airstrikeJetObject = endlessGameViewModel.currentOnScreenAirstrikeFighterJet
    val airstrikeMissileObject = endlessGameViewModel.currentOnScreenAirstrikeMissile

    val airstrikeStatus = endlessGameViewModel.gameCurrentEndlessAirstrikeStatus
    val explosionStatus = endlessGameViewModel.missileExplosionStatus

    Canvas(
        modifier = Modifier.fillMaxSize()
            .clipToBounds()
            .zIndex(2f)
            .onSizeChanged { size ->
                if (canvasSize== IntSize.Zero){
                    canvasSize = size
                }
               endlessGameViewModel.engine.setCanvasSize(
                   size.width.toFloat(),
                   size.height.toFloat()
               )
            },
    ) {
        // MAP BACKGROUND
        drawImage(
            image = background,
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )
        // PLATFORM
        if (!portalStatus){
            drawImage(
                image = currentPlatform,
                topLeft = Offset(platformX, size.height - platformHeight)
            )
            // CONSECUTIVE PLATFORM
            drawImage(
                image = currentPlatform,
                topLeft = Offset(
                    platformX + platformWidth, size.height - platformHeight
                )
            )
        }
        // OBSTACLES
        currentActiveObstacleHeads.forEach { headObj ->
            when (headObj.value.type){
                ObstacleSourceType.TOP -> {
                    withTransform({
                        rotate(
                            degrees = 180f,
                            pivot = Offset(
                                headObj.value.x + currentObstacleHead.width / 2f,
                                headObj.value.y
                            )
                        )
                    }) {
                        drawImage(
                            image = currentObstacleHead,
                            topLeft = Offset(headObj.value.x, headObj.value.y),
                        )
                    }
                }
                ObstacleSourceType.BOTTOM -> {
                    drawImage(
                        image = currentObstacleHead,
                        topLeft = Offset(headObj.value.x, headObj.value.y)
                    )
                }
            }
        }
        currentActiveObstacleBodies.forEach { bodyObj ->
            when (bodyObj.value.type){
                ObstacleSourceType.TOP -> {
                    withTransform({
                        rotate(
                            degrees = 180f,
                            pivot = Offset(
                                bodyObj.value.x + currentObstacleBody.width / 2f,
                                bodyObj.value.y
                            )
                        )
                    }) {
                        drawImage(
                            image = currentObstacleBody,
                            topLeft = Offset(bodyObj.value.x, bodyObj.value.y),
                        )
                    }
                }
                ObstacleSourceType.BOTTOM -> {
                    drawImage(
                        image = currentObstacleBody,
                        topLeft = Offset(bodyObj.value.x, bodyObj.value.y)
                    )
                }
            }
        }
        // FUEL CELL
        if (endlessGameViewModel.gameCurrentEndlessStatus != GameStatus.ENDLESS_WINGS_DISAPPEARED
            && endlessGameViewModel.gameCurrentEndlessStatus != GameStatus.ENDLESS_FELL_DOWN_LOSS)
        {
            currentActiveFuelCell?.let {
                drawImage(
                    image = fuelCell,
                    topLeft = Offset(it.x, it.y)
                )
            }
        }
        // POWERUP
        if (endlessGameViewModel.gameCurrentEndlessStatus != GameStatus.ENDLESS_WINGS_DISAPPEARED
            && endlessGameViewModel.gameCurrentEndlessStatus != GameStatus.ENDLESS_FELL_DOWN_LOSS)
        {
            currentActivePowerup?.let {
                drawImage(
                    image = powerupsMap[it.type]!!,
                    topLeft = Offset(it.x, it.y)
                )
            }
        }
        // CHARACTER
        withTransform({
            rotate(
                degrees = characterAngle,
                pivot = Offset(
                    characterX + currentFlyingFrame.width / 2f,
                    characterY + currentFlyingFrame.height / 2f
                )
            )
        }) {
            drawImage(
                image = currentFlyingFrame,
                topLeft = Offset(characterX, characterY),
            )
        }
        // CHARACTER CANNON BALL
        currentActiveCharacterCannonBalls.forEach { cannonBallObj ->
            withTransform({
                rotate(
                    degrees = cannonBallObj.value.angle,
                    pivot = Offset(
                        cannonBallObj.value.x + characterCannonBall.width / 2f,
                        cannonBallObj.value.y + characterCannonBall.height / 2f
                    )
                )
            }) {
                drawImage(
                    image = characterCannonBall,
                    topLeft = Offset(cannonBallObj.value.x, cannonBallObj.value.y),
                )
            }
        }
        // ENEMY
        if (enemySpawnStatus){
            withTransform({
                rotate(
                    degrees = currentEndlessEnemyAngle,
                    pivot = Offset(
                        currentEndlessEnemyX + currentEndlessEnemyWeapon.width / 2f,
                        currentEndlessEnemyY + currentEndlessEnemyWeapon.height / 2f
                    )
                )
            }) {
                drawImage(
                    image = if (explosionStatus) airstrikeExplosion else currentEndlessEnemy,
                    topLeft = Offset(currentEndlessEnemyX, currentEndlessEnemyY),
                )
            }
        }
        // ENEMY WEAPON
        currentActiveEnemyWeapons.forEach { weaponObj ->
            withTransform({
                rotate(
                    degrees = weaponObj.value.angle,
                    pivot = Offset(
                        weaponObj.value.x + currentEndlessEnemyWeapon.width / 2f,
                        weaponObj.value.y + currentEndlessEnemyWeapon.height / 2f
                    )
                )
            }) {
                drawImage(
                    image = currentEndlessEnemyWeapon,
                    topLeft = Offset(weaponObj.value.x, weaponObj.value.y),
                )
            }
        }
        // AIRSTRIKE
        airstrikeJetObject?.let {
            drawImage(
                image = airstrikeJet,
                topLeft = Offset(it.x, it.y)
            )
        }
        if (airstrikeStatus){
            airstrikeMissileObject?.let {
                withTransform({
                    rotate(
                        degrees = it.angle,
                        pivot = Offset(
                            it.x + airstrikeMissile.width / 2f,
                            it.y + airstrikeMissile.height / 2f
                        )
                    )
                }) {
                    drawImage(
                        image = airstrikeMissile,
                        topLeft = Offset(it.x, it.y),
                    )
                }
            }
        }
    }
}