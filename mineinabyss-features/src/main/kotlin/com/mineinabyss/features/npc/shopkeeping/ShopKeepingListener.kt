package com.mineinabyss.features.npc.shopkeeping

import com.mineinabyss.components.npc.shopkeeping.ShopKeeper
import com.mineinabyss.features.npc.shopkeeping.menu.ShopMainMenu
import com.mineinabyss.features.tutorial.TutorialEntity
import com.mineinabyss.features.tutorial.tutorial
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.guiy.inventory.guiy
import org.bukkit.Chunk
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.plugin.Plugin

class ShopKeepingListener(val config: NpcsConfig, val plugin: Plugin) : Listener {

    val map = initNpcMap(config, plugin)
    @EventHandler
    fun PlayerInteractEntityEvent.onInteractShopKeeper() {
        val shopkeeper = rightClicked.toGearyOrNull()?.get<ShopKeeper>() ?: return
        guiy {
            ShopMainMenu(player, shopkeeper)
            player.playSound(player, Sound.ENTITY_VILLAGER_TRADE, 1f, 1f)
        }
    }
    @EventHandler
    fun ChunkLoadEvent.onChunkLoad() {
        // for every entities in the hashmap at chunk index, load them as well as their configs and what not
        // (create npc call most likely)
        map[chunk.chunkKey]?.forEach(NpcEntity::createNpc) ?: return
//        for (npc in map[chunk] ?: return) {
//            //create entity
//        }
    }

}
