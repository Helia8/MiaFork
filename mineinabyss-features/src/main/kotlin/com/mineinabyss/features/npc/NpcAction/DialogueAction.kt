package com.mineinabyss.features.npc.NpcAction

import com.mineinabyss.features.abyss
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import org.aselstudios.luxdialoguesapi.Builders.Dialogue

@Serializable
class DialogueAction(
    val dialogues: List<String>,
    val dialogueCooldown: Long
) {

    fun displayAllDialogue(player: Player) {
        dialogues.forEachIndexed { index, dialogue ->
            player.server.scheduler.runTaskLater(
                abyss.plugin,
                Runnable { player.sendMessage(dialogue) },
                (index * dialogueCooldown)
            )
        }
    }

    fun displayNextDialogue(player: Player, currentDialogueIndex: Int) {
        if (currentDialogueIndex < dialogues.size) {
            player.sendMessage(dialogues[currentDialogueIndex])
        }
    }
}