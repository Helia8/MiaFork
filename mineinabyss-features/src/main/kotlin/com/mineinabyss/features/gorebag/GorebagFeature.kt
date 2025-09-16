package com.mineinabyss.features.gorebag

import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.features.Configurable
import com.mineinabyss.idofront.features.FeatureDSL
import com.mineinabyss.idofront.features.FeatureWithContext
import com.mineinabyss.idofront.plugin.listeners
import org.bukkit.entity.Player

class GorebagFeature: FeatureWithContext<GorebagFeature.Context>(::Context)  {

    class Context : Configurable<Context> {
        override val configManager: IdofrontConfig<Context>
            get() = TODO("Not yet implemented")
        val gorebagListener = GorebagListener()
    }

    override fun FeatureDSL.enable() {
        plugin.listeners(context.gorebagListener)
        mainCommand {
            "gorebag"(desc = "Commands for gorebag") {
                permission = "mineinabyss.gorebag"
                "give"(desc = "Gives you a custom gorebag item") {
                    permission = "mineinabyss.gorebag.give"
                    playerAction {
                        val item = context.gorebagListener.createCustomItem()
                        player.inventory.addItem(item)
                        player.sendMessage("You received a Gorebag!")
                    }
                }
            }
        }
    }
}