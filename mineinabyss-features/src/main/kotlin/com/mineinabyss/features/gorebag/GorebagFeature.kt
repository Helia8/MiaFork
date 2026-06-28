package com.mineinabyss.features.gorebag

import com.mineinabyss.dependencies.*

import com.mineinabyss.dependencies.module
import com.mineinabyss.features.AbyssFeatureConfig
import com.mineinabyss.features.abyss
import com.mineinabyss.idofront.features.listeners
import com.mineinabyss.idofront.features.mainCommand
import com.mineinabyss.idofront.messaging.info
import org.bukkit.entity.Player

val GorebagFeature = module("gorebag") {
    //require(get<AbyssFeatureConfig>().gorebag.enabled) { "Gorebag feature is disabled" }
    listeners(new(::GorebagListener))
}.mainCommand {
    "gorebag" {
        requires { sender.isOp }
        "info" {
            executes {
                sender.info("The feature is enabled!! (duh)")
            }
        }
        requires { sender.isOp }
        "isDisabled" {
            executes {
                sender.info("No, obviously...")
            }
        }
        requires { sender.isOp }
        "give" {
            executes {
                (sender as? Player)?.let { (GorebagUtils::giveGorebag)(it) } ?:  return@executes // silent fail ig (don't think this can ever happen though)
            }
        }

    }
}