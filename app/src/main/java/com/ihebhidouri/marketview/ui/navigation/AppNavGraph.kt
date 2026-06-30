package com.ihebhidouri.marketview.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
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
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

// Transition constants
private const val TRANSITION_DURATION = 300

private fun defaultEnterTransition(): EnterTransition =
    fadeIn(animationSpec = tween(TRANSITION_DURATION))

private fun defaultExitTransition(): ExitTransition =
    fadeOut(animationSpec = tween(TRANSITION_DURATION))

private fun slideInFromRight(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { it / 3 },
        animationSpec = tween(TRANSITION_DURATION)
    ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))

private fun slideOutToRight(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { it / 3 },
        animationSpec = tween(TRANSITION_DURATION)
    ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))

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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            MarketViewBottomBar(navController = navController)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() }
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
                    onAddToWatchlist = { stock ->
                        watchlistViewModel.addStockFromMarket(stock)
                        showSnackbar("${stock.symbol} added to watchlist")
                        navController.navigate(Routes.WATCHLIST) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRetryLoadStocks = stockViewModel::loadStocks,
                    openTrades = openTrades,
                    leaderboard = leaderboardState.portfolios
                        .sortedByDescending { it.pnlPercent },
                    displayName = authState.displayName,
                    onNavigateToPortfolio = {
                        navController.navigate(Routes.PORTFOLIO) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onTradeClick = { portfolioId ->
                        portfolioViewModel.selectPortfolio(portfolioId)
                        navController.navigate(Routes.PORTFOLIO_DETAIL)
                    }
                )
            }
            composable(Routes.WATCHLIST) {
                WatchlistScreen(
                    uiState = watchlistState,
                    onRemoveStock = { symbol ->
                        watchlistViewModel.removeStock(symbol)
                        showSnackbar("$symbol removed from watchlist")
                    },
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
                        showSnackbar("$type trade opened on $symbol")
                        portfolioViewModel.selectPortfolio(portfolioId)
                        navController.navigate(Routes.PORTFOLIO_DETAIL)
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
                    onCreatePortfolio = { name, style, balance ->
                        portfolioViewModel.createPortfolio(name, style, balance) { newId ->
                            portfolioViewModel.selectPortfolio(newId)
                            navController.navigate(Routes.PORTFOLIO_DETAIL)
                        }
                        showSnackbar("Portfolio created")
                    },
                    onDeletePortfolio = { id ->
                        portfolioViewModel.deletePortfolio(id)
                        showSnackbar("Portfolio deleted")
                    }
                )
            }
            composable(
                Routes.PORTFOLIO_DETAIL,
                enterTransition = { slideInFromRight() },
                exitTransition = { defaultExitTransition() },
                popEnterTransition = { defaultEnterTransition() },
                popExitTransition = { slideOutToRight() }
            ) {
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
                        showSnackbar("$type trade opened on $symbol")
                    },
                    onCloseTrade = { tradeId, price ->
                        portfolioViewModel.closeTrade(tradeId, price)
                        showSnackbar("Trade closed")
                    },
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    themeMode = settingsState.themeMode,
                    onThemeModeChange = settingsViewModel::setThemeMode,
                    onTradeHistoryClick = { navController.navigate(Routes.TRADE_HISTORY) },
                    onLogout = authViewModel::logout,
                    displayName = authState.displayName,
                    email = authViewModel.currentUserEmail,

                )
            }
            composable(
                Routes.TRADE_HISTORY,
                enterTransition = { slideInFromRight() },
                exitTransition = { defaultExitTransition() },
                popEnterTransition = { defaultEnterTransition() },
                popExitTransition = { slideOutToRight() }
            ) {
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
        BottomNavItem("Trading", Routes.PORTFOLIO, Icons.Default.Assessment),
        BottomNavItem("Settings", Routes.SETTINGS, Icons.Default.Settings)
    )


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Column {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
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
}

@Composable
private fun bottomBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)