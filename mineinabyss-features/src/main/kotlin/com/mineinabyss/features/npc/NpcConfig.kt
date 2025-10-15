package com.mineinabyss.features.npc

import com.mineinabyss.features.npc.NpcAction.DialogData
import com.mineinabyss.features.npc.NpcAction.DialogueAction
import com.mineinabyss.features.npc.shopkeeping.TradeTable
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
) {



    fun defaultInteraction(player: Player, dialogId: String, dialogData: DialogData) {
        when (type) {
            "trader" -> traderInteraction(player)
            "gondola_unlocker" -> gondolaUnlockerInteraction()
            "quest_giver" -> questGiverInteraction()
            "dialogue" -> dialogInteraction(player, dialogId, dialogData)
            else -> throw IllegalArgumentException("Unknown NPC type: $type")
        }
    }

    fun fallbackInteraction(player: Player) {
        when (type) {
            "trader" -> traderInteraction(player)
            "gondola_unlocker" -> gondolaUnlockerInteraction()
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
        dialogData.startDialogue(player, dialogId)
    }


    fun gondolaUnlockerInteraction() {
        // just a matter of gondola.unlockroute(ticket_id) most likely

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
