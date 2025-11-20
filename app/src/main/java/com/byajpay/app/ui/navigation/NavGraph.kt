package com.byajpay.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.byajpay.app.ui.screen.*

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.CustomerList.route) {
            CustomerListScreen(navController = navController)
        }
        composable(Screen.AddCustomer.route) {
            AddCustomerScreen(navController = navController)
        }
        composable("${Screen.CustomerDetail.route}/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerDetailScreen(
                navController = navController,
                customerId = customerId
            )
        }
        composable("${Screen.AddTransaction.route}/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            AddTransactionScreen(
                navController = navController,
                customerId = customerId
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object CustomerList : Screen("customer_list")
    object AddCustomer : Screen("add_customer")
    object CustomerDetail : Screen("customer_detail")
    object AddTransaction : Screen("add_transaction")
    object Profile : Screen("profile")
}

