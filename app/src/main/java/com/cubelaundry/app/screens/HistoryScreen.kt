package com.cubelaundry.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cubelaundry.app.models.HistoryOrder
import com.cubelaundry.app.ui.theme.*
import com.cubelaundry.app.viewmodels.HistoryViewModel

private val COMPLETED_STATUSES = setOf("delivered", "completed")

private fun isOngoing(status: String) = status.trim().lowercase() !in COMPLETED_STATUSES

@Composable
fun HistoryScreen(navController: NavController, viewModel: HistoryViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Ongoing, 1 = History

    LaunchedEffect(Unit) { viewModel.loadHistory() }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Text(
            "My Orders",
            style = MaterialTheme.typography.headlineMedium,
            color = Navy,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        TabRow(selectedTabIndex = selectedTab, containerColor = Panel, contentColor = Navy) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Ongoing", fontWeight = FontWeight.SemiBold) },
                selectedContentColor = Cyan,
                unselectedContentColor = Sub
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("History", fontWeight = FontWeight.SemiBold) },
                selectedContentColor = Cyan,
                unselectedContentColor = Sub
            )
        }

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Cyan)
                }
            }
            state.noMobileOnFile -> {
                HistoryEmptyState("Place an order to see it here.")
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error ?: "", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
            else -> {
                val orders = state.orders ?: emptyList()
                val filtered = if (selectedTab == 0) {
                    orders.filter { isOngoing(it.status) }
                } else {
                    orders.filter { !isOngoing(it.status) }
                }

                if (filtered.isEmpty()) {
                    HistoryEmptyState(
                        if (selectedTab == 0) "No ongoing orders found" else "No orders in your history yet"
                    )
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(filtered) { order ->
                            HistoryOrderCard(order) {
                                navController.navigate("invoice/${order.invoice_number}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryEmptyState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Inbox, contentDescription = null, tint = Border, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(12.dp))
            Text(message, color = Sub, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HistoryOrderCard(order: HistoryOrder, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Panel)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Receipt, contentDescription = null, tint = Cyan)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(order.invoice_number, fontWeight = FontWeight.Bold, color = Ink)
                Text(order.created_at, style = MaterialTheme.typography.bodySmall, color = Sub)
                Text(order.status, style = MaterialTheme.typography.bodySmall, color = Blue)
            }
            Text("Rs. ${order.grand_total}", color = Navy, fontWeight = FontWeight.Bold)
        }
    }
}
