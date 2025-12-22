package com.mineinabyss.features.npc.shopkeeping

import com.mineinabyss.components.playerprofile.PlayerProfile
import com.mineinabyss.geary.papermc.toGeary
import com.mineinabyss.geary.papermc.tracking.items.ItemTracking
import com.mineinabyss.geary.prefabs.PrefabKey
import io.papermc.paper.datacomponent.DataComponentType
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit.getWorld
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

@Serializable
data class TradeEntry(
    val prefab: String,
    val amount: Int = 1,
//    val components: Map<String, String>? = null, // numerical only for now
)
//tradeTable:
//  id: basic_trades
//  trades:
//      - input:
//          prefab: minecraft:bee_nest
//          amount: 3
//          -components:
//              "minecraft:bees": "[{entity_data:{id:"bee",CustomName:"Maya"},min_ticks_in_hive:60,ticks_in_hive:0}]"
//      output:
//          prefab: mineinabyss:orth_coin
//          amount: 1

@Serializable
data class Trade(
    val input: TradeEntry,
    val output: TradeEntry,
)

@Serializable
class TradeTable(
    val id: String,
    val trades: List<Trade>,
) {
    fun createMerchantRecipes(): List<MerchantRecipe> {
        val mainWorld = getWorld("world") ?: error("World 'world' not found")
        val gearyItems = mainWorld.toGeary().getAddon(ItemTracking)
        val recipes = mutableListOf<MerchantRecipe>()
        lateinit var inputItem: ItemStack
        lateinit var outputItem: ItemStack
        for (trade in trades) {
            var namespace = PrefabKey.Companion.of(trade.input.prefab).namespace
            if (namespace == "minecraft") {
                inputItem = ItemStack(Material.matchMaterial(trade.input.prefab)?: error("Incorrect prefab key: ${trade.input.prefab}"))
            }
            else if (namespace == "mineinabyss") {
                inputItem = gearyItems.createItem(PrefabKey.Companion.of(trade.input.prefab)) ?: error("Incorrect prefab key: ${trade.input.prefab}")
            } else {
                error("Incorrect prefab key: ${trade.input.prefab}")
            }
            inputItem.amount = trade.input.amount
            namespace =  PrefabKey.Companion.of(trade.output.prefab).namespace
            if (namespace == "minecraft") {
                outputItem = ItemStack(Material.matchMaterial(trade.output.prefab)?: error("Incorrect prefab key: ${trade.output.prefab}"))
            }
            else if (namespace == "mineinabyss") {
                outputItem = gearyItems.createItem(PrefabKey.Companion.of(trade.output.prefab)) ?: error("Incorrect prefab key: ${trade.output.prefab}")
            } else {
                error("Incorrect prefab key: ${trade.output.prefab}")
            }
            outputItem.amount = trade.output.amount
//            for (components in trade.input.components?.entries ?: emptyList()) {
//                val key = components.key as DataComponentType.Valued<Int>
//                val value = components.value.toInt()
//                outputItem.setData(key, value as Int)
//
//            }
            val recipe = MerchantRecipe(outputItem, 99999)
            recipe.addIngredient(inputItem)

            recipes.add(recipe)
        }
        return recipes
    }
}
