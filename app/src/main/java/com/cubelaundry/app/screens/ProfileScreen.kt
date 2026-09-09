package com.cubelaundry.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cubelaundry.app.data.Prefs
import com.cubelaundry.app.ui.theme.*

private const val WHATSAPP_URL = "https://wa.me/923478164692"
private const val CONTACT_NUMBER = "+92 347 8164692"

/**
 * No login/account system: this just shows and edits the locally-stored
 * customer info that prefills the Cart form and looks up History, plus
 * static business info.
 */
@Composable
fun ProfileScreen() {
    val context = LocalContext.current

    var name by remember { mutableStateOf(Prefs.getName(context) ?: "") }
    var mobile by remember { mutableStateOf(Prefs.getMobileNumber(context) ?: "") }
    var address by remember { mutableStateOf(Prefs.getAddress(context) ?: "") }
    var saved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = Navy,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text("Your details, saved on this device", color = Sub, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Blue)
                    Spacer(Modifier.width(8.dp))
                    Text("Your Info", fontWeight = FontWeight.Bold, color = Navy)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; saved = false },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it; saved = false },
                    label = { Text("Mobile Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it; saved = false },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        Prefs.saveProfile(context, name, mobile, address)
                        saved = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
                if (saved) {
                    Spacer(Modifier.height(6.dp))
                    Text("Saved. This will prefill your Cart and History.", color = Blue, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Panel),
            border = BorderStroke(1.dp, Border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Cube Laundry", fontWeight = FontWeight.Bold, color = Navy)
                Spacer(Modifier.height(10.dp))
                InfoRow(icon = Icons.Filled.Phone, label = "Contact", value = CONTACT_NUMBER)
                Spacer(Modifier.height(8.dp))
                InfoRow(icon = Icons.Filled.Chat, label = "WhatsApp", value = WHATSAPP_URL, onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WHATSAPP_URL)))
                })
                Spacer(Modifier.height(8.dp))
                InfoRow(icon = Icons.Filled.LocationOn, label = "Service Areas", value = "Qasimabad & Rest of Hyderabad")
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, color = Sub, style = MaterialTheme.typography.bodySmall)
            Text(
                value,
                color = if (onClick != null) Blue else Ink,
                fontWeight = FontWeight.SemiBold,
                modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
        }
    }
}
