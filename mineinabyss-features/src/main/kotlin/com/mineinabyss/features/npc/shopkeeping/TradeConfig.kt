package com.mineinabyss.features.npc.shopkeeping

import kotlinx.serialization.Serializable

@Serializable
data class TradeEntry(
    val prefab: String,
    val amount: Int = 1,
)

@Serializable
data class Trade(
    val input: TradeEntry,
    val output: TradeEntry,
)

@Serializable
class TradeTable(
    val id: String,
    val trades: List<Trade>,
)
