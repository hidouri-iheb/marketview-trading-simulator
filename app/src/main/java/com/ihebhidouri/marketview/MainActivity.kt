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
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(20.dp)
    ) {

        Text(
            text = "MarketView",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text(
            text = "Paper trading dashboard",
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161B22)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "🔎 Search Stocks",
                    color = Color.White
                )

                Text(
                    text = "Apple, Tesla, NVIDIA...",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        Text(
            text = "Trending",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(top = 28.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161B22)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AAPL", color = Color.White)
                Text("$228.45   ▲ 1.2%", color = Color(0xFF00C853))
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161B22)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TSLA", color = Color.White)
                Text("$341.80   ▼ 0.8%", color = Color.Red)
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161B22)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("NVDA", color = Color.White)
                Text("$129.10   ▲ 2.4%", color = Color(0xFF00C853))
            }
        }

        Text(
            text = "Quick Access",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(top = 28.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF21262D)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(
                text = " Open Watchlist",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}