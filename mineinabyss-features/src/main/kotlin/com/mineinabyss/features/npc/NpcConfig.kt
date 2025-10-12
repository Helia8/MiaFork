package com.mineinabyss.features.npc

import com.mineinabyss.features.abyss
import com.mineinabyss.features.npc.shopkeeping.Trade
import com.mineinabyss.features.npc.shopkeeping.TradeTable
import com.mineinabyss.geary.papermc.toGeary
import com.mineinabyss.geary.papermc.tracking.items.ItemTracking
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.serialization.LocationSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.entity.Interaction
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.bukkit.inventory.MerchantRecipe

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
    val dialog_id: String? = null,
) {

    fun FallbackInteraction(player: Player) {
        when (type) {
            "trader" -> traderInteraction(player)
            "gondola_unlocker" -> gondolaUnlockerInteraction()
            "quest_giver" -> questGiverInteraction()
            "dialogue" -> player.sendMessage("This NPC has no dialogue configured.")
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



    fun gondolaUnlockerInteraction() {

    }
    fun questGiverInteraction() {

    }

}

@Serializable
class NpcsConfig(
    val npcs: Map<String, Npc>
)
