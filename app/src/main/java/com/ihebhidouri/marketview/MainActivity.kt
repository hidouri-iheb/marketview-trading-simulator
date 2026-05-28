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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ihebhidouri.marketview.ui.navigation.Routes
import com.ihebhidouri.marketview.ui.screens.HomeScreen
import com.ihebhidouri.marketview.ui.theme.MarketTextSecondary
import com.ihebhidouri.marketview.ui.theme.MarketViewTheme
import com.ihebhidouri.marketview.ui.navigation.BottomNavItem
import com.ihebhidouri.marketview.ui.screens.WatchlistScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ihebhidouri.marketview.models.ThemeMode
import com.ihebhidouri.marketview.ui.screens.SettingsScreen
import com.ihebhidouri.marketview.viewmodels.SettingsViewModel
import androidx.compose.ui.platform.LocalContext
import com.ihebhidouri.marketview.viewmodels.MarketViewViewModelFactory
import com.ihebhidouri.marketview.viewmodels.StockViewModel
import com.ihebhidouri.marketview.viewmodels.WatchlistViewModel
import com.ihebhidouri.marketview.ui.screens.PortfolioScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MarketViewApp()
        }
    }
}

@Composable
fun MarketViewApp() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as MarketViewApplication

    val viewModelFactory = MarketViewViewModelFactory(
        stockRepository = app.stockRepository,
        watchlistRepository = app.watchlistRepository,
        themeRepository = app.themePreferencesRepository
    )

    val stockViewModel: StockViewModel = viewModel(factory = viewModelFactory)
    val watchlistViewModel: WatchlistViewModel = viewModel(factory = viewModelFactory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)

    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val watchlistState by watchlistViewModel.uiState.collectAsStateWithLifecycle()
    val stockState by stockViewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by stockViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by stockViewModel.searchResults.collectAsStateWithLifecycle()
    val selectedStock by stockViewModel.selectedStock.collectAsStateWithLifecycle()
    val isCardLoading by stockViewModel.isCardLoading.collectAsStateWithLifecycle()
    val isDarkTheme = settingsState.themeMode == ThemeMode.DARK

    MarketViewTheme(darkTheme = isDarkTheme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                MarketViewBottomBar(navController = navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.HOME) {
                    HomeScreen(
                        uiState = stockState,
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        selectedStock = selectedStock,
                        isCardLoading = isCardLoading,
                        onSearchQueryChange = stockViewModel::onSearchQueryChange,
                        onStockSelected = stockViewModel::onStockSelected,
                        onDismissCard = stockViewModel::onDismissCard,
                        onAddToWatchlist = watchlistViewModel::addStockFromMarket,
                        onRetryLoadStocks = stockViewModel::loadStocks
                    )
                }
                composable(Routes.WATCHLIST) {
                    WatchlistScreen(
                        uiState = watchlistState,
                        onRemoveStock = watchlistViewModel::removeStock
                    )
                }
                composable(Routes.PORTFOLIO) {
                    PortfolioScreen()
                }
                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        themeMode = settingsState.themeMode,
                        onThemeModeChange = settingsViewModel::setThemeMode
                    )
                }
            }
        }
    }
}

@Composable
fun MarketViewBottomBar(navController: NavHostController) {
    val navItems = listOf(
        BottomNavItem("Home", Routes.HOME, Icons.Default.Home),
        BottomNavItem("Watchlist", Routes.WATCHLIST, Icons.Default.Bookmark),
        BottomNavItem("Portfolio", Routes.PORTFOLIO, Icons.Default.Assessment),
        BottomNavItem("Settings", Routes.SETTINGS, Icons.Default.Settings)
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors = bottomBarColors()
            )
        }
    }
}

@Composable
private fun bottomBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
    unselectedIconColor = MarketTextSecondary,
    unselectedTextColor = MarketTextSecondary
)