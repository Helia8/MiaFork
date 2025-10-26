package com.mineinabyss.features.quests

import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.serialization.getOrSetPersisting
import org.bukkit.entity.Player

class PlayerActiveQuests {

    val activeQuests = mutableSetOf<String>() // Set of active quest IDs

    fun set(player: Player) {
        player.toGearyOrNull()?.set<PlayerActiveQuests>(this@PlayerActiveQuests)
    }

    fun addQuest(player: Player, questId: String) {
//        player.toGearyOrNull()?.getOrSetPersisting { activeQuests }
        player.toGearyOrNull()?.get<PlayerActiveQuests>()?.activeQuests?.add(questId)
    }

    fun removeQuest(player: Player, questId: String) {
        player.toGearyOrNull()?.get<PlayerActiveQuests>()?.activeQuests?.remove(questId)
    }
}