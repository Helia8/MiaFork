package com.mineinabyss.features.gorebag

import com.mineinabyss.features.abyss
import com.mineinabyss.idofront.features.plugin
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit.createInventory
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.util.HashMap
import java.util.UUID
import kotlin.arrayOfNulls

val GOREBAG_CONTENTS_KEY = NamespacedKey(abyss, "backpack_contents")

class Gorebag private constructor(val itemStack: ItemStack, val contents  : Array<ItemStack?>) {
    companion object {


        private fun deserialize(bytes: ByteArray): Array<ItemStack?> {
            val input = BukkitObjectInputStream(ByteArrayInputStream(bytes))
            val contents = arrayOfNulls<ItemStack>(27)
            for (i in 0 until input.readInt()) {
                contents[i] = input.readObject() as? ItemStack
            }
            return contents
        }

        private fun serialize(contents: Array<ItemStack?>): ByteArray {
            val output = java.io.ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(output)
            dataOutput.writeInt(contents.size)
            for (item in contents) {
                dataOutput.writeObject(item)
            }
            dataOutput.close()
            return output.toByteArray()
        }

        fun from(itemStack: ItemStack): Gorebag? {
            val meta = itemStack.itemMeta ?: return null
            val bytes = meta.persistentDataContainer.get(GOREBAG_CONTENTS_KEY, PersistentDataType.BYTE_ARRAY) ?: return null
            val contents = try {
                deserialize(bytes)
            } catch (e: Exception) {
                abyss.logger.w("Failed to deserialize gorebag contents for item: ${e.message}")
                return null
            }
            return Gorebag(itemStack, contents)
        }


        fun createItem(): ItemStack {
            val item = ItemStack(Material.ENCHANTED_BOOK)
            val meta = item.itemMeta
            meta?.setDisplayName("Gorebag")
            meta?.persistentDataContainer?.set(GOREBAG_CONTENTS_KEY, PersistentDataType.BYTE_ARRAY, serialize(arrayOfNulls(27)))
            item.itemMeta = meta
            return item
        }
    }



    fun openInventory(player: Player) {
        val inv = createInventory(player, InventoryType.CHEST, "Gorebag")
        inv.contents = contents
        player.openInventory(inv)
    }

    fun saveInventory(inv: Array<ItemStack?>) {
        val meta = itemStack.itemMeta ?: return
        val items = serialize(inv)
        meta.persistentDataContainer.set(GOREBAG_CONTENTS_KEY, PersistentDataType.BYTE_ARRAY, items)
        itemStack.itemMeta = meta
    }

    fun addItem(item: ItemStack): HashMap<Int, ItemStack> {
        val inv = createInventory(null, InventoryType.CHEST, "Gorebag")
        inv.contents = contents
        val res = inv.addItem(item)
        saveInventory(inv.contents)
        return res
    }
}