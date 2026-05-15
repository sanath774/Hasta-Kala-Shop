package com.example.hasta_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SmartToy
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
import com.example.hasta_kala.ui.viewmodel.AIInsightsViewModel

@Composable
fun AIInsightsScreen(
    navController: NavController,
    viewModel: AIInsightsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5FF))
    ) {
        AIInsightsTopBar(navController)
        
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AI Business Analysis",
                            fontSize = 18.sp,
                            color = Color(0xFF673AB7),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (uiState.isGenerating) {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF673AB7))
                            }
                        } else {
                            Text(
                                text = uiState.insights,
                                fontSize = 15.sp,
                                color = Color.DarkGray,
                                lineHeight = 22.sp
                            )
                        }
                        
                        uiState.error?.let {
                            Text(text = it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.generateInsights() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                    enabled = !uiState.isGenerating,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (uiState.isGenerating) "Analyzing Data..." else "Generate Business Insights", fontWeight = FontWeight.Bold)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Note: AI insights are based on your recent sales history and current inventory levels to help you reduce waste and improve profitability.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun AIInsightsTopBar(navController: NavController) {
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
        Text(text = "AI Insights", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF673AB7))
    }
}
