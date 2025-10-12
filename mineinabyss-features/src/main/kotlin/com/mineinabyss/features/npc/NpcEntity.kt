package com.mineinabyss.features.npc

import com.mineinabyss.features.abyss
import com.mineinabyss.features.npc.shopkeeping.Trade
import com.mineinabyss.geary.papermc.toGeary
import com.mineinabyss.geary.papermc.tracking.items.ItemTracking
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.messaging.info
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Interaction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.plugin.Plugin

class NpcEntity(
    val config: Npc,
    val mainWorld: World,
) {


    fun createTypedNpc() {
        when (config.type) {
            "trader" -> createTraderNpc()
            "gondola_unlocker" -> createGondolaUnlockerNpc()
            "quest_giver" -> createQuestGiverNpc()
            "dialogue" -> createDialogueNpc()
            else -> throw IllegalArgumentException("Unknown type ${config.type}")
        }
    }










    fun createDialogueNpc() {

    }

    fun createQuestGiverNpc() {

    }

    fun createGondolaUnlockerNpc() {

    }

    fun createTraderNpc() {
        if (config.tradeTable.trades.isNotEmpty()) {

            val location = config.location
            val chunk = location.chunk

            for (e in chunk.entities) {
                if (config.id in e.scoreboardTags) {
                    // delete the old entity if it exists and respawn a newer version instead
                    e.remove()
                }
            }
            val entity = location.world.spawn(location, Interaction::class.java)

            val recipes = createMerchantRecipes(config.tradeTable.trades)
            val merchant = Bukkit.createMerchant()
            //entity.setItemStack(ItemStack(Material.WITHER_SKELETON_SKULL))
            //entity.setAI(false)
            merchant.recipes = recipes
//            entity.profession = org.bukkit.entity.Villager.Profession.LIBRARIAN
//            entity.recipes = recipes
            entity.customName = "a"
            entity.addScoreboardTag("custom trade ig")
            entity.addScoreboardTag(config.id)
            Bukkit.getPluginManager().registerEvents(object : Listener {
                @EventHandler
                fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
                    val player = event.player
                    player.info("bleh bleh bleh")
                    if (event.rightClicked == entity) {
                       player.info("bla bla bla")
                        event.isCancelled = true
                        val new_recipe = createMerchantRecipes(config.tradeTable.trades)
                        merchant.recipes = new_recipe

                        MenuType.MERCHANT.builder().merchant(merchant).build(player).open()
                    }
                }
            }, abyss.plugin)
        }

        return
    }

    fun createMerchantRecipes(trades: List<Trade>): List<MerchantRecipe> {
        val gearyItems = mainWorld.toGeary().getAddon(ItemTracking)
        val recipes = mutableListOf<MerchantRecipe>()
        for (trade in trades) {
            val inputItem: ItemStack = gearyItems.createItem(PrefabKey.Companion.of(trade.input.prefab)) ?: error("Incorrect prefab key: ${trade.input.prefab}")
            inputItem.amount = trade.input.amount
            val outputItem = gearyItems.createItem(PrefabKey.Companion.of(trade.output.prefab)) ?: error("Incorrect prefab key: ${trade.output.prefab}")
            outputItem.amount = trade.output.amount

            val recipe = MerchantRecipe(outputItem, 99999)
            recipe.addIngredient(inputItem)

            recipes.add(recipe)
        }
        return recipes
    }
}