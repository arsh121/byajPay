package com.byajpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byajpay.app.data.repository.CustomerRepository
import com.byajpay.app.data.repository.InterestRepository
import com.byajpay.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository,
    private val interestRepository: InterestRepository
) : ViewModel() {
    
    private val _totalOutstanding = MutableStateFlow(0.0)
    val totalOutstanding: StateFlow<Double> = _totalOutstanding.asStateFlow()
    
    val recentTransactions = transactionRepository.getRecentTransactions(3)
    
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
}

