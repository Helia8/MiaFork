package com.mineinabyss.features.npc.shopkeeping

import org.bukkit.Chunk
import org.bukkit.plugin.Plugin

fun initNpcMap(config: NpcsConfig, plugin: Plugin): MutableMap<Long, List<NpcEntity>> {
    val map = mutableMapOf<Long, List<NpcEntity>>()

    println("config created ")
    for (npc in config.npcs) {
        val chunkKey = npc.location.chunk.chunkKey
        val npcEntity = NpcEntity(npc, npc.location.world, plugin)
        map[chunkKey] = map.getOrDefault(chunkKey, emptyList()) + npcEntity
    }
    return map
}