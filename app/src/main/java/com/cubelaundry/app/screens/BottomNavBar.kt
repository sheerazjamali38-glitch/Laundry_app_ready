package com.cubelaundry.app.screens

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cubelaundry.app.ui.theme.Blue
import com.cubelaundry.app.ui.theme.Cyan
import com.cubelaundry.app.ui.theme.Panel
import com.cubelaundry.app.ui.theme.Sub

data class BottomNavDestination(val route: String, val label: String, val icon: ImageVector)

/** The five persistent bottom-nav destinations. Blue-tinted icons only, per brand rules. */
val BOTTOM_NAV_DESTINATIONS = listOf(
    BottomNavDestination("home", "Home", Icons.Filled.Home),
    BottomNavDestination("cart", "Cart", Icons.Filled.ShoppingCart),
    BottomNavDestination("whatsapp", "WhatsApp", Icons.Filled.Chat),
    BottomNavDestination("history", "History", Icons.Filled.Receipt),
    BottomNavDestination("profile", "Profile", Icons.Filled.Person)
)

/** Routes on which the bottom bar should be shown (main tabs + item selection,
 * which is reached from Home but should still let you jump straight to Cart). */
val BOTTOM_BAR_ROUTES = setOf("home", "items", "cart", "whatsapp", "history", "profile")

@Composable
fun CubeLaundryBottomBar(navController: NavController, cartItemCount: Int) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = Panel, contentColor = Blue) {
        BOTTOM_NAV_DESTINATIONS.forEach { dest ->
            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(dest.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (dest.route == "cart" && cartItemCount > 0) {
                        BadgedBox(badge = { Badge(containerColor = Cyan) { Text(cartItemCount.toString()) } }) {
                            Icon(dest.icon, contentDescription = dest.label, modifier = androidx.compose.ui.Modifier.size(24.dp))
                        }
                    } else {
                        Icon(dest.icon, contentDescription = dest.label)
                    }
                },
                label = { Text(dest.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Cyan,
                    selectedTextColor = Cyan,
                    unselectedIconColor = Blue,
                    unselectedTextColor = Sub,
                    indicatorColor = Panel
                )
            )
        }
    }
}
