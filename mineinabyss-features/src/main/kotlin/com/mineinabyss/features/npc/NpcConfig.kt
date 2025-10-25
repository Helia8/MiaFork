package com.mineinabyss.features.npc

import com.mineinabyss.components.editPlayerData
import com.mineinabyss.components.gondolas.Ticket
import com.mineinabyss.features.gondolas.pass.TicketConfigHolder
import com.mineinabyss.features.gondolas.pass.isRouteUnlocked
import com.mineinabyss.features.gondolas.pass.unlockRoute
import com.mineinabyss.features.npc.NpcAction.DialogData
import com.mineinabyss.features.npc.NpcAction.DialogueAction
import com.mineinabyss.features.npc.shopkeeping.TradeTable
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.idofrontLogger
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.serialization.LocationSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType

@Serializable
data class Npc(
    val id: String,
    val displayName: String,
    val location: @Serializable(LocationSerializer::class) Location,
    val scale: List<Double>, // ??
    val bbModel: String, // unsure, actually I guess it would be serializable
    val tradeTable: TradeTable, // todo: remove
    val tradeTableId: String, // use id pulled from config instead to be more modular
    val type: String, // "trader", "gondola_unlocker", "quest_giver", "dialogue"
    val dialogId: String? = null,
    val ticket_id: String? = null,
    val questId : String? = null,
) {



    fun defaultInteraction(player: Player, dialogId: String, dialogData: DialogData) {
        when (type) {
            "trader" -> traderInteraction(player)
            "gondola_unlocker" -> dialogInteraction(player, dialogId, dialogData)
            "quest_giver" -> questGiverInteraction()
            "dialogue" -> dialogInteraction(player, dialogId, dialogData)
            else -> throw IllegalArgumentException("Unknown NPC type: $type")
        }
    }

    fun fallbackInteraction(player: Player) {
        when (type) {
            "trader" -> traderInteraction(player)
            "gondola_unlocker" -> gondolaUnlockerInteraction(player)
            "quest_giver" -> questGiverInteraction()
            "dialogue" -> player.sendMessage("dialog data missing")
            else -> throw IllegalArgumentException("Unknown NPC type: $type")
        }
    }

    fun traderInteraction(player: Player) {
        if (tradeTable.trades.isEmpty()) return
        val recipes = tradeTable.createMerchantRecipes() ?: return
        val merchant = Bukkit.createMerchant()
        merchant.recipes = recipes
        MenuType.MERCHANT.builder().merchant(merchant).build(player).open()
        return
    }

    fun dialogInteraction(player: Player, dialogId: String, dialogData: DialogData) {
        dialogData.startDialogue(player, dialogId, this)
    }


    fun gondolaUnlockerInteraction(player: Player) {
        // instead of printing messages in chat, we should open an error dialog instead
        ticket_id ?: return idofrontLogger.e{"Ticket id is null for gondola unlocker NPC $id"}
        val ticket: Ticket = TicketConfigHolder.config?.tickets?.get(ticket_id)
            ?: return idofrontLogger.e("Ticket with id $ticket_id not found")
        player.editPlayerData {
            when {
                player.isRouteUnlocked(ticket) -> player.error("You already own this ticket!")
                ticket.ticketPrice > orthCoinsHeld -> player.error("You do not have enough orth coins to purchase this ticket. (Price: ${ticket.ticketPrice}, You have: $orthCoinsHeld)")
                else -> {
                    orthCoinsHeld -= ticket.ticketPrice
                    player.unlockRoute(ticket)
                    player.success("Obtained the ${ticket.ticketName} ticket")
                }
            }
        }
    }
    fun questGiverInteraction() {
        // this one is a bit more finicky, tho I think i'll do something like:
        // give the player a "objective" component, which would be something like:
        // (quest_id | progress | max_progress | completion_action)
        // so most of them are straightforward, and completion_action would be like an event listener of a specific type
        // so I guess it would also be like another object like
        // (event_type | event_data), where type could be like "kill" and data would be like "mob", so when we register our quest listeners,
        // we would do something like :
        // onMobKill
        // killer = player
        // if (killer has objective component with event_type "kill" and event_data == mob)
        // objective.progress++
        // same for every quest type we support
        // then we could also have a "success" function, the listener would invoke if objective.progress >= objective.max_progress
        // and it would go like, player.objective.displaysuccessmessage or some variation of that
    }

}

@Serializable
class NpcsConfig(
    val npcs: Map<String, Npc> = mapOf()
)
