package com.example.viktorsjourney.endless.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.util.lerp
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.viktorsjourney.R
import com.example.viktorsjourney.core.data.models.GameCountry
import com.example.viktorsjourney.core.data.models.Map
import com.example.viktorsjourney.ui.theme.GameTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@Composable
fun SelectMapScreen(
    navigator: NavHostController,
    viewModel: SelectMapScreenViewModel = koinViewModel<SelectMapScreenViewModel>()
) {

    val mapsList = viewModel.mapsList

    val hopCount by viewModel.hopCountForLastChosenMapIndex.collectAsState()

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % mapsList.size)
    )
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenPx = with(density) { ((configuration.screenWidthDp.dp/2)).toPx() }

    LaunchedEffect(hopCount) {
        val totalDistance = (screenPx+2) *  (hopCount*2)    //hopCount to jump to last chosen Map
        val durationMs = 400L
        val frames = 240                          // 240 increments (smooth)
        val step = totalDistance / frames         // scroll per frame
        val delayPerFrame = durationMs / frames   // ms per frame

        repeat(frames) {
            lazyListState.scrollBy(step)
            delay(delayPerFrame)
        }
    }



    GameTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(R.drawable.game_uiux_screen),
                    contentScale = ContentScale.Crop
                ),
            color = Color.Transparent
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                //.windowInsetsPadding(WindowInsets.systemBars),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        .border(width = 2.dp, color = MaterialTheme.colorScheme.secondary),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Endless Screen",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 50.dp, bottom = 10.dp, start = 15.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                navigator.popBackStack()
                            }
                    )
                    Text(
                        text = "Maps",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 85.dp)
                            .padding(top = 50.dp, bottom = 10.dp)
                    )
                }
                LazyRow(
                    state = lazyListState,
                    flingBehavior = snapBehavior
                ) {
                    items(Int.MAX_VALUE) { i ->
                        val realIndex = i % mapsList.size
                        MapScroll(
                            map = mapsList[realIndex],
                            index = i,
                            listState = lazyListState
                        )
                    }
                }
                //Select Map Button
                Box(
                    modifier = Modifier.padding(bottom = 5.dp).align(Alignment.CenterHorizontally)
                        .height(30.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f))
                        .clickable {
                            val currentIndex = lazyListState.firstVisibleItemIndex
                            val realIndex = currentIndex % mapsList.size
                            when(mapsList[realIndex]){
                                Map.Croatia -> viewModel.updateGameCountry(GameCountry.CROATIA)
                                Map.Japan -> viewModel.updateGameCountry(GameCountry.JAPAN)
                                Map.Russia -> viewModel.updateGameCountry(GameCountry.RUSSIA)
                                Map.Egypt -> viewModel.updateGameCountry(GameCountry.EGYPT)
                                Map.India -> viewModel.updateGameCountry(GameCountry.INDIA)
                            }
                            navigator.popBackStack()
                        },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Select",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                // Fuel Cell and Powerups Description
                Box(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.92f)),
                    contentAlignment = Alignment.TopCenter
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp, top = 10.dp)
                    ) {
                        Text(
                            text = "Game Items",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 20.dp)
                        )

                        FuelCell(R.drawable.fuel_cell_icon, "Fuel Cell")
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            Powerup(R.drawable.ammunition_icon, "Ammunition")
                            Powerup(R.drawable.invincible_shield_icon, "Invincible")
                            Powerup(R.drawable.extra_life_icon, "Extra Life")
                            AnywhereMap(R.drawable.map_icon, "Anywhere Map")
                        }
                    }
                }

            }
        }
    }
}



@Composable
fun MapScroll(
    map: Map,
    index: Int,
    listState: LazyListState
) {
    // screen center in px
    val screenCenterPx = with(LocalDensity.current) {
        (LocalConfiguration.current.screenWidthDp.dp / 2).toPx()
    }

    // find the visible item info for this index (if visible)
    val itemInfo = listState.layoutInfo
        .visibleItemsInfo
        .firstOrNull { it.index == index }

    // compute target scale (default small)
    val targetScale = remember(itemInfo) {
        if (itemInfo == null) {
            // not visible => shrunken
            0.85f
        } else {
            // item center in px (offset + half size)
            val itemCenter = itemInfo.offset + itemInfo.size / 2f
            val distance = abs(screenCenterPx - itemCenter)

            // normalize distance 0..1 (0 = center, 1 = far edge)
            // maxDistance choose screenCenterPx so center->edge maps 0..1
            val normalized = (distance / screenCenterPx).coerceIn(0f, 1f)

            // interpolate scale: center -> 1.1f, edge -> 0.85f
            lerp(1.0f, 0.85f, normalized)
        }
    }

    // animate the scale smoothly
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 220)
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .padding(horizontal = 10.dp)
    ) {
        // --- original MapScroll content ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 5.dp)
                .paint(
                    painter = painterResource(R.drawable.scroll),
                    contentScale = ContentScale.Crop,
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(top = 53.dp)
            ) {
                // Map Name
                Text(
                    text = map.mapName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(15.dp)
                )

                // Map Image
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .paint(
                            painter = painterResource(map.mapImage),
                            contentScale = ContentScale.Crop
                        )
                )
            }
        }
        // -----------------------------------------
    }
}



@Composable
fun FuelCell(
    imageId: Int,
    desc: String
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(horizontal = 7.dp)
    ) {
        Text(
            text = desc,
            style = TextStyle(
                fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Start,
        )
        Icon(
            painter = painterResource(imageId),
            contentDescription = "FuelCell",
            modifier = Modifier.size(52.dp),
            tint = Color.Unspecified
        )
    }
}


@Composable
fun Powerup(
    imageId: Int,
    powerDesc: String
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(horizontal = 7.dp)
    ) {
        Text(
            text = powerDesc,
            style = TextStyle(
                fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Start,
        )
        Icon(
            painter = painterResource(imageId),
            contentDescription = "Powerup",
            modifier = Modifier.size(50.dp),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun AnywhereMap(
    imageId: Int,
    powerDesc: String
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(horizontal = 7.dp)
    ) {
        Text(
            text = powerDesc,
            style = TextStyle(
                fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Start,
        )
        Icon(
            painter = painterResource(imageId),
            contentDescription = "Anywhere Map",
            modifier = Modifier.size(50.dp),
            tint = Color.Unspecified
        )
    }
}
