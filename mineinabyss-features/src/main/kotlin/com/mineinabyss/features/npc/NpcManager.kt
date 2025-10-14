package com.mineinabyss.features.npc

import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.world.ChunkLoadEvent
import org.aselstudios.luxdialoguesapi.LuxDialoguesAPI
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEntityEvent


// how to use:
// load npcs config from config
// load list of all dialogs ids
// create NpcManager with npcs config, world, and dialogs ids
// init it
// register listeners
class NpcManager(
    val npcsConfig: NpcsConfig,
    val world: World,
    val dialogsIds: List<String>
): Listener {
    // probably not needed
    var npcEntities: List<NpcEntity> = emptyList()
    val npcMap: MutableMap<Long, List<NpcEntity>> = mutableMapOf()

//    val api = LuxDialoguesAPI.getAPI().getProvider()
    fun initNpc() {
        // load npc config
        for (npc in npcsConfig.npcs.values) {
            val npcEntity = NpcEntity(npc, world)
            npcEntities = npcEntities + npcEntity

            val chunkKey = npc.location.chunk.chunkKey
            npcMap[chunkKey] = npcMap.getOrDefault(chunkKey, emptyList()) + npcEntity
        }
    }

    @EventHandler
    fun ChunkLoadEvent.handleNpcSpawn() {
        //spawn npc
        npcMap[chunk.chunkKey]?.forEach(NpcEntity::createBaseNpc)
    }


    fun PlayerInteractEntityEvent.handleNpcInteraction() {
        val player = this.player
        val entity = this.rightClicked.toGearyOrNull() ?: return
        val NpcData = entity.get<Npc>() ?: return
        val dialogId: String? = NpcData.dialog_id
        // execute /ld send player dialogId
        // TODO: use api once I get doc
        if (dialogId != null && dialogId in dialogsIds) {
            val command = "ld send ${player.name} $dialogId"
            player.server.dispatchCommand(player.server.consoleSender, command)
        } else {
            NpcData.FallbackInteraction(player)
        }
    }

}


