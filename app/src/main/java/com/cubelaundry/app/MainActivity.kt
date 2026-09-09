package com.cubelaundry.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cubelaundry.app.screens.*
import com.cubelaundry.app.ui.theme.CubeLaundryTheme
import com.cubelaundry.app.viewmodels.CartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CubeLaundryTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    // Created once here, tied to the Activity's ViewModelStore, and
                    // passed down explicitly so the cart survives navigation between
                    // bottom-nav tabs (Item Selection <-> Cart in particular).
                    val cartViewModel: CartViewModel = viewModel()
                    val cartState by cartViewModel.state.collectAsState()

                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry?.destination?.route
                    val showBottomBar = currentRoute in BOTTOM_BAR_ROUTES

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                CubeLaundryBottomBar(navController = navController, cartItemCount = cartState.itemCount)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "splash",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("splash") { SplashScreen(navController) }
                            composable("home") { HomeScreen(navController) }
                            composable("items") { ItemSelectionScreen(navController, cartViewModel) }
                            composable("cart") { CartScreen(navController, cartViewModel) }
                            composable("whatsapp") { WhatsAppScreen() }
                            composable("history") { HistoryScreen(navController) }
                            composable("profile") { ProfileScreen() }
                            composable("invoice/{invoiceNumber}") { entry ->
                                val invoiceNumber = entry.arguments?.getString("invoiceNumber") ?: ""
                                InvoiceScreen(navController, invoiceNumber)
                            }
                        }
                    }
                }
            }
        }
    }
}
