package com.mineinabyss.features.achievements.data

import com.mineinabyss.idofront.Idofront
import com.mineinabyss.idofront.datastore.db
import me.dvyy.sqlite.Database
import org.bukkit.entity.Player

class AchievementRepository(
    private val db: Database = Idofront.db,
) {
    suspend fun setCompleted(player: Player, key: String, completed: Boolean) {
        db.write {
            AchievementStore[player, key] = AchievementProgress(completed)
        }
    }

    suspend fun hasCompleted(player: Player, key: String): Boolean {
        TODO()
    }
}