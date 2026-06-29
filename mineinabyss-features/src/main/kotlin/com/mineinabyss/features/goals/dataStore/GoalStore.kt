package com.mineinabyss.features.goals.dataStore

import com.mineinabyss.features.goals.Goal
import com.mineinabyss.idofront.datastore.KeyedMinecraftDataStore
import com.mineinabyss.idofront.serialization.InstantAsEpochSecondsSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.sqlite.datastore.keyedJsonTable
import org.bukkit.entity.Player
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class GoalProgress(
    val completed: Boolean, // 'completed' can be used as is for goals that doesn't need state tracking. Their progress can be directly handled in the listener
    val completionTime: @Serializable(with = InstantAsEpochSecondsSerializer::class) Instant = Clock.System.now(),
    val visitedPlaces : List<String> = emptyList(),
    val craftedItems: List<String> = emptyList(),
    val mobsKilled: List<Map<String, Int>> = emptyList(),
    val extras: JsonObject = JsonObject(emptyMap()),

    )

object GoalStore : KeyedMinecraftDataStore<String, GoalProgress>(
    keyedJsonTable("goals") {
        index("completionTime", "data ->> '$.completionTime'")
    },
    String.serializer(),
    GoalProgress.serializer()
) {
    context(tx: WriteTransaction)
     fun isCompleted(player: Player, goal: String): Boolean {
        return false
    }

    context(tx: WriteTransaction)
     fun setCompleted(player: Player, goal: String, completed: Boolean) {

    }

    context(tx: WriteTransaction)
     fun getVisitedPlaces(player: Player): List<String> {
        return emptyList()
    }

    context(tx: Transaction)
    fun getProgress(player: Player, goal: Goal): GoalProgress? {
        val progress = this[player, goal.id]
        return progress
    }

    // goals operations would be added here
}
