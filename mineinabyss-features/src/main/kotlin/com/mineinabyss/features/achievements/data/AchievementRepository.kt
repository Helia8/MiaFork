package com.mineinabyss.features.achievements.data

import com.mineinabyss.idofront.Idofront
import com.mineinabyss.idofront.datastore.db
import me.dvyy.sqlite.Database
import me.dvyy.sqlite.WriteTransaction
import org.bukkit.entity.Player



class AchievementRepository(
    private val db: Database = Idofront.db,
) {
    suspend fun setCompleted(player: Player, key: String, completed: Boolean) {
        db.write {
            AchievementStore[player, key] = GoalProgress(completed)
        }
    }

    context(tx: WriteTransaction)
    suspend fun hasCompleted(player: Player, key: String): Boolean {
        val a = tx.getOrNull("SELECT data ->>'$.completed' FROM 'goals' WHERE uuid = ? and key = ? ", player, "visit_quest"){ json.decodeFromString<List<String>>(getText(0)) }
        return a ?: emptyList()
    }
}


