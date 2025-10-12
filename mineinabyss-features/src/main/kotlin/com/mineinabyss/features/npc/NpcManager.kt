package com.mineinabyss.features.npc

import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.world.ChunkLoadEvent

class NpcManager(
    val npcsConfig: NpcsConfig,
    val world: World,
) {
    var npcEntities: List<NpcEntity> = emptyList()
    val npcMap: MutableMap<Long, List<NpcEntity>> = mutableMapOf()

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
        npcMap[chunk.chunkKey]?.forEach(NpcEntity::createNpc)
    }

}