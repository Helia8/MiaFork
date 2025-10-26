package com.mineinabyss.features.quests

import com.mineinabyss.geary.papermc.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.papermc.tracking.items.ItemTracking
import com.mineinabyss.geary.prefabs.PrefabKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun unlockQuest(player: Player, questId: String) {
    player.toGearyOrNull()?.get<PlayerActiveQuests>()?.activeQuests?.add(questId)
}

fun completeQuest(player: Player, questId: String, config: QuestConfig) {
    player.toGearyOrNull()?.get<PlayerActiveQuests>()?.activeQuests?.remove(questId)
    val gearyRewards = config.visitQuests[questId]?.gearyRewards ?: return
    for ((item, amount) in gearyRewards) {
        val gearyItems =  player.world.toGeary().getAddon(ItemTracking)
        val gearyItem = gearyItems.createItem(item) ?: error("welp $item doesn't exist")
        gearyItem.amount = amount.coerceIn(1, gearyItem.maxStackSize)
        player.inventory.addItem(gearyItem)
    }

}