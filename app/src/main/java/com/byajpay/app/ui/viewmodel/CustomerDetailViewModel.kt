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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository,
    private val interestRepository: InterestRepository
) : ViewModel() {
    
    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()
    
    private val _outstandingBalance = MutableStateFlow(0.0)
    val outstandingBalance: StateFlow<Double> = _outstandingBalance.asStateFlow()
    
    fun loadCustomer(customerId: String) {
        viewModelScope.launch {
            val customer = customerRepository.getCustomerById(customerId)
            _customer.value = customer
            if (customer != null) {
                updateOutstandingBalance(customerId)
            }
        }
    }
    
    val transactions = _customer.asStateFlow().flatMapLatest { customer ->
        if (customer != null) {
            transactionRepository.getTransactionsByCustomer(customer.id)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList<Transaction>())
        }
    }
    
    private fun updateOutstandingBalance(customerId: String) {
        viewModelScope.launch {
            _outstandingBalance.value = customerRepository.calculateOutstandingBalance(customerId)
        }
    }
    
    fun refresh(customerId: String) {
        loadCustomer(customerId)
        processInterestAccrual()
    }
    
    private fun processInterestAccrual() {
        viewModelScope.launch {
            interestRepository.processInterestAccrual()
        }
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionRepository.deleteTransaction(transaction)
        _customer.value?.let { updateOutstandingBalance(it.id) }
    }
}

