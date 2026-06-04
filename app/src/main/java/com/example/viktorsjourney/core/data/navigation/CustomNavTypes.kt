package com.example.viktorsjourney.core.data.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.example.viktorsjourney.core.data.models.EndlessAchievement
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

val navJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}

object EndlessAchievementListNavType :
    NavType<List<EndlessAchievement>>(isNullableAllowed = false) {

    override fun put(bundle: Bundle, key: String, value: List<EndlessAchievement>) {
        bundle.putString(
            key,
            navJson.encodeToString(
                ListSerializer(EndlessAchievement.serializer()),
                value
            )
        )
    }

    override fun get(bundle: Bundle, key: String): List<EndlessAchievement> {
        return navJson.decodeFromString(
            ListSerializer(EndlessAchievement.serializer()),
            bundle.getString(key)!!
        )
    }

    override fun parseValue(value: String): List<EndlessAchievement> {
        return navJson.decodeFromString(
            ListSerializer(EndlessAchievement.serializer()),
            value
        )
    }
}