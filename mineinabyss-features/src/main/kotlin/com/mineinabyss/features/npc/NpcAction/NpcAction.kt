package com.mineinabyss.features.npc.NpcAction

class NpcAction(
    val type: String,
) {


    init {
        when (type) {
            "trade" -> TradeAction()
            "gondola" -> GondolaAction()
            else -> throw IllegalArgumentException("Unknown action type: $type")
        }
    }
}