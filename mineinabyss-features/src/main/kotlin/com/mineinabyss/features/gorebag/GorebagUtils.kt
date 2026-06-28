package com.mineinabyss.features.gorebag

import org.bukkit.entity.Player

class GorebagUtils {
    companion object {
        fun giveGorebag(player: Player) {
            player.inventory.addItem(Gorebag.createItem())

        }
    }
}