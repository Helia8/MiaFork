package com.mineinabyss.features.guilds.menus

import androidx.compose.runtime.Composable
import com.mineinabyss.features.guilds.extensions.deleteGuild
import com.mineinabyss.features.helpers.Text
import com.mineinabyss.features.helpers.ui.composables.Button
import com.mineinabyss.guiy.components.Spacer
import com.mineinabyss.guiy.components.canvases.Chest
import com.mineinabyss.guiy.layout.Row
import com.mineinabyss.guiy.modifiers.Modifier
import com.mineinabyss.guiy.modifiers.height
import com.mineinabyss.guiy.modifiers.placement.absolute.at
import com.mineinabyss.guiy.modifiers.size
import com.mineinabyss.guiy.navigation.LocalBackGestureDispatcher
import com.mineinabyss.idofront.textcomponents.miniMsg

@Composable
fun GuildUIScope.GuildDisbandScreen() = Chest(":space_-8::guild_disband_or_leave_menu:", Modifier.height(5)) {
    Row(Modifier.at(1, 1)) {
        ConfirmButton()
        Spacer(width = 1)
        CancelButton()
    }
}

@Composable
fun GuildUIScope.ConfirmButton(modifier: Modifier = Modifier) = Button(
    modifier,
    onClick = {
        player.deleteGuild()
        player.closeInventory()
    }) {
    Text("<green><b>Confirm Guild Disbanding".miniMsg(), modifier = Modifier.size(3, 3))
}

@Composable
fun CancelButton(modifier: Modifier = Modifier) {
    val dispatcher = LocalBackGestureDispatcher.current
    Button(
        modifier,
        onClick = { dispatcher.onBack() }
    ) {
        Text("<red><b>Cancel Guild Disbanding".miniMsg(), modifier = Modifier.size(3, 3))
    }
}
