package com.byajpay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.byajpay.app.data.preferences.PreferencesManager
import com.byajpay.app.ui.navigation.NavGraph
import com.byajpay.app.ui.navigation.Screen
import com.byajpay.app.ui.theme.ByajPayTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ByajPayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = hiltViewModel()
    val isLoggedIn by viewModel.preferencesManager.isLoggedIn.collectAsState(initial = false)
    
    val startDestination = if (isLoggedIn) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }
    
    NavGraph(navController = navController, startDestination = startDestination)
}

@HiltViewModel
class MainViewModel @Inject constructor(
    val preferencesManager: PreferencesManager
) : ViewModel()


