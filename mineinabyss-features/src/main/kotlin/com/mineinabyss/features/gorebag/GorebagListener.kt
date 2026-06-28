package com.mineinabyss.features.gorebag
import com.mineinabyss.features.abyss
import com.mineinabyss.idofront.features.plugin
import com.mineinabyss.idofront.messaging.info
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.UUID

class GorebagListener(

): Listener {
    val uuid_to_item: HashMap<UUID, Gorebag> = hashMapOf()
    @EventHandler
    fun EntityPickupItemEvent.onPickup() {
        val player = entity
        if (player is Player && player.hasGorebag()) { // player has gorebag
            isCancelled = true
            // bundle store sound played at player
            player.playSound(player.location, Sound.ITEM_BUNDLE_INSERT, 1f, 1f)
            val stack = item.itemStack
            val overflowedItems = putInGorebag(stack, player, )
            if (overflowedItems.isEmpty()) {
                item.remove()
            } else {
                item.itemStack.amount = overflowedItems.values.sumOf { it.amount }
            }
        }
    }

    private fun Player.hasGorebag(): Boolean {
       for (item in inventory.contents) {
           if (item == null)
               continue
           val gorebag = Gorebag.from(item) ?: continue
           return true
       }
        return false
    }

    private fun putInGorebag(item: ItemStack, player: Player):  HashMap<Int, ItemStack> {
        // find first non full gorebag
        iterGorebag@for (bag in player.inventory.contents) {
            if (bag == null)
                continue
            val gorebag = Gorebag.from(bag) ?: continue
            val res = gorebag.addItem(item)
            for (it in res.values) {
                if (it == item) { // if nothing got added, means gorebag is full, try next one
                    continue@iterGorebag
                }
            }
            return res
        }
        return hashMapOf()
    }


    @EventHandler
    fun InventoryCloseEvent.onClose() {
        val gorebag = uuid_to_item[player.uniqueId] ?: return
        gorebag.saveInventory(inventory.contents)
        uuid_to_item.remove(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGHEST) // h
    fun InventoryClickEvent.onGorebagOpen() {
        val clicked = currentItem ?: return
        val player = whoClicked as? Player ?: return
        val gorebag = Gorebag.from(clicked) ?: return
        isCancelled = true
        Bukkit.getScheduler().runTask(abyss, Runnable {
            gorebag.openInventory(player)
        })
        uuid_to_item[player.uniqueId] = gorebag
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerInteractEvent.onGorebagOpen() {
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return
        if (hand != EquipmentSlot.HAND) return
        val item = player.inventory.itemInMainHand
        val gorebag = Gorebag.from(item) ?: return
        isCancelled = true
        gorebag.openInventory(player)
        uuid_to_item[player.uniqueId] = gorebag
    }

}