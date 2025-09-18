package com.mineinabyss.features.guilds.menus

import androidx.compose.runtime.Composable
import com.mineinabyss.features.guilds.extensions.leaveGuild
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
fun GuildUIScope.GuildLeaveScreen() = Chest(":space_-8::guild_disband_or_leave_menu:", Modifier.height(5)) {
    Row(Modifier.at(1, 1)) {
        LeaveButton()
        Spacer(width = 1)
        DontLeaveButton()
    }
}

@Composable
fun GuildUIScope.LeaveButton(modifier: Modifier = Modifier) = Button(
    modifier,
    onClick = {
        player.leaveGuild()
        player.closeInventory()
    }) {
    Text(
        "<green><b>Leave <dark_green><i>${guildName}".miniMsg(),
        modifier = Modifier.size(3, 3)
    )
}

@Composable
fun GuildUIScope.DontLeaveButton(modifier: Modifier = Modifier) {
    val dispatcher = LocalBackGestureDispatcher.current
    Button(
        modifier,
        onClick = { dispatcher?.onBack() }
    ) {
        Text(
            "<red><b>Don't Leave <dark_red><i>${guildName}".miniMsg(),
            modifier = Modifier.size(3, 3)
        )
    }
}
