package com.example.hasta_kala.data

import com.google.firebase.firestore.DocumentId

// 1. User Module
data class UserProfile(
    @DocumentId
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val businessName: String = ""
)

// 2. Product Management
data class Product(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val color: String = "",
    val price: Double = 0.0,
    val stock: Long = 0, // Changed to Long for Firestore compatibility
    val imageUrl: String = "",
    val createdDate: Long = System.currentTimeMillis()
)

// 4. Billing & Sales Module
data class Sale(
    @DocumentId
    val saleId: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 1,
    val color: String = "",
    val totalAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

// 3. Inventory History
data class InventoryLog(
    @DocumentId
    val logId: String = "",
    val productId: String = "",
    val changeAmount: Int = 0,
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
