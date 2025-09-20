package com.mineinabyss.features.npc.shopkeeping

import com.mineinabyss.components.npc.shopkeeping.ShopKeeper
import com.mineinabyss.features.npc.shopkeeping.menu.ShopMainMenu
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.guiy.canvas.guiy
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class ShopKeepingListener : Listener {
    @EventHandler
    fun PlayerInteractEntityEvent.onInteractShopKeeper() {
        val shopkeeper = rightClicked.toGearyOrNull()?.get<ShopKeeper>() ?: return
        guiy(player) {
            ShopMainMenu(player, shopkeeper)
            player.playSound(player, Sound.ENTITY_VILLAGER_TRADE, 1f, 1f)
        }
    }
}
