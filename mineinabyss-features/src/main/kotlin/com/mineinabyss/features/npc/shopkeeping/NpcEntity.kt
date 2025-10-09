package com.mineinabyss.features.npc.shopkeeping

import com.mineinabyss.chatty.helpers.globalChannel
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.papermc.gearyPaper
import com.mineinabyss.geary.papermc.toGeary
import com.mineinabyss.geary.papermc.tracking.items.*
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.messaging.info
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.plugin.Plugin
import kotlin.getValue

class NpcEntity(
    val config: Npc,
    val mainWorld: World,
    val plugin: Plugin,
) {

    fun createNpc() {
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

                        player.openMerchant(merchant, true)
                    }
                }
            }, plugin)
        }

        return
    }

    fun createMerchantRecipes(trades: List<Trade>): List<MerchantRecipe> {
        val gearyItems = mainWorld.toGeary().getAddon(ItemTracking)
        val recipes = mutableListOf<MerchantRecipe>()
        for (trade in trades) {
            val inputItem: ItemStack = gearyItems.createItem(PrefabKey.of(trade.input.prefab)) ?: error("Incorrect prefab key: ${trade.input.prefab}")
            inputItem.amount = trade.input.amount
            val outputItem = gearyItems.createItem(PrefabKey.of(trade.output.prefab)) ?: error("Incorrect prefab key: ${trade.output.prefab}")
            outputItem.amount = trade.output.amount

            val recipe = MerchantRecipe(outputItem, 99999)
            recipe.addIngredient(inputItem)

            recipes.add(recipe)
        }
        return recipes
    }
}