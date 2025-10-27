package com.mineinabyss.features.quests

import com.mineinabyss.features.abyss
import com.mineinabyss.idofront.features.Configurable
import com.mineinabyss.idofront.features.FeatureWithContext
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.features.FeatureDSL

class QuestFeature : FeatureWithContext<QuestFeature.Context>(::Context) {
    class Context: Configurable<QuestConfig> {
        override val configManager = config("quests", abyss.dataPath, QuestConfig())
        val questConfig by config("quests", abyss.dataPath, QuestConfig())
    }

    override fun FeatureDSL.enable() {
        QuestConfigHolder.config = context.questConfig
    }
}