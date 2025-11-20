package com.byajpay.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.byajpay.app.data.model.InterestCycle
import com.byajpay.app.data.model.TransactionType
import com.byajpay.app.ui.viewmodel.AddTransactionViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    customerId: String,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var notes by remember { mutableStateOf("") }
    var chargeInterest by remember { mutableStateOf(false) }
    var interestRate by remember { mutableStateOf("") }
    var selectedCycle by remember { mutableStateOf<InterestCycle?>(null) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is com.byajpay.app.ui.viewmodel.TransactionUiState.Success) {
            navController.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )
            
            // Transaction Type
            Text("Type *", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TransactionType.values().forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            if (type != TransactionType.CREDIT) {
                                chargeInterest = false
                            }
                        },
                        label = { Text(type.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )
            
            // Interest Section (only for Credit)
            if (selectedType == TransactionType.CREDIT) {
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = chargeInterest,
                        onCheckedChange = { chargeInterest = it }
                    )
                    Text("Charge Interest")
                }
                
                if (chargeInterest) {
                    OutlinedTextField(
                        value = interestRate,
                        onValueChange = { interestRate = it },
                        label = { Text("Interest Rate (%) *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true
                    )
                    
                    Text("Interest Cycle *", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InterestCycle.values().forEach { cycle ->
                            FilterChip(
                                selected = selectedCycle == cycle,
                                onClick = { selectedCycle = cycle },
                                label = { Text(cycle.name) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && selectedType != null) {
                        val rate = if (chargeInterest) interestRate.toDoubleOrNull() else null
                        val cycle = if (chargeInterest) selectedCycle else null
                        
                        viewModel.addTransaction(
                            customerId = customerId,
                            amount = amountValue,
                            type = selectedType!!,
                            date = selectedDate,
                            notes = notes.takeIf { it.isNotBlank() },
                            chargeInterest = chargeInterest && rate != null && cycle != null,
                            interestRate = rate,
                            interestCycle = cycle
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.toDoubleOrNull() != null &&
                        selectedType != null &&
                        (!chargeInterest || (interestRate.toDoubleOrNull() != null && selectedCycle != null)) &&
                        uiState !is com.byajpay.app.ui.viewmodel.TransactionUiState.Loading
            ) {
                if (uiState is com.byajpay.app.ui.viewmodel.TransactionUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Add Transaction")
                }
            }
            
            if (uiState is com.byajpay.app.ui.viewmodel.TransactionUiState.Error) {
                Text(
                    text = (uiState as com.byajpay.app.ui.viewmodel.TransactionUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

