package com.byajpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byajpay.app.data.model.Customer
import com.byajpay.app.data.model.Transaction
import com.byajpay.app.data.repository.CustomerRepository
import com.byajpay.app.data.repository.InterestRepository
import com.byajpay.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerWithRecentTransactions(
    val customer: Customer,
    val transactions: List<Transaction>,
    val outstandingBalance: Double
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository,
    private val interestRepository: InterestRepository
) : ViewModel() {
    
    private val _totalOutstanding = MutableStateFlow(0.0)
    val totalOutstanding: StateFlow<Double> = _totalOutstanding.asStateFlow()
    
    val customersWithRecentTransactions = combine(
        customerRepository.getAllCustomers(),
        transactionRepository.getRecentTransactions(50) // Get more transactions to group by customer
    ) { customers, allTransactions ->
        customers.mapNotNull { customer ->
            // Get all transactions for this customer from recent transactions
            val customerAllTransactions = allTransactions
                .filter { it.customerId == customer.id }
            
            // Only include customers with transactions
            if (customerAllTransactions.isEmpty()) {
                null
            } else {
                // Get last 3 transactions for expanded view
                val last3Transactions = customerAllTransactions.take(3)
                
                CustomerWithRecentTransactions(
                    customer = customer,
                    transactions = last3Transactions,
                    outstandingBalance = 0.0 // Will be calculated in UI using repository
                )
            }
        }.sortedByDescending { it.transactions.firstOrNull()?.date ?: 0L } // Sort by most recent transaction
    }
    
    init {
        loadTotalOutstanding()
        processInterestAccrual()
    }
    
    private fun loadTotalOutstanding() {
        viewModelScope.launch {
            customerRepository.getAllCustomers().collect { customers ->
                var total = 0.0
                customers.forEach { customer ->
                    total += customerRepository.calculateOutstandingBalance(customer.id)
                }
                _totalOutstanding.value = total
            }
        }
    }
    
    private fun processInterestAccrual() {
        viewModelScope.launch {
            interestRepository.processInterestAccrual()
        }
    }
    
    fun refresh() {
        loadTotalOutstanding()
        processInterestAccrual()
    }
    
    suspend fun calculateOutstandingBalance(customerId: String): Double {
        return customerRepository.calculateOutstandingBalance(customerId)
    }
}

