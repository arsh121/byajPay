package com.byajpay.app.data.repository

import com.byajpay.app.data.dao.CustomerDao
import com.byajpay.app.data.dao.TransactionDao
import com.byajpay.app.data.model.Customer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) {
    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()
    
    suspend fun getCustomerById(customerId: String): Customer? = customerDao.getCustomerById(customerId)
    
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
    
    suspend fun insertCustomer(customer: Customer) {
        customerDao.insertCustomer(customer)
    }
    
    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer.copy(lastUpdatedAt = System.currentTimeMillis()))
    }
    
    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer)
    }
    
    suspend fun calculateOutstandingBalance(customerId: String): Double {
        val transactions = transactionDao.getAllTransactionsForCustomerSync(customerId)
        var balance = 0.0
        transactions.forEach { transaction ->
            when (transaction.type) {
                com.byajpay.app.data.model.TransactionType.CREDIT -> balance += transaction.amount
                com.byajpay.app.data.model.TransactionType.DEBIT -> balance -= transaction.amount
            }
        }
        return balance
    }
}

