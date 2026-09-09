package com.cubelaundry.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cubelaundry.app.R
import com.cubelaundry.app.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painterResource(R.drawable.logo), contentDescription = "Cube Laundry", modifier = Modifier.size(84.dp))
            Spacer(Modifier.height(12.dp))
            Text("Cube Laundry", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text("Fresh • Fast • Reliable", fontSize = 13.sp, color = Sub)
        }

        Spacer(Modifier.height(32.dp))

        // Prominent "Place Order" entry point.
        Card(
            onClick = { navController.navigate("items") },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Navy),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.LocalLaundryService, contentDescription = null, tint = Cyan, modifier = Modifier.size(36.dp))
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Place an Order", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Pick your items and get them washed", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("How It Works", style = MaterialTheme.typography.titleMedium, color = Navy, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        HowItWorksRow("1. Select your items and services")
        HowItWorksRow("2. Confirm pickup and delivery details in your Cart")
        HowItWorksRow("3. We wash, iron, and deliver back to you")

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Blue)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("We currently deliver to", color = Sub, fontSize = 12.sp)
                    Text("Qasimabad & Rest of Hyderabad", color = Ink, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun HowItWorksRow(text: String) {
    Text(text, color = Sub, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 3.dp))
}
