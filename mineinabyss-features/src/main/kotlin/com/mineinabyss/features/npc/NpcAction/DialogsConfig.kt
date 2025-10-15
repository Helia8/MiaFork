package com.mineinabyss.features.npc.NpcAction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AnswerData(
    val text: String,
    val placeholderCondition: String? = null,
    val replyMessage: String? = null,
    val nextDialogueId: String? = null,
    val action: DialogueAction? = null
)

@Serializable
class PageData(
    val lines: List<String> = emptyList(),
    val preAction: String? = null,
    val postAction: String? = null,
)

@Serializable
class DialogsConfig(
    val typingSpeed: Int = 1,
    val typingSound: String = "luxdialogues:luxdialogues.sounds.typing",
    val selectionSound : String = "luxdialogues:luxdialogues.sounds.selection",
    val range: Double = 3.0,
    val effect: String = "Slowness",
    val answerNumbers: Boolean = true,
    val dialogueTextColor: String = "#4f4a3e",
    val backgroundFog: Boolean = true,
    val characterName : String = "default name",
    val characterNameColor: String = "#4f4a3e",
    val characterImage: String = "character-background",
    val nameStartImage: String = "name-start",
    val nameMidImage: String = "name-mid",
    val nameEndImage: String = "name-end",
    val answerBackgroundImage: String = "answer-background",
    val dialogueBackgroundImage: String = "dialogue-background",
    val dialogBackgroundImageColor: String = "#f8ffe0",
    val answerBackgroundImageColor: String = "#f8ffe0",
    val cursorIconImage: String = "hand",
    val answers: List<AnswerData> = emptyList(),
    val pages: List<PageData> = emptyList(),
) {
}