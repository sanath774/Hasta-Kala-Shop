package com.example.hasta_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
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
import com.example.hasta_kala.ui.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: ProductsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF673AB7),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5FF))
        ) {
            ProductTopBar()
            SearchBar()
            CategoryTabs(onCategorySelected = { viewModel.loadProducts(it) })
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF673AB7))
                }
            } else if (uiState.products.isEmpty()) {
                EmptyProductsView(onSeed = { viewModel.seedDemoData() })
            } else {
                ProductList(uiState.products)
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { product ->
                viewModel.addProduct(product)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun EmptyProductsView(onSeed: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "No products found", color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSeed,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
        ) {
            Text("Load Demo Artisan Products")
        }
    }
}

@Composable
fun ProductTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.MoreVert, contentDescription = null)
        Text(text = "Products", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.Search, contentDescription = null)
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = { Text("Search products...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}

@Composable
fun CategoryTabs(onCategorySelected: (String) -> Unit) {
    val categories = listOf("All", "Bags", "Keychains", "Others")
    var selectedIndex by remember { mutableIntStateOf(0) }
    
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
        divider = {},
        indicator = {}
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = selectedIndex == index,
                onClick = { 
                    selectedIndex = index
                    onCategorySelected(category)
                },
                text = {
                    Text(
                        text = category,
                        color = if (selectedIndex == index) Color(0xFF673AB7) else Color.Gray,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun ProductList(products: List<Product>) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductItem(product)
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    val icon = when(product.category) {
        "Bags" -> "👜"
        "Keychains" -> "🔑"
        else -> "📦"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "${product.category} • ${product.color}", color = Color.Gray, fontSize = 12.sp)
                Text(text = "₹ ${product.price}", fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
            }
            Column(horizontalAlignment = Alignment.End) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.LightGray)
                Spacer(modifier = Modifier.height(8.dp))
                val stockColor = if (product.stock <= 5) Color.Red else Color.Gray
                Text(text = "Stock: ${product.stock}", fontSize = 12.sp, color = stockColor, fontWeight = if (product.stock <= 5) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
fun AddProductDialog(onDismiss: () -> Unit, onConfirm: (Product) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Bags") }
    var color by remember { mutableStateOf("Natural") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Artisan Product") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (₹)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Initial Stock") }, modifier = Modifier.fillMaxWidth())
                
                Text(text = "Category", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Bags", "Keychains", "Others").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                
                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color/Design") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(Product(
                        name = name,
                        price = price.toDoubleOrNull() ?: 0.0,
                        stock = stock.toLongOrNull() ?: 0L,
                        category = category,
                        color = color
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) { Text("Add to Shop") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
