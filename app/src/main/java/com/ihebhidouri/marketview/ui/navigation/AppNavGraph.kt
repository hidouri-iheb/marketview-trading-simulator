package com.ihebhidouri.marketview.ui.navigation

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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ihebhidouri.marketview.MarketViewApplication
import com.ihebhidouri.marketview.ui.screens.HomeScreen
import com.ihebhidouri.marketview.ui.screens.PortfolioDetailScreen
import com.ihebhidouri.marketview.ui.screens.PortfolioScreen
import com.ihebhidouri.marketview.ui.screens.SettingsScreen
import com.ihebhidouri.marketview.ui.screens.TradeHistoryScreen
import com.ihebhidouri.marketview.ui.screens.WatchlistScreen
import com.ihebhidouri.marketview.viewmodels.AuthUiState
import com.ihebhidouri.marketview.viewmodels.AuthViewModel
import com.ihebhidouri.marketview.viewmodels.MarketViewViewModelFactory
import com.ihebhidouri.marketview.viewmodels.PortfolioViewModel
import com.ihebhidouri.marketview.viewmodels.SettingsUiState
import com.ihebhidouri.marketview.viewmodels.SettingsViewModel
import com.ihebhidouri.marketview.viewmodels.StockViewModel
import com.ihebhidouri.marketview.viewmodels.WatchlistViewModel

@Composable
fun MainContent(
    viewModelFactory: MarketViewViewModelFactory,
    settingsState: SettingsUiState,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    authState: AuthUiState
) {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as MarketViewApplication
    val userId = app.authRepository.currentUser?.uid ?: ""
    val stockViewModel: StockViewModel = viewModel(key = "stock_$userId", factory = viewModelFactory)
    val watchlistViewModel: WatchlistViewModel = viewModel(key = "watchlist_$userId", factory = viewModelFactory)
    val portfolioViewModel: PortfolioViewModel = viewModel(key = "portfolio_$userId", factory = viewModelFactory)
    val watchlistState by watchlistViewModel.uiState.collectAsStateWithLifecycle()
    val stockState by stockViewModel.uiState.collectAsStateWithLifecycle()
    val portfolioListState by portfolioViewModel.listState.collectAsStateWithLifecycle()
    val leaderboardState by portfolioViewModel.leaderboardState.collectAsStateWithLifecycle()
    val portfolioDetailState by portfolioViewModel.detailState.collectAsStateWithLifecycle()
    val openTrades by portfolioViewModel.openTradesState.collectAsStateWithLifecycle()
    val tradeHistory by portfolioViewModel.historyState.collectAsStateWithLifecycle()

    val searchQuery by stockViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by stockViewModel.searchResults.collectAsStateWithLifecycle()
    val selectedStock by stockViewModel.selectedStock.collectAsStateWithLifecycle()
    val isCardLoading by stockViewModel.isCardLoading.collectAsStateWithLifecycle()

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
                    onRetryLoadStocks = stockViewModel::loadStocks,
                    openTrades = openTrades,
                    leaderboard = leaderboardState.portfolios
                        .sortedByDescending { it.pnlPercent },
                    displayName = authState.displayName,

                )
            }
            composable(Routes.WATCHLIST) {
                WatchlistScreen(
                    uiState = watchlistState,
                    onRemoveStock = watchlistViewModel::removeStock,
                    portfolios = portfolioListState.portfolios,
                    onOpenTrade = { portfolioId, symbol, name, type, size, entryPrice, tp, sl ->
                        portfolioViewModel.openTrade(
                            portfolioId = portfolioId,
                            symbol = symbol,
                            name = name,
                            type = type,
                            size = size,
                            leverage = 1.0,
                            entryPrice = entryPrice,
                            takeProfit = tp,
                            stopLoss = sl
                        )
                    }
                )
            }

            composable(Routes.PORTFOLIO) {
                PortfolioScreen(
                    uiState = portfolioListState,
                    onPortfolioClick = { id ->
                        portfolioViewModel.selectPortfolio(id)
                        navController.navigate(Routes.PORTFOLIO_DETAIL)
                    },
                    onCreatePortfolio = portfolioViewModel::createPortfolio,
                    onDeletePortfolio = portfolioViewModel::deletePortfolio
                )
            }
            composable(Routes.PORTFOLIO_DETAIL) {
                PortfolioDetailScreen(
                    uiState = portfolioDetailState,
                    watchlistStocks = watchlistState.stocks,
                    portfolios = portfolioListState.portfolios,
                    onBack = { navController.popBackStack() },
                    onOpenTrade = { portfolioId, symbol, name, type, size, entryPrice, tp, sl ->
                        portfolioViewModel.openTrade(
                            portfolioId = portfolioId,
                            symbol = symbol,
                            name = name,
                            type = type,
                            size = size,
                            leverage = 1.0,
                            entryPrice = entryPrice,
                            takeProfit = tp,
                            stopLoss = sl
                        )
                    },
                    onCloseTrade = portfolioViewModel::closeTrade,
                    onDeleteTrade = portfolioViewModel::deleteTrade
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    themeMode = settingsState.themeMode,
                    onThemeModeChange = settingsViewModel::setThemeMode,
                    onTradeHistoryClick = { navController.navigate(Routes.TRADE_HISTORY) },
                    onLogout = authViewModel::logout
                )
            }
            composable(Routes.TRADE_HISTORY) {
                TradeHistoryScreen(
                    trades = tradeHistory,
                    onBack = { navController.popBackStack() }
                )
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
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)