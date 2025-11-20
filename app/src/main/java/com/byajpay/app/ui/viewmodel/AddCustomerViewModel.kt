package com.byajpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byajpay.app.data.model.Customer
import com.byajpay.app.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CustomerUiState>(CustomerUiState.Idle)
    val uiState: StateFlow<CustomerUiState> = _uiState.asStateFlow()
    
    fun addCustomer(name: String, phone: String? = null, notes: String? = null) {
        viewModelScope.launch {
            _uiState.value = CustomerUiState.Loading
            try {
                if (name.isBlank()) {
                    _uiState.value = CustomerUiState.Error("Name is required")
                    return@launch
                }
                
                val customer = Customer(
                    id = UUID.randomUUID().toString(),
                    name = name.trim(),
                    phone = phone?.trim()?.takeIf { it.isNotBlank() },
                    notes = notes?.trim()?.takeIf { it.isNotBlank() }
                )
                
                customerRepository.insertCustomer(customer)
                _uiState.value = CustomerUiState.Success(customer)
            } catch (e: Exception) {
                _uiState.value = CustomerUiState.Error(e.message ?: "Failed to add customer")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = CustomerUiState.Idle
    }
}

sealed class CustomerUiState {
    object Idle : CustomerUiState()
    object Loading : CustomerUiState()
    data class Success(val customer: Customer) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
}

