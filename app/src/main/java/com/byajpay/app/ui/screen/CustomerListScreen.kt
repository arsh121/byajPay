package com.byajpay.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.byajpay.app.data.model.Customer
import androidx.compose.ui.draw.clip
import com.byajpay.app.ui.components.AppTopBar
import com.byajpay.app.ui.theme.*
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
            AppTopBar(
                title = "Customers",
                navController = navController
            )
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
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = BorderColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                singleLine = true
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightGrey
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreyText,
                        fontSize = 13.sp
                    )
                }
            }
            Text(
                text = "₹${NumberFormat.getNumberInstance(Locale.US).format(outstandingBalance)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        }
    }
}

