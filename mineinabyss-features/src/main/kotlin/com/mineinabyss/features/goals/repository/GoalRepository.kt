package com.mineinabyss.features.goals.repository

import com.mineinabyss.features.goals.Goal
import com.mineinabyss.features.goals.dataStore.GoalProgress
import com.mineinabyss.features.goals.dataStore.GoalStore
import com.mineinabyss.idofront.Idofront
import com.mineinabyss.idofront.datastore.db
import com.mineinabyss.idofront.datastore.readBlocking
import me.dvyy.sqlite.Database
import org.bukkit.entity.Player

class GoalRepository(
    private val db: Database = Idofront.db,
) {
    suspend fun tryComplete(player: Player, goal: String): Boolean {
        return false
    }

    suspend fun validate(player: Player, goal: Goal): Boolean {
         val success = db.read {
            val progress = GoalStore.getProgress(player, goal) ?: return@read false

            goal.validate(progress)
        }
        return success
    }

    suspend fun update(player: Player, goal: Goal) {

    }

    suspend fun hasGoal(player: Player, goal: Goal): Boolean {
        val success = db.read {
            GoalStore.getProgress(player, goal) ?: return@read false
            true
        }
        return success
    }
}


