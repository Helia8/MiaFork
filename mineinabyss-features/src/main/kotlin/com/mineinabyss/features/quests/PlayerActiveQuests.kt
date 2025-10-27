package com.mineinabyss.features.quests

import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.serialization.getOrSetPersisting
import org.bukkit.entity.Player
import kotlin.collections.MutableSet

class PlayerActiveQuests {


    // we use this to store a player visited location and look it up;
    // will probably be moved to database implementation later on
    val visitedLocations: MutableSet<String> = mutableSetOf() // this stores LocationData.name


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