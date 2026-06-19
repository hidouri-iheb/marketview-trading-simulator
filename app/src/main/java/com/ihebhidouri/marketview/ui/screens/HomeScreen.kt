package com.ihebhidouri.marketview.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ihebhidouri.marketview.models.Stock
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.stringResource
import com.ihebhidouri.marketview.R
import com.ihebhidouri.marketview.viewmodels.StockUiState
import com.ihebhidouri.marketview.data.SearchableStock
import com.ihebhidouri.marketview.viewmodels.TradeWithPnL




@Composable
fun HomeScreen(
    uiState: StockUiState,
    searchQuery: String,
    searchResults: List<SearchableStock>,
    selectedStock: Stock?,
    isCardLoading: Boolean,
    openTrades: List<TradeWithPnL>,
    onSearchQueryChange: (String) -> Unit,
    onStockSelected: (String) -> Unit,
    onDismissCard: () -> Unit,
    onAddToWatchlist: (Stock) -> Unit,
    onRetryLoadStocks: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeHeader()
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )
            OpenTradesHolder(openTrades = openTrades)

            if (uiState.error != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_load_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = onRetryLoadStocks
                    ) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
            }



            if (uiState.trending.isNotEmpty()) {
                TrendingHolder(stocks = uiState.trending)
            }
        }

        if (searchResults.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 148.dp),
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
                                .clickable { onStockSelected(stock.symbol) }
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stock.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stock.symbol,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

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

        selectedStock?.let { stock ->
            Dialog(onDismissRequest = onDismissCard) {
                StockDetailCard(
                    stock = stock,
                    onDismiss = onDismissCard,
                    onAddToWatchlist = {
                        onAddToWatchlist(stock)
                        onDismissCard()
                    }
                )
            }
        }
    }
}
@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_welcome),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                stringResource(R.string.app_name) ,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
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
private fun OpenTradesHolder(openTrades: List<TradeWithPnL>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Open Trades",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )

        if (openTrades.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No open trades across your portfolios.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    openTrades.forEach { tradeWithPnL ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
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
private fun MarketPulseMiniChart() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val heights = listOf(18, 24, 20, 34, 28, 42, 36, 48, 40, 50)

        heights.forEach { height ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(height.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun TrendingHolder(stocks: List<Stock>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Top Volatile Assets ",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = { }) {
                Text(
                    text = "See all",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                stocks.forEach { stock ->
                    StockPreviewRow(stock = stock)
                }
            }
        }
    }
}

@Composable
private fun StockPreviewRow(stock: Stock) {
    StockRow(
        stock = stock,
        chartIconSize = 32
    )
}
@Composable
private fun StockDetailCard(
    stock: Stock,
    onDismiss: () -> Unit,
    onAddToWatchlist: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(24.dp),
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
                text = "$${String.format("%.2f", stock.price)}",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displaySmall
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${if (stock.isPositive) "+" else ""}${String.format("%.2f", stock.change)}",
                    color = if (stock.isPositive) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "(${if (stock.isPositive) "+" else ""}${String.format("%.2f", stock.changePercent)}%)",
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