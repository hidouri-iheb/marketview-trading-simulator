package com.ihebhidouri.marketview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ihebhidouri.marketview.R
import com.ihebhidouri.marketview.data.SearchableStock
import com.ihebhidouri.marketview.models.PortfolioSummary
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.viewmodels.StockUiState
import com.ihebhidouri.marketview.viewmodels.TradeWithPnL
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import com.ihebhidouri.marketview.ui.components.ShimmerStockList
import com.ihebhidouri.marketview.ui.components.StockRow
import com.ihebhidouri.marketview.ui.components.UserAvatar
import com.ihebhidouri.marketview.ui.theme.RankGold
import com.ihebhidouri.marketview.ui.theme.RankSilver
import com.ihebhidouri.marketview.ui.theme.RankBronze


@Composable
fun HomeScreen(
    uiState: StockUiState,
    searchQuery: String,
    searchResults: List<SearchableStock>,
    selectedStock: Stock?,
    isCardLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onStockSelected: (String) -> Unit,
    onDismissCard: () -> Unit,
    onAddToWatchlist: (Stock) -> Unit,
    onRetryLoadStocks: () -> Unit,
    openTrades: List<TradeWithPnL>,
    leaderboard: List<PortfolioSummary>,
    displayName: String?,
    onNavigateToPortfolio: () -> Unit,
    onTradeClick: (Long) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HomeHeader(displayName = displayName)

            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )

            if (uiState.error != null) {
                ErrorSection(
                    onRetry = onRetryLoadStocks
                )
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(400)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400)
                )
            ) {
                LeaderboardSection(leaderboard = leaderboard.take(3))
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(500)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(500)
                )
            ) {
                OpenTradesSection(
                    openTrades = openTrades.take(3),
                    onNavigateToPortfolio = onNavigateToPortfolio,
                    onTradeClick = onTradeClick
                )
            }

            if (uiState.isLoading) {
                ShimmerStockList()
            } else {
                AnimatedVisibility(
                    visible = uiState.trending.isNotEmpty(),
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(600)
                    )
                ) {
                    TrendingSection(
                        stocks = uiState.trending.take(5),
                        onStockClick = onStockSelected
                    )
                }
            }
        }

        // Search dropdown
        if (searchResults.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 136.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    searchResults.forEach { stock ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onStockSelected(stock.symbol) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stock.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stock.symbol,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        // Loading overlay
        if (isCardLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        // Stock detail dialog
        selectedStock?.let { stock ->
            val dialogScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(300),
                label = "dialog_scale"
            )
            Dialog(onDismissRequest = onDismissCard) {
                StockDetailCard(
                    stock = stock,
                    onDismiss = onDismissCard,
                    onAddToWatchlist = {
                        onAddToWatchlist(stock)
                        onDismissCard()
                    },
                    modifier = Modifier.graphicsLayer {
                        scaleX = dialogScale
                        scaleY = dialogScale
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(displayName: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (displayName != null) "Welcome back, $displayName"
                else stringResource(R.string.home_welcome),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_marketview_logo),
                    contentDescription = "MarketView Logo",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        UserAvatar(displayName = displayName)
    }
}
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.home_search_placeholder),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ErrorSection(onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_load_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: @Composable (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon?.invoke()
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun LeaderboardSection(leaderboard: List<PortfolioSummary>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(
            title = "Portfolio Leaderboard",
            icon = {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            if (leaderboard.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create portfolios to see the leaderboard.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    leaderboard.forEachIndexed { index, summary ->
                        val rankColor = when (index) {
                            0 -> RankGold
                            1 -> RankSilver
                            2 -> RankBronze
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = rankColor
                                )
                                Column {
                                    Text(
                                        text = summary.ownerName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${summary.portfolio.name} · ${summary.portfolio.style}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$${String.format("%.2f", summary.currentBalance)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${if (summary.pnlPercent >= 0) "+" else ""}${String.format("%.2f", summary.pnlPercent)}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (summary.pnlPercent >= 0) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OpenTradesSection(
    openTrades: List<TradeWithPnL>,
    onNavigateToPortfolio: () -> Unit,
    onTradeClick: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title = "Open Trades")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            if (openTrades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onNavigateToPortfolio,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Start Trading",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    openTrades.forEach { tradeWithPnL ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTradeClick(tradeWithPnL.trade.portfolioId) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tradeWithPnL.trade.symbol,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = tradeWithPnL.trade.type,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (tradeWithPnL.trade.type == "BUY") MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = "${if (tradeWithPnL.pnl >= 0) "+" else ""}$${String.format("%.2f", tradeWithPnL.pnl)}",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (tradeWithPnL.pnl >= 0) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendingSection(stocks: List<Stock>, onStockClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(title = "Top Volatile Assets")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                stocks.forEach { stock ->
                    Box(
                        modifier = Modifier.clickable { onStockClick(stock.symbol) }
                    ) {
                        StockRow(stock = stock)
                    }
                }
            }
        }
    }
}

@Composable
private fun StockDetailCard(
    stock: Stock,
    onDismiss: () -> Unit,
    onAddToWatchlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stock.symbol,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = stock.name,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = stock.formattedPrice,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displaySmall
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stock.formattedChange,
                    color = if (stock.isPositive) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "(${stock.formattedChangePercent})",
                    color = if (stock.isPositive) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow("Open", String.format("%.2f", stock.open))
                    DetailRow("High", String.format("%.2f", stock.high))
                    DetailRow("Low", String.format("%.2f", stock.low))
                    DetailRow("Prev Close", String.format("%.2f", stock.previousClose))
                    DetailRow("Volume", String.format("%,d", stock.volume))
                }
            }

            Button(
                onClick = { onAddToWatchlist() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.add_to_watchlist),
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}