package com.ihebhidouri.marketview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ihebhidouri.marketview.ui.screens.HomeScreen
import com.ihebhidouri.marketview.ui.screens.PlaceholderScreen
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme

enum class Screen {
    HOME,
    WATCHLIST,
    PORTFOLIO,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MarketViewTheme {
                MarketViewApp()
            }
        }
    }
}

@Composable
fun MarketViewApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    Scaffold(
        containerColor = Color(0xFF070A18),
        bottomBar = {
            MarketViewBottomBar(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        }
    ) { innerPadding ->
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                modifier = Modifier.padding(innerPadding)
            )

            Screen.WATCHLIST -> PlaceholderScreen(
                title = "Watchlist",
                subtitle = "Your saved stocks will appear here.",
                modifier = Modifier.padding(innerPadding)
            )

            Screen.PORTFOLIO -> PlaceholderScreen(
                title = "Portfolio",
                subtitle = "Portfolio tracking will be added later.",
                modifier = Modifier.padding(innerPadding)
            )

            Screen.SETTINGS -> PlaceholderScreen(
                title = "Settings",
                subtitle = "App preferences will be configured here.",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun MarketViewBottomBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF070A18),
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.HOME,
            onClick = { onScreenSelected(Screen.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            colors = bottomBarColors()
        )

        NavigationBarItem(
            selected = currentScreen == Screen.WATCHLIST,
            onClick = { onScreenSelected(Screen.WATCHLIST) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Watchlist"
                )
            },
            label = { Text("Watchlist") },
            colors = bottomBarColors()
        )

        NavigationBarItem(
            selected = currentScreen == Screen.PORTFOLIO,
            onClick = { onScreenSelected(Screen.PORTFOLIO) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Portfolio"
                )
            },
            label = { Text("Portfolio") },
            colors = bottomBarColors()
        )

        NavigationBarItem(
            selected = currentScreen == Screen.SETTINGS,
            onClick = { onScreenSelected(Screen.SETTINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") },
            colors = bottomBarColors()
        )
    }
}

@Composable
private fun bottomBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color(0xFF18E6FF),
    selectedTextColor = Color(0xFF18E6FF),
    indicatorColor = Color(0x3318E6FF),
    unselectedIconColor = Color(0xFF68749C),
    unselectedTextColor = Color(0xFF68749C)
)