package com.mineinabyss.components.gondolas

import com.mineinabyss.idofront.serialization.LocationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location


@Serializable
@SerialName("mineinabyss:warp_pair")
class WarpPair(
    @Serializable(with = LocationSerializer::class)
    val locA: Location,
    @Serializable(with = LocationSerializer::class)
    val locB: Location,
    val warpCooldown: Int,
    val warpZoneRadius : Double,
    val warpSound: String,
    val warpTicket: String,
)