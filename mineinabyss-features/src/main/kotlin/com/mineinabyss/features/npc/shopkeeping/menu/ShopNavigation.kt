package com.mineinabyss.features.npc.shopkeeping.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mineinabyss.components.npc.shopkeeping.ShopKeeper
import com.mineinabyss.features.helpers.Text
import com.mineinabyss.features.helpers.ui.composables.Button
import com.mineinabyss.guiy.canvas.GuiyOwner
import com.mineinabyss.guiy.canvas.LocalGuiyOwner
import com.mineinabyss.guiy.modifiers.Modifier
import com.mineinabyss.guiy.navigation.*
import com.mineinabyss.idofront.textcomponents.miniMsg
import org.bukkit.entity.Player

sealed class ShopScreen(val title: String, val height: Int) {
    class Default(shopKeeper: ShopKeeper) : ShopScreen(shopKeeper.menu, 6)
    class Sell(shopKeeper: ShopKeeper) : ShopScreen(shopKeeper.menu, 6)
    class Buy(shopKeeper: ShopKeeper) : ShopScreen(shopKeeper.menu, 6)
    class Special(shopKeeper: ShopKeeper) : ShopScreen(shopKeeper.menu, 6)
}

class ShopUIScope(
    val player: Player,
    val owner: GuiyOwner,
    val shopKeeper: ShopKeeper,
) {
}

@Composable
fun BackButton(modifier: Modifier = Modifier) {
    val dispatcher = LocalBackGestureDispatcher.current
    Button(onClick = { dispatcher?.onBack() }, modifier = modifier) {
        Text("<red><b>Back".miniMsg())
    }
}

@Composable
fun ShopUIScope.CloseButton(modifier: Modifier = Modifier) {
    Button(onClick = { player.closeInventory() }, modifier = modifier) {
        Text("<red><b>Close".miniMsg())
    }
}

@Composable
fun NextPageButton(modifier: Modifier = Modifier) {
    Button(onClick = { }, modifier = modifier) {
        Text("<yellow><b>Next Page".miniMsg())
    }
}

@Composable
fun PreviousPageButton(modifier: Modifier = Modifier) {
    Button(onClick = { }, modifier = modifier) {
        Text("<yellow><b>Previous Page".miniMsg())
    }
}

@Composable
fun ShopMainMenu(player: Player, shopKeeper: ShopKeeper) {
    val owner = LocalGuiyOwner.current
    val scope = remember { ShopUIScope(player, owner, shopKeeper) }
    scope.apply {
        val nav = rememberNavController()
        BackHandler { nav.popBackStack() }
        //TODO each screen needs to create their own Chest instance now.
        NavHost(nav, startDestination = ShopScreen.Default(shopKeeper)) {
            composable<ShopScreen.Default> {
                ShopHomeScreen(
                    onNavigateToSpecial = { nav.navigate(ShopScreen.Special(shopKeeper)) },
                    onNavigateToBuyScreen = { nav.navigate(ShopScreen.Buy(shopKeeper)) },
                    onNavigateToSellScreen = { nav.navigate(ShopScreen.Sell(shopKeeper)) },
                )
            }
            composable<ShopScreen.Sell> { ShopSellMenu() }
            composable<ShopScreen.Buy> { ShopBuyMenu() }
            composable<ShopScreen.Special> { ShopSpecialMenu() }
        }
    }
}
