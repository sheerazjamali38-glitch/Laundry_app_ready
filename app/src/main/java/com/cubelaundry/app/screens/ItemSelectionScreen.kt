package com.cubelaundry.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cubelaundry.app.data.ALL_SECTIONS
import com.cubelaundry.app.data.SERVICE_TYPES
import com.cubelaundry.app.ui.theme.*
import com.cubelaundry.app.viewmodels.CartViewModel

/**
 * Reference-app interaction pattern: a top row of service-type tabs, a second
 * row of category (Men/Ladies) tabs, and a filtered item list below with a
 * quantity stepper per row. Items with no price for the selected service type
 * are simply hidden, matching the existing per-item rate data.
 */
@Composable
fun ItemSelectionScreen(navController: NavController, cartViewModel: CartViewModel) {
    val state by cartViewModel.state.collectAsState()

    var selectedServiceType by remember { mutableStateOf(SERVICE_TYPES.first()) }
    var selectedSection by remember { mutableStateOf(ALL_SECTIONS.first()) }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(Modifier.fillMaxSize()) {
            Text(
                "Select Items",
                style = MaterialTheme.typography.headlineMedium,
                color = Navy,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Choose a service, then pick your items",
                color = Sub,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Top row: service type tabs (Wash + Iron / Iron / Urgent).
            ScrollableTabRow(
                selectedTabIndex = SERVICE_TYPES.indexOf(selectedServiceType),
                containerColor = Panel,
                contentColor = Navy,
                edgePadding = 16.dp
            ) {
                SERVICE_TYPES.forEach { serviceType ->
                    Tab(
                        selected = selectedServiceType == serviceType,
                        onClick = { selectedServiceType = serviceType },
                        text = { Text(serviceType, fontWeight = FontWeight.SemiBold) },
                        selectedContentColor = Cyan,
                        unselectedContentColor = Sub
                    )
                }
            }

            // Second row: category tabs (Men / Ladies).
            TabRow(
                selectedTabIndex = ALL_SECTIONS.indexOf(selectedSection),
                containerColor = Background,
                contentColor = Navy
            ) {
                ALL_SECTIONS.forEach { section ->
                    Tab(
                        selected = selectedSection == section,
                        onClick = { selectedSection = section },
                        text = { Text(section.title, fontWeight = FontWeight.Medium) },
                        selectedContentColor = Blue,
                        unselectedContentColor = Sub
                    )
                }
            }

            if (state.isLoadingRates) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Cyan)
                }
            }

            state.ratesError?.let { err ->
                Text(err, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            val visibleCategories = selectedSection.categories.map { category ->
                category to category.itemNames.filter { itemName ->
                    state.rates[itemName]?.get(selectedServiceType) != null
                }
            }.filter { it.second.isNotEmpty() }

            if (!state.isLoadingRates && state.ratesError == null && visibleCategories.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "No items available for $selectedServiceType in ${selectedSection.title}.",
                        color = Sub,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp)
            ) {
                visibleCategories.forEach { (category, itemNames) ->
                    item { CategoryHeader(category.title) }
                    items(itemNames) { itemName ->
                        val price = state.rates[itemName]?.get(selectedServiceType) ?: 0
                        val qty = cartViewModel.quantityFor(itemName, selectedServiceType)
                        ItemRow(
                            itemName = itemName,
                            price = price,
                            quantity = qty,
                            onQuantityChange = { newQty ->
                                cartViewModel.setQuantity(itemName, selectedServiceType, newQty)
                            }
                        )
                    }
                }
            }
        }

        if (state.itemCount > 0) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = Navy,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = Cyan)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "View Cart (${state.itemCount} item${if (state.itemCount == 1) "" else "s"})",
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    TextButton(onClick = { navController.navigate("cart") }) {
                        Text("Rs. ${state.subtotal}", color = Cyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = Blue,
        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
    )
}

@Composable
private fun ItemRow(
    itemName: String,
    price: Int,
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(1.dp, Border)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(itemName, fontWeight = FontWeight.Bold, color = Ink, style = MaterialTheme.typography.bodyLarge)
                Text("Rs. $price", color = Blue, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
            }
            QuantityStepper(quantity = quantity, onQuantityChange = onQuantityChange)
        }
    }
}

@Composable
private fun QuantityStepper(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { if (quantity > 0) onQuantityChange(quantity - 1) }) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease", tint = Blue)
        }
        Text(quantity.toString(), modifier = Modifier.widthIn(min = 20.dp), textAlign = TextAlign.Center)
        IconButton(onClick = { onQuantityChange(quantity + 1) }) {
            Icon(Icons.Filled.Add, contentDescription = "Increase", tint = Cyan)
        }
    }
}
