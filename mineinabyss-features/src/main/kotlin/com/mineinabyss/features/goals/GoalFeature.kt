package com.mineinabyss.features.goals

import com.mineinabyss.dependencies.get
import com.mineinabyss.dependencies.module
import com.mineinabyss.dependencies.new
import com.mineinabyss.features.AbyssFeatureConfig
import com.mineinabyss.features.achievements.data.GoalProgress
import com.mineinabyss.features.achievements.data.AchievementStore
import com.mineinabyss.features.achievements.data.AdvancementSyncListener
import com.mineinabyss.features.goals.dataStore.GoalStore
import com.mineinabyss.features.goals.goalListener.GoalListener
import com.mineinabyss.idofront.Idofront
import com.mineinabyss.idofront.commands.brigadier.Args
import com.mineinabyss.idofront.datastore.launchWrite
import com.mineinabyss.idofront.datastore.readBlocking
import com.mineinabyss.idofront.datastore.setupDataStore
import com.mineinabyss.idofront.features.listeners
import com.mineinabyss.idofront.features.mainCommand



val GoalFeature = module("goals") {
    // require goal feature!!
    require(get<AbyssFeatureConfig>().goals.enabled) { "Goals feature is disabled" }
    listeners(new(::GoalListener))
    Idofront.setupDataStore(GoalStore)




}.mainCommand {
    //TODO: change
    "goals" {
        "unlock" {
            // Unlock every criterion of a goal and re-evaluate the goal
            executes.asPlayer().args("name" to Args.string()) { name ->
                player.launchWrite { AchievementStore[player, name] = GoalProgress(completed = true) }
            }
        }
        "status" {
            // Evaluate each criterion of the goal and notify the player of the progress
            executes.asPlayer().args("name" to Args.string()) { name ->
                val progress = player.readBlocking { AchievementStore[player, name] }
                player.sendMessage("Achievement progress: $progress")
            }
        }
        "evaluate" {
           // Evaluate a single criterion and notify the player of it's status
        }
    }
}
