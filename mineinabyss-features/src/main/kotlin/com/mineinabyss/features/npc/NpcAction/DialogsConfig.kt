package com.mineinabyss.features.npc.NpcAction

import com.mineinabyss.features.npc.Npc
import com.mineinabyss.features.quests.completeQuest
import com.mineinabyss.features.quests.unlockQuest
import com.mineinabyss.idofront.messaging.error
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.aselstudios.luxdialoguesapi.Builders.Answer
import org.aselstudios.luxdialoguesapi.Builders.Dialogue
import org.aselstudios.luxdialoguesapi.Builders.Page
import org.aselstudios.luxdialoguesapi.LuxDialoguesAPI
import org.bukkit.entity.Player

@Serializable
data class DialogueAction(
    val name: String,
    val questId: String? = null
) {
    fun customAction(player: Player) {
        player.sendMessage("Custom action executed!")
    }

    fun unlockQuestAction(player: Player) {
        if (questId == null) {
            player.error("Missing questId")
            return
        }
        unlockQuest(player, questId)
    }

    fun completeQuestAction(player: Player) {
        if (questId == null) {
            player.error("Missing questId")
            return
        }
        completeQuest(player, questId)
    }
    fun execute(player: Player, npc: Npc) {
        when (name) {
            "customAction" -> customAction(player)
            "gondolaAction" -> npc.gondolaUnlockerInteraction(player)
            "unlockQuestAction" -> unlockQuestAction(player)
            "completeQuestAction" -> completeQuestAction(player)
            else -> player.error("Error resolving action: $name")
        }
    }
}

@Serializable
class AnswerData(
    val text: String,
    @EncodeDefault(Mode.NEVER)
    val placeholderCondition: String? = null,
    @EncodeDefault(Mode.NEVER)
    val replyMessage: String? = null,
    @EncodeDefault(Mode.NEVER)
    val action: DialogueAction? = null
) {
    val npc = null
    fun build(npc: Npc): Answer? {
        val answer = Answer.Builder()
            .setAnswerText(text)
        if (placeholderCondition != null) answer.addCondition(placeholderCondition)
        if (replyMessage != null) answer.addReplyMessage(replyMessage)
        if (action != null) answer.addCallback { player -> action.execute(player, npc) }
        return answer.build()
    }
}

@Serializable
class PageData(
    val lines: List<String> = emptyList(),
    @EncodeDefault(Mode.NEVER)
    val preAction: String? = null,
    @EncodeDefault(Mode.NEVER)
    val postAction: String? = null,
) {

    fun build(): Page? {
        val page = Page.Builder()
        lines.forEach { line -> page.addLine(line) }
        if (preAction != null) page.addPreAction(preAction)
        if (postAction != null) page.addPostAction(postAction)
        return page.build()
    }
}

@Serializable
class DialogData(
    @EncodeDefault(Mode.NEVER) val typingSpeed: Int = 1,
    @EncodeDefault(Mode.NEVER) val typingSound: String = "luxdialogues:luxdialogues.sounds.typing",
    @EncodeDefault(Mode.NEVER) val selectionSound : String = "luxdialogues:luxdialogues.sounds.selection",
    @EncodeDefault(Mode.NEVER) val range: Double = 3.0,
    @EncodeDefault(Mode.NEVER) val effect: String = "Slowness",
    @EncodeDefault(Mode.NEVER) val answerNumbers: Boolean = true,
    @EncodeDefault(Mode.NEVER) val dialogueTextColor: String = "#4f4a3e",
    @EncodeDefault(Mode.NEVER) val backgroundFog: Boolean = true,
    @EncodeDefault(Mode.NEVER) val characterName : String = "default name",
    @EncodeDefault(Mode.NEVER) val characterNameColor: String = "#4f4a3e",
    @EncodeDefault(Mode.NEVER) val characterImage: String = "character-background",
    @EncodeDefault(Mode.NEVER) val nameStartImage: String = "name-start",
    @EncodeDefault(Mode.NEVER) val nameMidImage: String = "name-mid",
    @EncodeDefault(Mode.NEVER) val nameEndImage: String = "name-end",
    @EncodeDefault(Mode.NEVER) val answerBackgroundImage: String = "answer-background",
    @EncodeDefault(Mode.NEVER) val dialogueBackgroundImage: String = "dialogue-background",
    @EncodeDefault(Mode.NEVER) val dialogBackgroundImageColor: String = "#f8ffe0",
    @EncodeDefault(Mode.NEVER) val answerBackgroundImageColor: String = "#f8ffe0",
    @EncodeDefault(Mode.NEVER) val cursorIconImage: String = "hand",
    @EncodeDefault(Mode.NEVER) val answers: List<AnswerData> = emptyList(),
    @EncodeDefault(Mode.NEVER) val pages: List<PageData> = emptyList(),
) {

    fun build(id: String, npc: Npc): Dialogue? {
        val dialogue = Dialogue.Builder()
            .setDialogueID(id)
            .setDialogueSpeed(typingSpeed)
            .setTypingSound(typingSound)
            .setTypingSoundPitch(1.0)
            .setTypingSoundVolume(1.0)
            .setRange(range)
            .setSelectionSound(selectionSound)
            .setEffect(effect)
            .setAnswerNumbers(answerNumbers)
            .setArrowImage(cursorIconImage, "#4f4a3e", -7)
            .setDialogueBackgroundImage(dialogueBackgroundImage, dialogBackgroundImageColor, 0)
            .setAnswerBackgroundImage(answerBackgroundImage, answerBackgroundImageColor, 140)
            .setDialogueText(dialogueTextColor, 10)
            .setAnswerText(dialogueTextColor, 13, dialogueTextColor)
            .setCharacterImage(characterImage, -16)
            .setCharacterNameText(characterName, characterNameColor, 20)
            .setNameStartImage(nameStartImage)
            .setNameMidImage(nameMidImage)
            .setNameEndImage(nameEndImage)
            .setNameImageColor("#f8ffe0")

        if (backgroundFog) dialogue.setFogImage("fog", "#000000")

        answers.forEach { answerData -> dialogue.addAnswer(answerData.build(npc)) }
        pages.forEach { pageData -> dialogue.addPage(pageData.build()) }
        return dialogue.build()
    }

    fun startDialogue(player: Player, id: String, npc: Npc) {
        val dialog = this.build(id, npc) ?: return
        LuxDialoguesAPI.getProvider().sendDialogue(player, dialog)
    }
}

@Serializable
class DialogsConfig(
    val configs: Map<String, DialogData> = mapOf()
)