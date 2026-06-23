package com.mineinabyss.features.quests

import com.mineinabyss.idofront.Idofront
import com.mineinabyss.idofront.datastore.db
import com.mineinabyss.idofront.datastore.setupDataStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import me.dvyy.sqlite.Database
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.sqlite.datastore.KeyedDataStore
import me.dvyy.sqlite.datastore.keyedJsonTable
import org.bukkit.entity.Player
import java.util.*

object QuestDataStore : KeyedDataStore<String, QuestProgress>(keyedJsonTable("quests"), String.serializer(), QuestProgress.serializer()) {
    context(tx: WriteTransaction)
    fun resetFor(uuid: UUID) {
        tx.exec("DELETE FROM $tableName WHERE uuid = ?", uuid)
    }
}

@Serializable
data class QuestProgress(
    val completed: Boolean,
)


object VisitedLocsDataStore : KeyedDataStore<String, QuestProgress>(keyedJsonTable("visited_locations"), String.serializer(), QuestProgress.serializer())

@Serializable
data class VisitInfo(
    val seen: Boolean,
)

class QuestRepository(
    val db: Database = Idofront.db,
) {
    init {
        Idofront.setupDataStore(QuestDataStore)
        Idofront.setupDataStore(VisitedLocsDataStore)
    }

    suspend fun complete(player: Player, quest: String) {

    }

    suspend fun unlock(player: Player, quest: String) {

    }

    suspend fun hasVisited(player: Player, location: String): Boolean {
        TODO()
    }

    suspend fun hasCompleted(player: Player, quest: String): Boolean {
        TODO()
    }

    suspend fun resetQuests(player: Player) {
        db.write { QuestDataStore.resetFor(player.uniqueId) }
    }
}