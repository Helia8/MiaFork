package com.mineinabyss.features.orthbanking.ui

import androidx.compose.runtime.Composable
import com.mineinabyss.components.PlayerData
import com.mineinabyss.components.playerDataOrNull
import com.mineinabyss.features.helpers.Text
import com.mineinabyss.features.helpers.ui.composables.Button
import com.mineinabyss.guiy.components.canvases.Chest
import com.mineinabyss.guiy.modifiers.Modifier
import com.mineinabyss.guiy.modifiers.click.clickable
import com.mineinabyss.guiy.modifiers.height
import com.mineinabyss.guiy.modifiers.placement.absolute.at
import com.mineinabyss.guiy.modifiers.size
import com.mineinabyss.guiy.navigation.NavHost
import com.mineinabyss.guiy.navigation.composable
import com.mineinabyss.guiy.navigation.rememberNavController
import com.mineinabyss.idofront.textcomponents.miniMsg
import org.bukkit.entity.Player

sealed class BankScreen {
    data object Default : BankScreen()
    data object Deposit : BankScreen()
    data object Widthdraw : BankScreen()
}

@Composable
fun BankMenu(player: Player) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = BankScreen.Default) {
        composable<BankScreen.Default> {
            Chest(":space_-8::orthbanking_menu:", Modifier.height(4)) {
                val data = player.playerDataOrNull ?: PlayerData() // careful not to modify directly here
                DepositCurrencyOption(data, Modifier.at(1, 1).clickable {
                    nav.navigate(BankScreen.Deposit)
                })
                WithdrawCurrencyOption(data, Modifier.at(5, 1).clickable {
                    nav.navigate(BankScreen.Widthdraw)
                })
            }
        }
        composable<BankScreen.Deposit> { DepositScreen(player) }
        composable<BankScreen.Widthdraw> { WithdrawScreen(player) }
    }
}

@Composable
fun DepositCurrencyOption(data: PlayerData, modifier: Modifier = Modifier) = Button {
    Text(
        "<gold><b>Open Deposit Menu".miniMsg(),
        "<yellow>You currently have <i>${data.orthCoinsHeld} <yellow>coins in your account.".miniMsg(),
        modifier = modifier.size(3, 2)
    )
}

@Composable
fun WithdrawCurrencyOption(data: PlayerData, modifier: Modifier = Modifier) = Button {
    Text(
        "<gold><b>Open Withdrawal Menu".miniMsg(),
        "<yellow>You currently have <i>${data.orthCoinsHeld} <yellow>coins in your account.".miniMsg(),
        modifier = modifier.size(3, 2)
    )
}

