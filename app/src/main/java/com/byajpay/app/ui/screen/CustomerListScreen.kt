package com.byajpay.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.byajpay.app.data.model.Customer
import com.byajpay.app.ui.viewmodel.CustomerListViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    navController: NavController,
    viewModel: CustomerListViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(searchQuery) {
        viewModel.updateSearchQuery(searchQuery)
    }
    
    val customersList by viewModel.customersWithBalance.collectAsState(initial = emptyList())
    val balancesMap = remember { mutableStateMapOf<String, Double>() }
    
    // Calculate balances for each customer
    LaunchedEffect(customersList) {
        customersList.forEach { customer ->
            if (!balancesMap.containsKey(customer.id)) {
                val balance = viewModel.getCustomerBalance(customer.id)
                balancesMap[customer.id] = balance
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Customers") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_customer") }
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search customers") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(customersList) { customer ->
                    CustomerItem(
                        customer = customer,
                        outstandingBalance = balancesMap[customer.id] ?: 0.0,
                        onClick = {
                            navController.navigate("customer_detail/${customer.id}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerItem(
    customer: Customer,
    outstandingBalance: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (customer.phone != null) {
                    Text(
                        text = customer.phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "₹${NumberFormat.getNumberInstance(Locale.US).format(outstandingBalance)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

