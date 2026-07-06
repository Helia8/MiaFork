package com.mineinabyss.features.goals.dataStore

import com.mineinabyss.features.goals.ConditionProgress
import com.mineinabyss.idofront.datastore.KeyedMinecraftDataStore
import com.mineinabyss.idofront.serialization.InstantAsEpochSecondsSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.datastore.keyedJsonTable
import org.bukkit.entity.Entity
import kotlin.time.Instant
import kotlin.uuid.toKotlinUuid

@Serializable
data class GoalProgress(
    val completed: Boolean = false,
    val completionTime: @Serializable(with = InstantAsEpochSecondsSerializer::class) Instant? = null,
    val conditions: Map<Int, ConditionProgress> = emptyMap(),
)

open class GoalProgressStore(tableName: String) : KeyedMinecraftDataStore<String, GoalProgress>(
    keyedJsonTable(tableName) {
        index("completionTime", "data ->> '$.completionTime'")
    },
    String.serializer(),
    GoalProgress.serializer(),
) {
    context(tx: Transaction)
    fun getAll(entity: Entity): Map<String, GoalProgress> {
        return tx.getList(
            "SELECT json(key), json(data) FROM $tableName WHERE id = ? AND data IS NOT null",
            entity.uniqueId.toKotlinUuid(),
        ) {
            json.decodeFromString(keySerializer, getText(0)) to json.decodeFromString(serializer, getText(1))
        }.toMap()
    }
}

object GoalStore : GoalProgressStore("goals")
