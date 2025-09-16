package com.mineinabyss.features.gorebag

import net.kyori.adventure.text.Component
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import java.util.*
import org.bukkit.event.inventory.InventoryClickEvent


class GorebagListener : Listener {
    private val inventories = mutableMapOf<String, Pair<Int, Inventory>>() // id -> (page, inventory)
    private val key = NamespacedKey("mineinabyss", "gorebag")
    private val pageCount = 5
    private val pageSize = 27 // 3 rows for items

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val meta = item.itemMeta ?: return
        val id = meta.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return

        val page = inventories[id]?.first ?: 0
        val inventory = createPagedInventory(id, page)
        inventories[id] = page to inventory
        event.player.openInventory(inventory)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.inventory
        val id = inventory.getHolder()?.let { it as? GorebagHolder }?.id ?: return
        val slot = event.rawSlot
        if (slot >= pageSize && slot < pageSize + pageCount) {
            event.isCancelled = true
            val newPage = slot - pageSize
            inventories[id] = newPage to createPagedInventory(id, newPage)
            event.whoClicked.openInventory(inventories[id]!!.second)
        }
    }

    fun createCustomItem(): ItemStack {
        val item = ItemStack(Material.CHEST)
        val meta = item.itemMeta!!
        val id = UUID.randomUUID().toString()
        meta.persistentDataContainer.set(key, PersistentDataType.STRING, id)
        item.itemMeta = meta
        return item
    }

    private fun createPagedInventory(id: String, page: Int): Inventory {
        val holder = GorebagHolder(id)
        val inventory = Bukkit.createInventory(holder, pageSize + pageCount, Component.text("Gorebag Page ${page + 1}"))
        // Fill with items for the current page (implement your own item storage logic)
        // Fill last row with page switchers
        for (i in 0 until pageCount) {
            val item = ItemStack(if (i == page) Material.YELLOW_STAINED_GLASS_PANE else Material.LIME_STAINED_GLASS_PANE)
            val meta = item.itemMeta!!
            meta.displayName(Component.text("Page ${i + 1}"))
            item.itemMeta = meta
            inventory.setItem(pageSize + i, item)
        }
        return inventory
    }

    class GorebagHolder(val id: String) : InventoryHolder {
        override fun getInventory(): Inventory = id as Inventory
    }
}