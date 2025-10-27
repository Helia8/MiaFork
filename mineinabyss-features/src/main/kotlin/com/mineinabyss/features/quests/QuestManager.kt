package com.mineinabyss.features.quests

import com.mineinabyss.geary.papermc.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.papermc.tracking.items.ItemTracking
import com.mineinabyss.geary.serialization.getOrSetPersisting
import com.mineinabyss.geary.serialization.setPersisting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

@Serializable
@SerialName("mineinabyss:quest_data")
data class QuestData(
    val visitedLocations: Set<String> = emptySet(),
    val completedQuests: Set<String> = emptySet(),
    val activeQuests: Set<String> = emptySet()
)


object QuestManager {


    // we use this to store a player visited location and look it up;
    // will probably be moved to database implementation later on
    private fun getQuestData(player: Player): QuestData {
        return player.toGeary().getOrSetPersisting<QuestData> { QuestData() }
    }


    private fun update(player: Player, update: (QuestData) -> QuestData) {
        val data = getQuestData(player)
        val newData = update(data)
        player.toGeary().setPersisting(newData)
    }

    // hopefully this auto save the changes on the player
    fun addQuest(player: Player, questId: String) {
        update(player) { data -> data.copy(activeQuests = data.activeQuests + questId) }
    }

    fun completeQuest(player: Player, questId: String) {
        update(player) { data ->
            data.copy(
                activeQuests = data.activeQuests - questId,
                completedQuests = data.completedQuests + questId
            )
        }

        // give quest reward to player
        val config = QuestConfigHolder.config ?: error("Trying to complete quest $questId but QuestConfig is not initialized")
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


}