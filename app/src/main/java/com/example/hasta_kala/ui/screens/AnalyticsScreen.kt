package com.example.hasta_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hasta_kala.ui.Screen
import com.example.hasta_kala.ui.viewmodel.AnalyticsViewModel
import com.example.hasta_kala.ui.viewmodel.TimeRange
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // Refresh data when the screen is opened
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5FF))
    ) {
        AnalyticsTopBar(navController, onRefresh = { viewModel.loadData() })
        
        AnalyticsTabs(
            selectedRange = uiState.selectedRange,
            onRangeSelected = { viewModel.setTimeRange(it) }
        )
        
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF673AB7))
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Button(
                        onClick = { navController.navigate(Screen.AIInsights.route) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                    ) {
                        Text("✨ View Smart AI Insights")
                    }
                }
                
                item {
                    BestSellingSection(uiState.colorSales)
                }
                
                item {
                    Text(
                        text = "Sales Log (${uiState.filteredSales.size} Records)", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                
                if (uiState.filteredSales.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No sales found for this period.", color = Color.Gray)
                        }
                    }
                } else {
                    items(uiState.filteredSales) { sale ->
                        RecentSaleItem(sale)
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsTopBar(navController: NavController, onRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(text = "Business Analytics", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = "Sync")
        }
    }
}

@Composable
fun AnalyticsTabs(selectedRange: TimeRange, onRangeSelected: (TimeRange) -> Unit) {
    val ranges = listOf(TimeRange.WEEK, TimeRange.MONTH, TimeRange.YEAR)
    val labels = listOf("This Week", "This Month", "This Year")
    
    TabRow(
        selectedTabIndex = selectedRange.ordinal,
        containerColor = Color.White,
        contentColor = Color(0xFF673AB7)
    ) {
        ranges.forEachIndexed { index, range ->
            Tab(
                selected = selectedRange == range,
                onClick = { onRangeSelected(range) },
                text = { Text(labels[index]) }
            )
        }
    }
}

@Composable
fun BestSellingSection(colorSales: Map<String, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Top Selling Designs (Colors)", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            if (colorSales.isEmpty()) {
                Text("Not enough data to calculate trends.", color = Color.LightGray, fontSize = 14.sp)
            } else {
                colorSales.forEach { (colorName, qty) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), 
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = colorName, fontSize = 16.sp)
                        Text(text = "$qty Units Sold", fontWeight = FontWeight.ExtraBold, color = Color(0xFF673AB7))
                    }
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                }
            }
        }
    }
}

@Composable
fun RecentSaleItem(sale: com.example.hasta_kala.data.Sale) {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = sdf.format(Date(sale.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = sale.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "${sale.color} • $dateString", color = Color.Gray, fontSize = 12.sp)
            }
            Text(
                text = "₹ ${sale.totalAmount.toInt()}", 
                color = Color(0xFF4CAF50), 
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
    }
}
