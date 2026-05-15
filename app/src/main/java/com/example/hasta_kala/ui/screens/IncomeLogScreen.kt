package com.example.hasta_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun IncomeLogScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5FF))
    ) {
        IncomeLogTopBar(navController)
        IncomeLogTabs()
        DateNavigation()
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Total Income for May 2025", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹ 45,230",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF673AB7)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "↑ 12% from last month", fontSize = 12.sp, color = Color(0xFF4CAF50))
            }
        }

        Text(
            text = "Transaction History",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        IncomeList()
    }
}

@Composable
fun IncomeLogTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Income Log", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun IncomeLogTabs() {
    var selectedTabIndex by remember { mutableIntStateOf(2) }
    val tabs = listOf("Daily", "Weekly", "Monthly")
    
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = Color(0xFF673AB7)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun DateNavigation() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) { Icon(Icons.Default.ChevronLeft, contentDescription = null) }
        Text(text = "May 2025", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 24.dp))
        IconButton(onClick = {}) { Icon(Icons.Default.ChevronRight, contentDescription = null) }
    }
}

data class IncomeEntry(val date: String, val amount: String, val type: String)

@Composable
fun IncomeList() {
    val entries = listOf(
        IncomeEntry("May 7, 2025", "₹ 4,560", "Direct Sale"),
        IncomeEntry("May 6, 2025", "₹ 3,650", "Online Order"),
        IncomeEntry("May 5, 2025", "₹ 2,950", "Direct Sale"),
        IncomeEntry("May 4, 2025", "₹ 6,100", "Direct Sale"),
        IncomeEntry("May 3, 2025", "₹ 5,620", "Online Order"),
        IncomeEntry("May 2, 2025", "₹ 3,870", "Direct Sale"),
        IncomeEntry("May 1, 2025", "₹ 4,350", "Direct Sale")
    )

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(entries) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(0.dp) // Flat list look
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = entry.date, fontWeight = FontWeight.Medium)
                        Text(text = entry.type, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(text = entry.amount, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), fontSize = 18.sp)
                }
            }
            HorizontalDivider(color = Color(0xFFF0F0F0))
        }
    }
}
