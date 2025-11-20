package com.byajpay.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byajpay.app.data.model.User
import com.byajpay.app.data.preferences.PreferencesManager
import com.byajpay.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    fun sendOtp(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // TODO: Integrate with actual OTP service
                // For now, simulate OTP sending
                _uiState.value = AuthUiState.OtpSent(phoneNumber)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to send OTP")
            }
        }
    }
    
    fun verifyOtp(phoneNumber: String, otp: String, name: String? = null, businessName: String? = null) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // TODO: Integrate with actual OTP verification service
                // For now, accept any 6-digit OTP
                if (otp.length == 6) {
                    val user = userRepository.getUserByPhone(phoneNumber)
                    if (user == null) {
                        // Create new user
                        val newUser = User(
                            phoneNumber = phoneNumber,
                            name = name,
                            businessName = businessName
                        )
                        userRepository.insertUser(newUser)
                        preferencesManager.setLoggedIn(phoneNumber)
                        _uiState.value = AuthUiState.Success(newUser)
                    } else {
                        // Update user if name/business name provided
                        if (name != null || businessName != null) {
                            val updatedUser = user.copy(
                                name = name ?: user.name,
                                businessName = businessName ?: user.businessName
                            )
                            userRepository.updateUser(updatedUser)
                            preferencesManager.setLoggedIn(phoneNumber)
                            _uiState.value = AuthUiState.Success(updatedUser)
                        } else {
                            preferencesManager.setLoggedIn(phoneNumber)
                            _uiState.value = AuthUiState.Success(user)
                        }
                    }
                } else {
                    _uiState.value = AuthUiState.Error("Invalid OTP")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to verify OTP")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = AuthUiState.Initial
    }
}

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    data class OtpSent(val phoneNumber: String) : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

