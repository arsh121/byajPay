package com.byajpay.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.compose.material.icons.filled.Person
import com.byajpay.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = true,
    showHomeButton: Boolean = true,
    showProfileButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val canGoBack = navController.previousBackStackEntry != null
    
    TopAppBar(
        title = { 
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )
        },
        navigationIcon = {
            if (showBackButton && canGoBack) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        },
        actions = {
            if (showProfileButton) {
                IconButton(
                    onClick = {
                        navController.navigate("profile")
                    }
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
            if (showHomeButton) {
                IconButton(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BrandPrimary,
            titleContentColor = androidx.compose.ui.graphics.Color.White,
            navigationIconContentColor = androidx.compose.ui.graphics.Color.White,
            actionIconContentColor = androidx.compose.ui.graphics.Color.White
        ),
        modifier = modifier
    )
}

