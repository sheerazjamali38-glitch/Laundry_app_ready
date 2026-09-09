package com.cubelaundry.app.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cubelaundry.app.R
import com.cubelaundry.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Pure logo-only launch screen: no buttons, no user interaction. Logo fades
 * in with the tagline, the tagline fades back out after ~1s leaving just the
 * logo, then at ~2s total we auto-navigate to Home.
 */
@Composable
fun SplashScreen(navController: NavController) {
    var taglineVisible by remember { mutableStateOf(false) }
    val taglineAlpha by animateFloatAsState(
        targetValue = if (taglineVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "taglineAlpha"
    )

    LaunchedEffect(Unit) {
        taglineVisible = true
        delay(1000)
        taglineVisible = false
        delay(1000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Navy),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource(R.drawable.logo),
                contentDescription = "Cube Laundry",
                modifier = Modifier.size(140.dp)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Fresh • Fast • Reliable",
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }
    }
}
