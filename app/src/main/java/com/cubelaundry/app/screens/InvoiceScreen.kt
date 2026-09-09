package com.cubelaundry.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cubelaundry.app.ui.theme.*
import com.cubelaundry.app.viewmodels.InvoiceViewModel

@Composable
fun InvoiceScreen(navController: NavController, invoiceNumber: String, viewModel: InvoiceViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(invoiceNumber) { viewModel.loadInvoice(invoiceNumber) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Invoice #$invoiceNumber",
            style = MaterialTheme.typography.headlineMedium,
            color = Navy,
            fontWeight = FontWeight.Bold
        )

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = Cyan)
        } else if (state.error != null) {
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        } else if (state.invoice != null) {
            val order = state.invoice!!.order
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Panel)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Customer: ${order?.customer_name}", color = Ink)
                    Text("Mobile: ${order?.mobile_number}", color = Ink)
                    Text("Address: ${order?.address}", color = Ink)
                    Text("Total: Rs. ${order?.grand_total}", fontWeight = FontWeight.Bold, color = Navy)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Items:", style = MaterialTheme.typography.titleSmall, color = Blue)
                    state.invoice!!.items?.forEach {
                        Text("- ${it.item_name} (${it.service_type}) x${it.quantity} = Rs. ${it.subtotal}", color = Ink)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = {
                val url = "https://cubelaundry.shop/orders/view_invoice.php?invoice=$invoiceNumber"
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }) {
                Text("View in browser", color = Blue)
            }
        } else {
            Text("No invoice data.", color = Sub)
        }
    }
}
