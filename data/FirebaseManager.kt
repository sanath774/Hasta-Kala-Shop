package com.example.hasta_kala.data

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseManager {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirebaseManager"
    
    suspend fun addProduct(product: Product): Boolean {
        return try {
            val ref = db.collection("products").document()
            ref.set(product.copy(id = ref.id)).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding product", e)
            false
        }
    }

    suspend fun getProducts(category: String? = null): List<Product> {
        return try {
            var query: Query = db.collection("products")
            if (category != null) {
                query = query.whereEqualTo("category", category)
            }
            query.get().await().toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun processSale(sale: Sale): Result<Boolean> {
        return try {
            val saleRef = db.collection("sales").document()
            val productRef = db.collection("products").document(sale.productId)
            val logRef = db.collection("inventory_logs").document()
            
            db.runTransaction { transaction ->
                val productSnapshot = transaction.get(productRef)
                
                if (!productSnapshot.exists()) {
                    throw Exception("Product record not found in database")
                }

                val currentStock = productSnapshot.getLong("stock") ?: 0
                
                if (currentStock >= sale.quantity) {
                    transaction.set(saleRef, sale.copy(saleId = saleRef.id))
                    transaction.update(productRef, "stock", currentStock - sale.quantity)
                    val log = InventoryLog(
                        logId = logRef.id,
                        productId = sale.productId,
                        changeAmount = -sale.quantity,
                        reason = "Sale"
                    )
                    transaction.set(logRef, log)
                } else {
                    throw Exception("Only $currentStock units available. Increase stock first.")
                }
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Sale Process Failed", e)
            Result.failure(e)
        }
    }

    suspend fun getSalesLogs(): List<Sale> {
        return try {
            db.collection("sales")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await().toObjects(Sale::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDashboardStats(): Map<String, Any> {
        val products = getProducts()
        val sales = getSalesLogs()
        
        val totalIncome = sales.sumOf { it.totalAmount }
        val lowStockCount = products.count { it.stock <= 5 }
        val bestSeller = sales.groupBy { it.productName }
            .maxByOrNull { it.value.sumOf { it.quantity } }?.key ?: "N/A"

        return mapOf(
            "totalIncome" to totalIncome,
            "totalSalesCount" to sales.sumOf { it.quantity },
            "lowStockCount" to lowStockCount,
            "bestSeller" to bestSeller
        )
    }
}
