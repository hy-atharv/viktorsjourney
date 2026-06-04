package com.example.viktorsjourney.core.data.models


import androidx.compose.ui.graphics.ImageBitmap
import com.example.viktorsjourney.R
import org.koin.java.KoinJavaComponent.getKoin

sealed class Map(
    val mapName: String,
    val mapImage: Int
) {
    data object Croatia : Map(
        mapName = "${GameCountry.CROATIA.placeName}, ${GameCountry.CROATIA.countryName}",
        mapImage = R.drawable.croatia_chapter
    )
    data object Japan : Map(
        mapName = "${GameCountry.JAPAN.placeName}, ${GameCountry.JAPAN.countryName}",
        mapImage = R.drawable.japan_chapter
    )
    data object Russia : Map(
        mapName = "${GameCountry.RUSSIA.placeName}, ${GameCountry.RUSSIA.countryName}",
        mapImage = R.drawable.russia_chapter
    )
    data object Egypt : Map(
        mapName = "${GameCountry.EGYPT.placeName}, ${GameCountry.EGYPT.countryName}",
        mapImage = R.drawable.egypt_chapter
    )
    data object India : Map(
        mapName = "${GameCountry.INDIA.placeName}, ${GameCountry.INDIA.countryName}",
        mapImage = R.drawable.india_chapter
    )
}