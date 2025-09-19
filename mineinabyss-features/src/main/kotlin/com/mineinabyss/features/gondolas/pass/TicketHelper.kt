package com.mineinabyss.features.gondolas.pass

import com.mineinabyss.components.gondolas.Ticket
import com.mineinabyss.components.gondolas.UnlockedGondolas
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.serialization.getOrSetPersisting
import org.bukkit.entity.Player
import com.mineinabyss.idofront.messaging.success

fun Player.unlockRoute(ticket: Ticket) {
    val unlocked =  this.toGeary().getOrSetPersisting<UnlockedGondolas> { UnlockedGondolas() }
    for (gondolasId in ticket.gondolasInRoute) {
        unlocked.keys.add(gondolasId)
    }
    this.success("Unlocked route of: ${ticket.ticketName}")
}