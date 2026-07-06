package com.mineinabyss.features.goals.goalListener

import com.mineinabyss.features.goals.FactKind
import com.mineinabyss.features.goals.repository.GoalRepository
import com.mineinabyss.idofront.plugin.Services
import com.mineinabyss.geary.papermc.spawning.locations.PlayerEnterRegionEvent
import com.mineinabyss.geary.papermc.spawning.locations.RegionService
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class GoalListener(
    private val repository: GoalRepository,
) : Listener {
    @EventHandler
    suspend fun PlayerJoinEvent.onJoin() {
        repository.loadPlayer(player)
        val regions = Services.getOrNull<RegionService>()?.regionsAt(player.location) ?: return
        repository.seedRegions(player, regions)
    }

    @EventHandler
    fun PlayerQuitEvent.onQuit() = repository.unloadPlayer(player)

    @EventHandler
    fun PlayerEnterRegionEvent.onRegionEntered() =
        repository.recordFact(player, FactKind.REGION_ENTER, regionId)

    @EventHandler(ignoreCancelled = true)
    fun CraftItemEvent.onCraft() {
        val player = whoClicked as? Player ?: return
        repository.recordFact(player, FactKind.CRAFT, recipe.result.type.key.asString()) // todo: geary support
    }

    @EventHandler
    fun EntityDeathEvent.onKill() {
        val killer = entity.killer ?: return
        //todo: mm support
        repository.recordFact(killer, FactKind.KILL, entity.type.key.asString())
    }
}
