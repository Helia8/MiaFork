package com.mineinabyss.features.achievements

import com.mineinabyss.features.goals.Goal
import com.mineinabyss.features.goals.repository.GoalRepository
import com.mineinabyss.geary.papermc.spawning.locations.RegionService
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.plugin.Services
import org.bukkit.entity.Player

class AchievementManager(
    private val config: AchievementsConfig,
    val repository: GoalRepository,
) {
    fun autoStart(player: Player) {
        val completed = repository.allProgress(player).filterValues { it.completed }.keys
        var startedAny = false
        config.achievements.forEach { achievement ->
            if (!achievement.isUnlockedBy(completed)) return@forEach
            if (repository.startGoal(player, achievement.toGoal())) {
                startedAny = true
            }
        }
        if (startedAny)  {
            seedCurrentRegions(player)
        }
    }

    fun onComplete(player: Player, goal: Goal) {
        player.success("Achievement unlocked: ${goal.name}")
        val completed = repository.allProgress(player).filterValues { it.completed }.keys
        var startedAny = false
        config.achievements
            .filter { goal.id in it.requires && it.isUnlockedBy(completed) }
            .forEach { gated ->
                if (repository.startGoal(player, gated.toGoal())) startedAny = true
            }
        if (startedAny) seedCurrentRegions(player)
    }

    private fun seedCurrentRegions(player: Player) {
        val regions = Services.getOrNull<RegionService>()?.regionsAt(player.location) ?: return
        repository.seedRegions(player, regions)
    }
}
