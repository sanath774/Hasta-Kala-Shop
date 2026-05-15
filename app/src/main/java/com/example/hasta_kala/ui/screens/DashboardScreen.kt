package com.example.hasta_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hasta_kala.ui.Screen
import com.example.hasta_kala.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5FF))
    ) {
        DashboardTopBar(navController)

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF673AB7))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    DateRangeSelector()
                }
                
                uiState.errorMessage?.let {
                    item {
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SummaryCard(
                            title = "Total Sales",
                            value = "${uiState.totalSales.toInt()}",
                            subtitle = "This Week",
                            modifier = Modifier.weight(1f),
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        SummaryCard(
                            title = "Total Income",
                            value = "₹ ${uiState.totalIncome.toInt()}",
                            subtitle = "This Month",
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.IncomeLog.route) }
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoCard(
                            title = "Best Seller",
                            item = uiState.bestSeller,
                            subtitle = "Most Sold",
                            modifier = Modifier.weight(1f),
                            onClick = {}
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        InfoCard(
                            title = "Low Stock Items",
                            item = "${uiState.lowStockCount} Items",
                            subtitle = "Check Now",
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Screen.Inventory.route) }
                        )
                    }
                }
                item {
                    Text(text = "Sales Overview", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    SalesOverviewChart()
                }
            }
        }
    }
}

@Composable
fun DashboardTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = null)
        Text(text = "Dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = { navController.navigate(Screen.AIInsights.route) }) {
            Icon(Icons.Default.SmartToy, contentDescription = "AI Insights", tint = Color(0xFF673AB7))
        }
    }
}

@Composable
fun DateRangeSelector() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "May 2 - May 8, 2025", fontSize = 14.sp)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun InfoCard(title: String, item: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
            Text(text = item, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Red)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SalesOverviewChart() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Mock chart
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.LightGray, RoundedCornerShape(75.dp))
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Pie Chart", modifier = Modifier.align(Alignment.Center))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ChartLegend("Red Bags", "60%", Color.Red)
            ChartLegend("Blue Bags", "20%", Color.Blue)
            ChartLegend("Keychains", "15%", Color.Green)
            ChartLegend("Others", "5%", Color.Yellow)
        }
    }
}

@Composable
fun ChartLegend(label: String, percentage: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, fontSize = 14.sp)
        }
        Text(text = percentage, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
