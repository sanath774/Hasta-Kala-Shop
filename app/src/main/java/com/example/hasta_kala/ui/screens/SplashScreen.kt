package com.example.hasta_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hasta_kala.data.AuthManager
import com.example.hasta_kala.ui.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val authManager = AuthManager()

    LaunchedEffect(key1 = true) {
        delay(2000)
        if (authManager.isUserLoggedIn()) {
            navController.navigate(Screen.MainContent.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F5FF)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp),
                tint = Color(0xFF673AB7)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Hasta-Kala Shop",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF673AB7)
            )
            Text(
                text = "Micro-Sales Analytics for Artisans",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(64.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(200.dp),
                color = Color(0xFF673AB7),
                trackColor = Color(0xFFD1C4E9)
            )
        }
    }
}
