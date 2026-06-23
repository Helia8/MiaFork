package com.mineinabyss.features.achievements.data

import com.mineinabyss.idofront.datastore.KeyedMinecraftDataStore
import com.mineinabyss.idofront.serialization.InstantAsEpochSecondsSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject
import me.dvyy.sqlite.datastore.keyedJsonTable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class AchievementProgress(
    val completed: Boolean,
    val completionTime: @Serializable(with = InstantAsEpochSecondsSerializer::class) Instant = Clock.System.now(),
    val extras: JsonObject = JsonObject(emptyMap()),
)

object AchievementStore : KeyedMinecraftDataStore<String, AchievementProgress>(
    keyedJsonTable("achievements") {
        index("completionTime", "data ->> '$.completionTime'")
    },
    String.serializer(),
    AchievementProgress.serializer()
)
