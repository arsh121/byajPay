package com.byajpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byajpay.app.data.model.InterestCycle
import com.byajpay.app.data.model.InterestRule
import com.byajpay.app.data.model.Transaction
import com.byajpay.app.data.model.TransactionType
import com.byajpay.app.data.repository.CustomerRepository
import com.byajpay.app.data.repository.InterestRepository
import com.byajpay.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val customerRepository: CustomerRepository,
    private val interestRepository: InterestRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Idle)
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    fun addTransaction(
        customerId: String,
        amount: Double,
        type: TransactionType,
        date: Long,
        notes: String? = null,
        chargeInterest: Boolean = false,
        interestRate: Double? = null,
        interestCycle: InterestCycle? = null
    ) {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading
            try {
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    customerId = customerId,
                    amount = amount,
                    type = type,
                    date = date,
                    notes = notes
                )
                
                transactionRepository.insertTransaction(transaction)
                
                // Create interest rule if applicable
                if (chargeInterest && type == TransactionType.CREDIT && interestRate != null && interestCycle != null) {
                    interestRepository.createInterestRule(
                        transactionId = transaction.id,
                        customerId = customerId,
                        principal = amount,
                        rate = interestRate,
                        cycle = interestCycle,
                        startDate = date
                    )
                }
                
                _uiState.value = TransactionUiState.Success(transaction)
            } catch (e: Exception) {
                _uiState.value = TransactionUiState.Error(e.message ?: "Failed to add transaction")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = TransactionUiState.Idle
    }
}

sealed class TransactionUiState {
    object Idle : TransactionUiState()
    object Loading : TransactionUiState()
    data class Success(val transaction: Transaction) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
}

