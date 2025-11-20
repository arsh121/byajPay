package com.byajpay.app.data.repository

import com.byajpay.app.data.dao.TransactionDao
import com.byajpay.app.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getTransactionsByCustomer(customerId: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCustomer(customerId)
    
    suspend fun getTransactionById(transactionId: String): Transaction? =
        transactionDao.getTransactionById(transactionId)
    
    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }
    
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
    
    fun getRecentTransactions(limit: Int = 3): Flow<List<Transaction>> =
        transactionDao.getRecentTransactions(limit)
    
    suspend fun getLastTransactionBefore(customerId: String, beforeDate: Long, beforeTimestamp: Long): Transaction? =
        transactionDao.getLastTransactionBefore(customerId, beforeDate, beforeTimestamp)
}

