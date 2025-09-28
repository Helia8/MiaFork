package com.mineinabyss.features.gondolas

import com.mineinabyss.components.gondolas.Gondola
import com.mineinabyss.components.gondolas.WarpPair
import kotlinx.serialization.Serializable

@Serializable
data class GondolasConfig(
    val gondolas: Map<String, WarpPair> = mapOf()
) {
    init {
        LoadedGondolas.loaded.clear()
        LoadedGondolas.loaded.putAll(gondolas.mapKeys { it.key.lowercase() })

    }
}