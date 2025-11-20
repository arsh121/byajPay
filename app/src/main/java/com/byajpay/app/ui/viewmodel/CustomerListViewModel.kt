package com.byajpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byajpay.app.data.model.Customer
import com.byajpay.app.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val customersWithBalance = kotlinx.coroutines.flow.combine(
        _searchQuery,
        customerRepository.getAllCustomers()
    ) { query, allCustomers ->
        if (query.isEmpty()) {
            allCustomers
        } else {
            allCustomers.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phone?.contains(query, ignoreCase = true) == true
            }
        }
    }
    
    suspend fun getCustomerBalance(customerId: String): Double {
        return customerRepository.calculateOutstandingBalance(customerId)
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    suspend fun getCustomerById(customerId: String): Customer? {
        return customerRepository.getCustomerById(customerId)
    }
    
    suspend fun deleteCustomer(customer: Customer) {
        customerRepository.deleteCustomer(customer)
    }
}

