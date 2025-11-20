package com.byajpay.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.byajpay.app.data.model.Transaction
import com.byajpay.app.data.model.TransactionType
import com.byajpay.app.ui.components.AppTopBar
import com.byajpay.app.ui.theme.*
import com.byajpay.app.ui.viewmodel.CustomerDetailViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    navController: NavController,
    customerId: String,
    viewModel: CustomerDetailViewModel = hiltViewModel()
) {
    val customer by viewModel.customer.collectAsState()
    val outstandingBalance by viewModel.outstandingBalance.collectAsState()
    
    LaunchedEffect(customerId) {
        viewModel.loadCustomer(customerId)
    }
    
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    
    // Store customer in a local variable to enable smart cast
    val currentCustomer = customer
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = currentCustomer?.name ?: "Customer",
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_transaction/$customerId") },
                containerColor = BrandPrimary,
                contentColor = androidx.compose.ui.graphics.Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Customer Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BrandSecondary
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = currentCustomer?.name ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    currentCustomer?.phone?.let { phone ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GreyText
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Outstanding Balance",
                            style = MaterialTheme.typography.titleMedium,
                            color = BrandPrimary
                        )
                        Text(
                            text = "₹${NumberFormat.getNumberInstance(Locale.US).format(outstandingBalance)}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    }
                }
            }
            
            // Ledger Book
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    // Ledger Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ledger",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Amount",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = GreyText
                        )
                    }
                    
                    Divider(thickness = 1.dp, color = BorderColor)
                    
                    // Ledger Entries
                    if (transactions.isEmpty()) {
                        Text(
                            text = "No transactions yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = GreyText,
                            modifier = Modifier.padding(20.dp)
                        )
                    } else {
                        transactions.forEachIndexed { index, transaction ->
                            LedgerRow(transaction = transaction)
                            if (index < transactions.size - 1) {
                                Divider(thickness = 0.5.dp, color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LedgerRow(transaction: Transaction) {
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type indicator - Credit = Red, Debit = Green
            Text(
                text = if (transaction.isInterestEntry) {
                    "INT"
                } else if (transaction.type == TransactionType.CREDIT) {
                    "CR"
                } else {
                    "DR"
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (transaction.type == TransactionType.CREDIT) {
                            WarningRed
                        } else {
                            SuccessGreen
                        }
                    )
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateFormat.format(Date(transaction.date)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkText,
                    fontSize = 14.sp
                )
                if (transaction.notes != null) {
                    Text(
                        text = transaction.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = GreyText,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        Text(
            text = "${if (transaction.type == TransactionType.CREDIT) "+" else "-"}₹${NumberFormat.getNumberInstance(Locale.US).format(transaction.amount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == TransactionType.CREDIT) {
                WarningRed
            } else {
                SuccessGreen
            },
            fontSize = 15.sp
        )
    }
}

