package com.ihebhidouri.marketview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.ihebhidouri.marketview.viewmodel.SearchUiState
import com.ihebhidouri.marketview.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MarketViewTheme {
                MarketViewApp()
            }
        }
    }
}

enum class Screen {
    HOME,
    WATCHLIST,
    PORTFOLIO,
    SETTINGS
}

@Composable
fun MarketViewApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF111827)
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.HOME,
                    onClick = { currentScreen = Screen.HOME },
                    icon = { Text("H", color = Color.White) },
                    label = { Text("Home", color = Color.White) }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.WATCHLIST,
                    onClick = { currentScreen = Screen.WATCHLIST },
                    icon = { Text("W", color = Color.White) },
                    label = { Text("Watchlist", color = Color.White) }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.PORTFOLIO,
                    onClick = { currentScreen = Screen.PORTFOLIO },
                    icon = { Text("P", color = Color.White) },
                    label = { Text("Portfolio", color = Color.White) }
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.SETTINGS,
                    onClick = { currentScreen = Screen.SETTINGS },
                    icon = { Text("S", color = Color.White) },
                    label = { Text("Settings", color = Color.White) }
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            Screen.HOME -> HomeScreen(Modifier.padding(innerPadding))
            Screen.WATCHLIST -> ScreenContent("Watchlist", Modifier.padding(innerPadding))
            Screen.PORTFOLIO -> ScreenContent("Portfolio", Modifier.padding(innerPadding))
            Screen.SETTINGS -> ScreenContent("Settings", Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun ScreenContent(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by searchViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(20.dp)
    ) {
        Text(
            text = "MarketView API Test",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            text = "Testing stock search through ViewModel + Repository",
            color = Color.Gray,
            modifier = Modifier.padding(top = 6.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Enter stock symbol or company") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )

        Button(
            onClick = {
                searchViewModel.searchStocks(
                    query = searchQuery,
                    apiKey = "HIDDEN_API_KEY" // Real key is stored locally and not committed
                )
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Search")
        }

        when (val state = uiState) {
            SearchUiState.Idle -> {
                Text(
                    text = "Search for stocks like Tesla, Apple, or Amazon.",
                    color = Color.White,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

            SearchUiState.Loading -> {
                Text(
                    text = "Loading...",
                    color = Color.White,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

            is SearchUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

            is SearchUiState.Success -> {
                Text(
                    text = "Results",
                    color = Color.White,
                    modifier = Modifier.padding(top = 20.dp)
                )

                state.results.forEach { stock ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF161B22)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stock.symbol ?: "Unknown symbol",
                                color = Color.White
                            )

                            Text(
                                text = stock.name ?: "Unknown company",
                                color = Color.Gray
                            )

                            Text(
                                text = "${stock.country ?: "Unknown country"} · ${stock.currency ?: "Unknown currency"}",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}