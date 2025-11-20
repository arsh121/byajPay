package com.byajpay.app.data.dao

import androidx.room.*
import com.byajpay.app.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY date DESC, createdAt DESC")
    fun getTransactionsByCustomer(customerId: String): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): Transaction?
    
    @Query("SELECT * FROM transactions WHERE customerId = :customerId AND date <= :beforeDate AND createdAt <= :beforeTimestamp ORDER BY date DESC, createdAt DESC LIMIT 1")
    suspend fun getLastTransactionBefore(customerId: String, beforeDate: Long, beforeTimestamp: Long): Transaction?
    
    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY date DESC, createdAt DESC LIMIT :limit")
    fun getRecentTransactions(customerId: String, limit: Int = 3): Flow<List<Transaction>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)
    
    @Update
    suspend fun updateTransaction(transaction: Transaction)
    
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
    
    @Query("SELECT * FROM transactions WHERE customerId = :customerId")
    suspend fun getAllTransactionsForCustomerSync(customerId: String): List<Transaction>
    
    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 3): Flow<List<Transaction>>
}

