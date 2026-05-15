package com.example.hasta_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hasta_kala.data.Product
import com.example.hasta_kala.ui.viewmodel.BillingViewModel

@Composable
fun QuickBillingScreen(viewModel: BillingViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5FF))
            .padding(16.dp)
    ) {
        Text(text = "Quick Billing", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
        Text(text = "Tap a product to start billing", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(text = "Artisan Catalog", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        if (uiState.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                Text("No products available. Add some in Products tab.", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.products) { product ->
                    ProductSelectionCard(
                        product = product,
                        isSelected = uiState.selectedProduct?.id == product.id,
                        onClick = { viewModel.selectProduct(product) }
                    )
                }
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFE0E0FF))
        
        uiState.selectedProduct?.let { product ->
            Text(text = "Order Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFF0F0FF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if(product.category == "Bags") "👜" else "🔑", fontSize = 24.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = "${product.category} • ${product.color}", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Quantity", fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.updateQuantity(uiState.quantity - 1) },
                                enabled = uiState.quantity > 1
                            ) { 
                                Icon(Icons.Default.Remove, "Less", tint = if(uiState.quantity > 1) Color(0xFF673AB7) else Color.Gray) 
                            }
                            
                            Surface(
                                color = Color(0xFFF5F5F5),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.width(40.dp)
                            ) {
                                Text(
                                    text = "${uiState.quantity}", 
                                    modifier = Modifier.padding(vertical = 4.dp), 
                                    fontSize = 18.sp, 
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            
                            IconButton(
                                onClick = { viewModel.updateQuantity(uiState.quantity + 1) },
                                enabled = uiState.quantity < product.stock
                            ) { 
                                Icon(Icons.Default.Add, "More", tint = if(uiState.quantity < product.stock) Color(0xFF673AB7) else Color.Gray) 
                            }
                        }
                    }
                    
                    if (product.stock <= 5) {
                        Text(
                            text = "Only ${product.stock} units left in stock!", 
                            color = Color.Red, 
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingBag, null, modifier = Modifier.size(64.dp), tint = Color(0xFFE0E0FF))
                    Text("Select a product above", color = Color.LightGray)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        uiState.error?.let {
            Text(text = it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        // Summary and Action
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Total Amount", color = Color.Gray, fontSize = 16.sp)
                    val total = (uiState.selectedProduct?.price ?: 0.0) * uiState.quantity
                    Text(text = "₹ $total", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF673AB7))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.processSale() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState.selectedProduct != null && !uiState.isProcessing
                ) {
                    if (uiState.isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(text = "Confirm & Save Sale", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (uiState.success) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text("🎉 Sale Recorded!") },
            text = { Text("The inventory for '${uiState.selectedProduct?.name}' has been updated successfully.") },
            confirmButton = { 
                Button(
                    onClick = { viewModel.resetState() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                ) { Text("Ready for next sale") } 
            }
        )
    }
}

@Composable
fun ProductSelectionCard(product: Product, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(130.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF673AB7) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if(product.category == "Bags") "👜" else "🔑", 
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = product.name, 
                fontWeight = FontWeight.Bold, 
                fontSize = 12.sp, 
                color = if(isSelected) Color.White else Color.Black,
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = "₹ ${product.price}", 
                fontSize = 14.sp, 
                color = if(isSelected) Color(0xFFD1C4E9) else Color(0xFF673AB7),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
