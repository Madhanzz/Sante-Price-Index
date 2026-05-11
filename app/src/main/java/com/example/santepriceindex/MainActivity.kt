package com.example.santepriceindex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.santepriceindex.ui.theme.SantePriceIndexTheme
import com.example.santepriceindex.ui.screens.MainScreen
import com.example.santepriceindex.ui.screens.PriceWatchScreen
import com.example.santepriceindex.ui.screens.CalculatorScreen
import com.example.santepriceindex.ui.screens.PriceBoardScreen
import com.example.santepriceindex.ui.screens.TrendScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SantePriceIndexTheme {

                val currentScreen = androidx.compose.runtime.remember {
                    androidx.compose.runtime.mutableStateOf("home")
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if (currentScreen.value == "home") {
                        MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            onNavigate = { screen ->
                                currentScreen.value = screen
                            }
                        )
                    } else if (currentScreen.value == "pricewatch") {
                        PriceWatchScreen(
                            onBack = {
                                currentScreen.value = "home"
                            }
                        )
                    }
                    else if (currentScreen.value == "calculator") {
                        CalculatorScreen(
                            onBack = {
                                currentScreen.value = "home"
                            }
                        )
                    }
                    else if (currentScreen.value == "priceboard") {
                        PriceBoardScreen(
                            onBack = {
                                currentScreen.value = "home"
                            }
                        )
                    }

                    else if (currentScreen.value == "trends") {
                        TrendScreen(
                            onBack = {
                                currentScreen.value = "home"
                            }
                        )
                    }

                }
            }
        }
    }
}

