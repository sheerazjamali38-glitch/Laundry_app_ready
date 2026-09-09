package com.cubelaundry.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cubelaundry.app.ui.theme.*

private const val WHATSAPP_URL = "https://wa.me/923478164692"

/** Dedicated WhatsApp tab: a single, clear "Chat With Us" entry point. */
@Composable
fun WhatsAppScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(Cyan, shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("Chat With Us", style = MaterialTheme.typography.titleLarge, color = Navy, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Questions about an order, pickup, or pricing? Message us directly on WhatsApp.",
            color = Sub,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(WHATSAPP_URL))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Color.White)
        ) {
            Icon(Icons.Filled.Chat, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Chat With Us", fontWeight = FontWeight.Bold)
        }
    }
}
