package com.mineinabyss.mineinabyss.core

import com.derongan.minecraft.deeperworld.world.section.Section
import com.derongan.minecraft.deeperworld.world.section.section
import com.mineinabyss.components.layer.Layer
import org.bukkit.Location
import org.bukkit.World


val Location.layer: Layer?
    get() {
        return AbyssWorldManager.getLayerForSection(this.section ?: return null)
    }

val Section.layer: Layer?
    get() = AbyssWorldManager.getLayerForSection(this)

val World.isAbyssWorld: Boolean get() = AbyssWorldManager.isAbyssWorld(this)
