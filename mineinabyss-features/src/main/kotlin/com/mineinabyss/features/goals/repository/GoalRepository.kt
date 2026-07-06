package com.mineinabyss.features.goals.repository

import com.mineinabyss.features.goals.ConditionProgress
import com.mineinabyss.features.goals.FactKind
import com.mineinabyss.features.goals.Goal
import com.mineinabyss.features.goals.dataStore.GoalProgress
import com.mineinabyss.features.goals.dataStore.GoalProgressStore
import com.mineinabyss.idofront.datastore.launchWrite
import com.mineinabyss.idofront.datastore.read
import com.mineinabyss.idofront.messaging.success
import org.bukkit.entity.Player
import kotlin.time.Clock

class GoalRepository(
    private val goals: List<Goal>,
    private val cache: GoalCache,
    private val store: GoalProgressStore,
) {
    var onComplete: (Player, Goal) -> Unit = { player, goal ->
        player.success("Goal completed: ${goal.name}") //TODO proper completion notification/rewards
    }

    suspend fun loadPlayer(player: Player) {
        val stored = player.read { store.getAll(player) }
        // only store online players
        if (player.isOnline) cache.load(player, stored)
    }

    fun unloadPlayer(player: Player)  { return  cache.unload(player) }

    fun progress(player: Player, goalId: String): GoalProgress? { return  cache[player, goalId] }

    fun allProgress(player: Player): Map<String, GoalProgress>  {return  cache.all(player) }

    fun hasGoal(player: Player, goal: Goal): Boolean {
        return cache[player, goal.id] != null
    }

    fun startGoal(player: Player, goal: Goal): Boolean {
        if (cache[player, goal.id] != null) return false // already tracked
        persist(player, goal.id, GoalProgress())
        return true // tracking start
    }

    fun forceComplete(player: Player, goal: Goal) {
        val current = cache[player, goal.id]
        if (current?.completed == true) return
        val progress = (current ?: GoalProgress())
            .copy(completed = true, completionTime = Clock.System.now())
        persist(player, goal.id, progress)
        onComplete(player, goal)
    }


    fun recordFact(player: Player, kind: FactKind, value: String) {
        goals.forEach { goal -> applyFact(player, goal, kind, value) }
    }

    // Seeds regions the player is already standing in, since enter events only fire on transitions
    fun seedRegions(player: Player, regions: Collection<String>) {
        regions.forEach { recordFact(player, FactKind.REGION_ENTER, it) }
    }

    private fun applyFact(player: Player, goal: Goal, kind: FactKind, value: String) {
        if (!goal.tracksAny(kind, value)) return
        updateProgress(player, goal) { progress ->
            val updated = progress.conditions.toMutableMap()
            goal.conditions.forEachIndexed { i, condition ->
                if (condition.tracks(kind, value))
                    updated[i] = condition.advance(updated[i] ?: ConditionProgress(), value)
            }
            progress.copy(conditions = updated)
        }
    }

    fun updateProgress(player: Player, goal: Goal, update: (GoalProgress) -> GoalProgress) {
        val current = cache[player, goal.id] ?: return
        if (current.completed) return
        var updated = update(current)
        if (updated == current) return
        val nowCompleted = goal.validate(updated)
        if (nowCompleted)  {
            updated = updated.copy(completed = true, completionTime = Clock.System.now())
        }
        persist(player, goal.id, updated)
        if (nowCompleted)  {
            onComplete(player, goal)
        }
    }

    // Store progress on db
    private fun persist(player: Player, goalId: String, progress: GoalProgress) {
        cache[player, goalId] = progress
        player.launchWrite { store[player, goalId] = progress }
    }
}
