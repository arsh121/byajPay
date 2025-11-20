package com.byajpay.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.byajpay.app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var showOtpScreen by remember { mutableStateOf(false) }
    var showNameScreen by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is com.byajpay.app.ui.viewmodel.AuthUiState.OtpSent -> {
                showOtpScreen = true
            }
            is com.byajpay.app.ui.viewmodel.AuthUiState.Success -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            else -> {}
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ByajPay",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        when {
            showNameScreen -> {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Name (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.verifyOtp(phoneNumber, otp, name, businessName)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                }
            }
            showOtpScreen -> {
                Text(
                    text = "Enter OTP sent to $phoneNumber",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it },
                    label = { Text("OTP") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.verifyOtp(phoneNumber, otp)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify OTP")
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { showOtpScreen = false }) {
                    Text("Change Phone Number")
                }
            }
            else -> {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10) phoneNumber = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (phoneNumber.length == 10) {
                            viewModel.sendOtp(phoneNumber)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = phoneNumber.length == 10 && uiState !is com.byajpay.app.ui.viewmodel.AuthUiState.Loading
                ) {
                    if (uiState is com.byajpay.app.ui.viewmodel.AuthUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Send OTP")
                    }
                }
            }
        }
        
        if (uiState is com.byajpay.app.ui.viewmodel.AuthUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (uiState as com.byajpay.app.ui.viewmodel.AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

