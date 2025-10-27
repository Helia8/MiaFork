package com.mineinabyss.features.quests

import com.mineinabyss.geary.papermc.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.papermc.tracking.items.ItemTracking
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun unlockQuest(player: Player, questId: String) {
    player.toGearyOrNull()?.get<PlayerActiveQuests>()?.activeQuests?.add(questId)
}

fun completeQuest(player: Player, questId: String) {
    val config = QuestConfigHolder.config ?: error("Trying to complete quest $questId but QuestConfig is not initialized")
    player.toGearyOrNull()?.get<PlayerActiveQuests>()?.activeQuests?.remove(questId)
    val gearyRewards = config.visitQuests[questId]?.gearyRewards
    if (gearyRewards != null) {
        for ((item, amount) in gearyRewards) {
            val gearyItems = player.world.toGeary().getAddon(ItemTracking)
            val gearyItem = gearyItems.createItem(item) ?: error("Failed to complete quest $questId: Geary prefab $item not found")
            gearyItem.amount = amount.coerceIn(1, gearyItem.maxStackSize)
            player.inventory.addItem(gearyItem)
        }
    }
    val vanillaRewards = config.visitQuests[questId]?.vanillaRewards ?: return
    for ((itemName, amount) in vanillaRewards) {
        val material = Material.matchMaterial(itemName) ?: error("Failed to complete quest $questId: Material $itemName not found")
        val itemStack = ItemStack(material)
        itemStack.amount = amount.coerceIn(1, itemStack.maxStackSize)
        player.inventory.addItem(itemStack)
    }
}

fun isVisitQuestCompleted(questId: String, config: QuestConfig, playerActiveQuests: PlayerActiveQuests): Boolean {
    val visitQuest = config.visitQuests[questId] ?: return false
    return visitQuest.locations.all { locationData ->
        locationData.name in playerActiveQuests.visitedLocations
    }

}

fun isFetchQuestCompleted(questId: String, config: QuestConfig, playerActiveQuests: PlayerActiveQuests): Boolean {
    // Placeholder implementation
    return false
}

fun isKillQuestCompleted(questId: String, config: QuestConfig, playerActiveQuests: PlayerActiveQuests): Boolean {
    // Placeholder implementation
    return false
}

fun isQuestCompleted(player: Player, questId: String): Boolean {
    val config = QuestConfigHolder.config ?: error("Trying to check completion of quest $questId but QuestConfig is not initialized")
    val playerActiveQuests = player.toGearyOrNull()?.get<PlayerActiveQuests>() ?: return false
    val activeQuests = playerActiveQuests.activeQuests
    if (questId !in activeQuests) return false
    return isVisitQuestCompleted(questId, config, playerActiveQuests) || isKillQuestCompleted(questId, config, playerActiveQuests) || isFetchQuestCompleted(questId, config, playerActiveQuests)


}