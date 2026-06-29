package com.mineinabyss.features.goals

import com.mineinabyss.features.goals.dataStore.GoalProgress
import com.mineinabyss.idofront.serialization.InstantAsEpochSecondsSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.time.Clock
import kotlin.time.Instant

data class Goal(
    // Goal info
    val id: String,
    val name: String,
    val description: String,

    // Goal content
    val toVisit : List<String> = emptyList(),
    val toCraft: List<String> = emptyList(),
    val toKill: List<Map<String, Int>> = emptyList(),
) {

    fun validate(progress: GoalProgress): Boolean {

        val places = progress.visitedPlaces.containsAll(toVisit)

        val items = progress.craftedItems.containsAll(toCraft)

        val mobs = toKill.all { required ->
            progress.mobsKilled.any { actual ->
                required.all { (key, count) ->
                    (actual[key] ?: 0) >= count
                }
            }
        }
        // Hopefully kotlin is smart enough to not actually store those variables 🤞
        return places && items && mobs
    }


    fun hasLocation() : Boolean {
        return toVisit.isNotEmpty();
    }
}