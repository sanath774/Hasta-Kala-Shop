package com.example.hasta_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hasta_kala.data.AuthManager
import com.example.hasta_kala.data.UserProfile
import com.example.hasta_kala.ui.Screen

@Composable
fun ProfileScreen(navController: NavController) {
    val authManager = remember { AuthManager() }
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    
    LaunchedEffect(Unit) {
        profile = authManager.getUserProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5FF))
    ) {
        ProfileTopBar()
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = profile?.name ?: "Artisan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "ID: ${profile?.userId?.take(8) ?: "..."}", color = Color.Gray, fontSize = 14.sp)
            Text(text = profile?.email ?: "loading...", color = Color.Gray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileMenuItem(Icons.Default.BusinessCenter, "Business Info")
        ProfileMenuItem(Icons.Default.Settings, "Settings")
        ProfileMenuItem(Icons.Default.Lock, "Change Password")
        ProfileMenuItem(Icons.Default.Help, "Help & Support")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        ProfileMenuItem(
            icon = Icons.AutoMirrored.Filled.Logout, 
            title = "Logout", 
            color = Color.Red,
            onClick = {
                authManager.logout()
                // Navigate back to Login and clear the backstack
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun ProfileTopBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF673AB7)
    ) {
        Text(
            text = "Profile",
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector, 
    title: String, 
    color: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), color = color, fontSize = 16.sp)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFEEEEEE))
}
