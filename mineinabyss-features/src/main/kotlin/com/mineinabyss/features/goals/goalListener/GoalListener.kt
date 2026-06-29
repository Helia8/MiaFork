package com.mineinabyss.features.goals.goalListener

import com.mineinabyss.features.goals.Goal
import com.mineinabyss.features.goals.repository.GoalRepository
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class GoalListener(val goals: List<Goal>, val repository: GoalRepository): Listener {


    @EventHandler
    fun PlayerMoveEvent.onLocationEntered() {
        if (!hasExplicitlyChangedBlock()) return

        // List of regions at the player location
        val regions = Regions.getAllIn(player.location)
        // get every goal that requires a location to be completed
        goals.filter { it.hasLocation() }.forEach { goal ->
            // try to add the region(s) to the goal progress (or do nothing if the goal isn't active for the player)
           repository.tryUpdateGoal(player, goal, regions)
       }
    }

}