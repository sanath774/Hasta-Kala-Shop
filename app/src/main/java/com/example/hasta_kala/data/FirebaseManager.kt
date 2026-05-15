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
            // We manually set the ID field to match the document name
            val productWithId = product.copy(id = ref.id)
            ref.set(productWithId).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding product", e)
            false
        }
    }

    suspend fun getProducts(category: String? = null): List<Product> {
        return try {
            var query: Query = db.collection("products")
            if (category != null && category != "All") {
                query = query.whereEqualTo("category", category)
            }
            
            val snapshot = query.get().await()
            // MANUAL MAPPING: This ensures the 'id' is ALWAYS correct
            val products = snapshot.documents.mapNotNull { doc ->
                val p = doc.toObject(Product::class.java)
                p?.copy(id = doc.id) 
            }
            Log.d(TAG, "Fetched ${products.size} products correctly with IDs")
            products
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products", e)
            emptyList()
        }
    }

    suspend fun processSale(sale: Sale): Result<Boolean> {
        return try {
            if (sale.productId.isEmpty()) {
                return Result.failure(Exception("Error: Selected product has no valid ID. Please refresh."))
            }

            val productRef = db.collection("products").document(sale.productId)
            val saleRef = db.collection("sales").document()
            val logRef = db.collection("inventory_logs").document()
            
            db.runTransaction { transaction ->
                val productSnapshot = transaction.get(productRef)
                
                if (!productSnapshot.exists()) {
                    throw Exception("Product not found in database. It might have been deleted.")
                }

                // Read stock safely from the snapshot
                val dbStock = productSnapshot.getLong("stock") ?: 0L
                
                if (dbStock >= sale.quantity) {
                    // 1. Record the Sale
                    transaction.set(saleRef, sale.copy(saleId = saleRef.id))
                    
                    // 2. Update the Inventory (Direct subtraction)
                    transaction.update(productRef, "stock", dbStock - sale.quantity)
                    
                    // 3. Log the history
                    val log = InventoryLog(
                        logId = logRef.id,
                        productId = sale.productId,
                        changeAmount = -sale.quantity,
                        reason = "Sale",
                        timestamp = System.currentTimeMillis()
                    )
                    transaction.set(logRef, log)
                } else {
                    throw Exception("Stock Alert: Only $dbStock left. You requested ${sale.quantity}.")
                }
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Sale Process Failed", e)
            Result.failure(e)
        }
    }

    // --- OTHER HELPERS ---

    suspend fun getSalesLogs(): List<Sale> {
        return try {
            db.collection("sales")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await().toObjects(Sale::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getBestSellers(): Map<String, Int> {
        return try {
            val sales = db.collection("sales").get().await().toObjects(Sale::class.java)
            sales.groupBy { it.productName }
                .mapValues { entry -> entry.value.sumOf { it.quantity } }
                .toList()
                .sortedByDescending { it.second }
                .take(5)
                .toMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun getBestSellersByColor(): Map<String, Int> {
        return try {
            val sales = db.collection("sales").get().await().toObjects(Sale::class.java)
            sales.groupBy { it.color }
                .mapValues { entry -> entry.value.sumOf { it.quantity } }
        } catch (e: Exception) {
            emptyMap()
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
            "totalSalesCount" to sales.sumOf { it.quantity }.toInt(),
            "lowStockCount" to lowStockCount,
            "bestSeller" to bestSeller
        )
    }

    suspend fun generateAIInsights(apiKey: String): String {
        return try {
            val products = getProducts()
            val stats = getDashboardStats()
            val prompt = "Artisan Shop Analysis: Income: ${stats["totalIncome"]}, Best Seller: ${stats["bestSeller"]}. Inventory: ${products.size} types. Give 3 tips."
            val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = apiKey)
            val response = generativeModel.generateContent(prompt)
            response.text ?: "No insights available."
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
