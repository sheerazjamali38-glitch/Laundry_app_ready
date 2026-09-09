package com.cubelaundry.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cubelaundry.app.data.BANK_ACCOUNT_NUMBER
import com.cubelaundry.app.data.Prefs
import com.cubelaundry.app.models.OrderResponse
import com.cubelaundry.app.ui.theme.*
import com.cubelaundry.app.viewmodels.CartLine
import com.cubelaundry.app.viewmodels.CartViewModel

private val DiscountGreen = Color(0xFF28A745)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val state by cartViewModel.state.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf(Prefs.getName(context) ?: "") }
    var phone by remember { mutableStateOf(Prefs.getMobileNumber(context) ?: "") }
    var address by remember { mutableStateOf(Prefs.getAddress(context) ?: "") }
    var specialInstructions by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        if (state.lines.isEmpty()) {
            EmptyCartState(onBrowse = { navController.navigate("items") })
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 220.dp)
            ) {
                item {
                    Text(
                        "Your Cart",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Navy,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                }

                items(state.lines) { line ->
                    CartLineRow(
                        line = line,
                        onQuantityChange = { newQty ->
                            cartViewModel.setQuantity(line.itemName, line.serviceType, newQty)
                        },
                        onRemove = { cartViewModel.removeLine(line.itemName, line.serviceType) }
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    CustomerDetailsSection(
                        name = name, onNameChange = { name = it },
                        phone = phone, onPhoneChange = { phone = it },
                        address = address, onAddressChange = { address = it },
                        specialInstructions = specialInstructions,
                        onSpecialInstructionsChange = { specialInstructions = it },
                        deliveryArea = state.deliveryArea, onDeliveryAreaChange = cartViewModel::setDeliveryArea,
                        paymentMethod = state.paymentMethod, onPaymentMethodChange = cartViewModel::setPaymentMethod
                    )
                }

                state.error?.let { err ->
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(err, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            CartTotalsBar(
                itemCount = state.itemCount,
                subtotal = state.subtotal,
                discountAmount = state.discountAmount,
                deliveryCharge = state.deliveryCharge,
                grandTotal = state.grandTotalEstimate,
                isSubmitting = state.isSubmitting,
                enabled = state.itemCount > 0 && name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                onPlaceOrder = { cartViewModel.placeOrder(name, phone, address, specialInstructions) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    state.orderResult?.let { result ->
        OrderConfirmationDialog(
            result = result,
            paymentMethod = state.paymentMethod,
            onViewInvoice = {
                val invoice = result.invoiceNumber
                cartViewModel.clearOrderResult()
                if (invoice != null) navController.navigate("invoice/$invoice")
            },
            onDismiss = { cartViewModel.clearOrderResult() }
        )
    }
}

@Composable
private fun EmptyCartState(onBrowse: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.RemoveShoppingCart,
            contentDescription = null,
            tint = Border,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Your cart is empty", color = Navy, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text("Add items to get started", color = Sub, textAlign = TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onBrowse, colors = ButtonDefaults.buttonColors(containerColor = Cyan)) {
            Text("Browse Items")
        }
    }
}

@Composable
private fun CartLineRow(line: CartLine, onQuantityChange: (Int) -> Unit, onRemove: () -> Unit) {
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
            Column(Modifier.weight(1f)) {
                Text(line.itemName, fontWeight = FontWeight.Bold, color = Ink)
                Text(line.serviceType, color = Sub, style = MaterialTheme.typography.bodySmall)
                Text("Rs. ${line.subtotal}", color = Blue, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onQuantityChange(line.quantity - 1) }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease", tint = Blue)
                }
                Text(line.quantity.toString(), modifier = Modifier.widthIn(min = 20.dp), textAlign = TextAlign.Center)
                IconButton(onClick = { onQuantityChange(line.quantity + 1) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase", tint = Cyan)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerDetailsSection(
    name: String, onNameChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    specialInstructions: String, onSpecialInstructionsChange: (String) -> Unit,
    deliveryArea: String, onDeliveryAreaChange: (String) -> Unit,
    paymentMethod: String, onPaymentMethodChange: (String) -> Unit
) {
    Column {
        Text("Delivery Details", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Your Name*") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Mobile Number*") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Address*") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))

        Text("Delivery Area", color = Sub, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf(
                "Qasimabad" to "Qasimabad (Rs. 100)",
                "Rest of Hyderabad" to "Rest of Hyderabad (Rs. 200)"
            ).forEach { (value, label) ->
                FilterChip(
                    selected = deliveryArea == value,
                    onClick = { onDeliveryAreaChange(value) },
                    label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = specialInstructions,
            onValueChange = onSpecialInstructionsChange,
            label = { Text("Special Instructions (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Text("Payment Method", color = Sub, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf(
                "cash" to "Cash on Delivery",
                "bank" to "Bank Transfer"
            ).forEach { (value, label) ->
                FilterChip(
                    selected = paymentMethod == value,
                    onClick = { onPaymentMethodChange(value) },
                    label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CartTotalsBar(
    itemCount: Int,
    subtotal: Int,
    discountAmount: Int,
    deliveryCharge: Int,
    grandTotal: Int,
    isSubmitting: Boolean,
    enabled: Boolean,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Panel,
        shadowElevation = 12.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$itemCount item(s)", color = Sub, style = MaterialTheme.typography.bodySmall)
                Text("Subtotal: Rs. $subtotal", color = Ink, style = MaterialTheme.typography.bodySmall)
            }
            if (discountAmount > 0) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Discount", color = DiscountGreen, style = MaterialTheme.typography.bodySmall)
                    Text("- Rs. $discountAmount", color = DiscountGreen, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Delivery", color = Sub, style = MaterialTheme.typography.bodySmall)
                Text(
                    if (deliveryCharge == 0) "Free" else "Rs. $deliveryCharge",
                    color = Sub,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Grand Total", fontWeight = FontWeight.Bold, color = Navy)
                Text("Rs. $grandTotal", fontWeight = FontWeight.Bold, color = Navy)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onPlaceOrder,
                enabled = enabled && !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Color.White)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Place Order")
                }
            }
        }
    }
}

@Composable
private fun OrderConfirmationDialog(
    result: OrderResponse,
    paymentMethod: String,
    onViewInvoice: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Order Placed!", color = Navy, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Invoice: ${result.invoiceNumber ?: "-"}")
                Text("Grand Total: Rs. ${result.grandTotal ?: 0}", fontWeight = FontWeight.Bold, color = Navy)
                if (paymentMethod == "bank") {
                    Spacer(Modifier.height(8.dp))
                    Text("Bank Transfer Details:", color = Sub, style = MaterialTheme.typography.bodySmall)
                    Text(BANK_ACCOUNT_NUMBER, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onViewInvoice) { Text("View Invoice") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
