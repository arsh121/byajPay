package com.byajpay.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.byajpay.app.data.model.TransactionType
import com.byajpay.app.ui.components.AppTopBar
import com.byajpay.app.ui.theme.*
import com.byajpay.app.ui.viewmodel.CustomerWithRecentTransactions
import com.byajpay.app.ui.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val customersWithRecentTransactions by viewModel.customersWithRecentTransactions.collectAsState(initial = emptyList())
    val balancesMap = remember { mutableStateMapOf<String, Double>() }
    
    // Calculate outstanding balances for each customer
    LaunchedEffect(customersWithRecentTransactions) {
        customersWithRecentTransactions.forEach { customerWithTransactions ->
            if (!balancesMap.containsKey(customerWithTransactions.customer.id)) {
                val balance = viewModel.calculateOutstandingBalance(customerWithTransactions.customer.id)
                balancesMap[customerWithTransactions.customer.id] = balance
            }
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "ByajPay",
                navController = navController,
                showBackButton = false,
                showHomeButton = false,
                showProfileButton = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_customer") },
                modifier = Modifier.padding(16.dp),
                containerColor = BrandPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Total Outstanding Card
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Outstanding",
                        style = MaterialTheme.typography.titleLarge,
                        color = BrandPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${NumberFormat.getNumberInstance(Locale.US).format(totalOutstanding)}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Quick Actions - Bigger buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    onClick = { navController.navigate("customer_list") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = BrandPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Customers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    }
                }
                
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    onClick = { navController.navigate("customer_list") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = BrandPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Add Entry",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Recent Transactions by Customer
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            
            if (customersWithRecentTransactions.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightGrey
                    )
                ) {
                    Text(
                        text = "No transactions yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = GreyText,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    customersWithRecentTransactions.forEach { customerWithTransactions ->
                        CustomerTransactionCard(
                            customerWithTransactions = customerWithTransactions,
                            outstandingBalance = balancesMap[customerWithTransactions.customer.id] ?: 0.0,
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerTransactionCard(
    customerWithTransactions: CustomerWithRecentTransactions,
    outstandingBalance: Double,
    navController: NavController,
    viewModel: HomeViewModel
) {
    val customer = customerWithTransactions.customer
    val transactions = customerWithTransactions.transactions
    val lastTransaction = transactions.firstOrNull()
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightGrey
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Collapsed view - Customer name, last transaction, total due
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Customer Name - Bold for scannability
                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    if (customer.phone != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = customer.phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = GreyText,
                            fontSize = 13.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Last Transaction
                    if (lastTransaction != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lastTransaction.isInterestEntry) {
                                    "INT"
                                } else if (lastTransaction.type == TransactionType.CREDIT) {
                                    "CR"
                                } else {
                                    "DR"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (lastTransaction.type == TransactionType.CREDIT) {
                                    Color.White
                                } else {
                                    Color.White
                                },
                                modifier = Modifier
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (lastTransaction.type == TransactionType.CREDIT) {
                                            WarningRed
                                        } else {
                                            SuccessGreen
                                        }
                                    )
                            )
                            Text(
                                text = dateFormat.format(Date(lastTransaction.date)),
                                style = MaterialTheme.typography.bodySmall,
                                color = GreyText,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${if (lastTransaction.type == TransactionType.CREDIT) "+" else "-"}₹${NumberFormat.getNumberInstance(Locale.US).format(lastTransaction.amount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (lastTransaction.type == TransactionType.CREDIT) {
                                    WarningRed
                                } else {
                                    SuccessGreen
                                },
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Total Due - Blue or Black (not red/green)
                    Text(
                        text = "₹${NumberFormat.getNumberInstance(Locale.US).format(outstandingBalance)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                    Text(
                        text = "Total Due",
                        style = MaterialTheme.typography.labelMedium,
                        color = GreyText,
                        fontSize = 12.sp
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.padding(top = 4.dp),
                        tint = GreyText
                    )
                }
            }
            
            // Expanded view - Last 3 transactions
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("customer_detail/${customer.id}")
                        }
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(thickness = 1.dp, color = BorderColor)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Last 3 Transactions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    transactions.forEachIndexed { index, transaction ->
                        LedgerEntry(
                            transaction = transaction,
                            dateFormat = dateFormat
                        )
                        if (index < transactions.size - 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LedgerEntry(
    transaction: com.byajpay.app.data.model.Transaction,
    dateFormat: SimpleDateFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Compact type indicator
            Text(
                text = if (transaction.isInterestEntry) {
                    "INT"
                } else if (transaction.type == TransactionType.CREDIT) {
                    "CR"
                } else {
                    "DR"
                },
                style = MaterialTheme.typography.labelSmall,
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
                    color = GreyText,
                    fontSize = 13.sp
                )
                if (transaction.notes != null) {
                    Text(
                        text = transaction.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = GreyText,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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

