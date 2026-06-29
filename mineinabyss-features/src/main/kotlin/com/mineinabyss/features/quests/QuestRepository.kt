package com.mineinabyss.features.quests

import com.mineinabyss.features.achievements.data.AchievementStore.get
import com.mineinabyss.features.achievements.data.GoalProgress
import com.mineinabyss.idofront.Idofront
import com.mineinabyss.idofront.datastore.db
import com.mineinabyss.idofront.datastore.setupDataStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import me.dvyy.sqlite.Database
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.sqlite.connection.NoSQLiteStatement.getText
import me.dvyy.sqlite.datastore.KeyedDataStore
import me.dvyy.sqlite.datastore.keyedJsonTable
import org.bukkit.entity.Player
import java.util.*

object QuestDataStore : KeyedDataStore<String, GoalProgress>(
    keyedJsonTable("goals"), String.serializer(), GoalProgress.serializer()) {
    context(tx: WriteTransaction)
    fun resetFor(uuid: UUID) {
        tx.exec("DELETE FROM $tableName WHERE uuid = ?", uuid)
    }

    context(tx: WriteTransaction)
    fun getVisited(player: Player) : List<String> {
        val progress: GoalProgress = get(player, "visit_quest") ?: return emptyList()
        return progress.visitedPlaces
        val a = tx.getOrNull("SELECT data ->>'$.visitedPlaces' FROM 'goals' WHERE uuid = ? and key = ? ", playeruuid, "visit_quest"){ json.decodeFromString<List<String>>(getText(0)) }
//        return a ?: emptyList()
        // then we're able to check the list against the config to infer the achievement progress and the like !
        // if (visited match config.needed) {
        //  questdatastore.complete(player, quest name)
        //  player.sendMessage("quest completed")
        // }
    }
}

//@Serializable
//data class GoalProgress(
//    val completed: Boolean,
//)


object VisitedLocsDataStore : KeyedDataStore<String, GoalProgress>(keyedJsonTable("visited_locations"), String.serializer(), GoalProgress.serializer())

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