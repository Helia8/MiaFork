package com.mineinabyss.features.goals

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class FactKind {
    REGION_ENTER,
    CRAFT,
    KILL,
}

@Serializable
data class ConditionProgress(
    val values: Set<String> = emptySet(),
    val count: Int = 0,
)

@Serializable
sealed interface GoalCondition {
    fun tracks(kind: FactKind, value: String): Boolean

    fun advance(current: ConditionProgress, value: String): ConditionProgress

    fun isMet(progress: ConditionProgress): Boolean

    fun describe(progress: ConditionProgress): String
}

@Serializable
@SerialName("visit")
data class VisitCondition(
    val regions: List<String>,
) : GoalCondition {
    override fun tracks(kind: FactKind, value: String) = kind == FactKind.REGION_ENTER && value in regions
    override fun advance(current: ConditionProgress, value: String) = current.copy(values = current.values + value)
    override fun isMet(progress: ConditionProgress) = progress.values.containsAll(regions)
    override fun describe(progress: ConditionProgress) =
        "Visited ${progress.values.size}/${regions.size}: ${regions.joinToString { if (it in progress.values) "✔$it" else it }}"
}

@Serializable
@SerialName("craft")
data class CraftCondition(
    val items: List<String>,
) : GoalCondition {
    override fun tracks(kind: FactKind, value: String) = kind == FactKind.CRAFT && value in items
    override fun advance(current: ConditionProgress, value: String) = current.copy(values = current.values + value)
    override fun isMet(progress: ConditionProgress) = progress.values.containsAll(items)
    override fun describe(progress: ConditionProgress) =
        "Crafted ${progress.values.size}/${items.size}: ${items.joinToString { if (it in progress.values) "✔$it" else it }}"
}

@Serializable
@SerialName("kill")
data class KillCondition(
    val mob: String,
    val amount: Int = 1,
) : GoalCondition {
    override fun tracks(kind: FactKind, value: String) = kind == FactKind.KILL && value == mob
    override fun advance(current: ConditionProgress, value: String) = current.copy(count = current.count + 1)
    override fun isMet(progress: ConditionProgress) = progress.count >= amount
    override fun describe(progress: ConditionProgress) =
        "Killed ${progress.count.coerceAtMost(amount)}/$amount $mob"
}
