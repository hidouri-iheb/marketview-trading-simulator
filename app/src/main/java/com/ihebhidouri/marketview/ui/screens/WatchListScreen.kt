package com.ihebhidouri.marketview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ihebhidouri.marketview.models.PortfolioSummary
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.viewmodels.WatchlistUiState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material.icons.Icons
import com.ihebhidouri.marketview.ui.components.ConfirmDialog
import com.ihebhidouri.marketview.ui.components.StockRow
import com.ihebhidouri.marketview.ui.components.TradeDialog

@Composable
fun WatchlistScreen(
    uiState: WatchlistUiState,
    onRemoveStock: (String) -> Unit,
    portfolios: List<PortfolioSummary>,
    onOpenTrade: (Long, String, String, String, Double, Double, Double?, Double?) -> Unit
) {
    var tradeStock by remember { mutableStateOf<Stock?>(null) }
    var stockToRemove by remember { mutableStateOf<String?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Watchlist",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your tracked assets with live prices.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.stocks.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(uiState.stocks, key = { it.symbol }) { stock ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        stockToRemove = stock.symbol
                                        false
                                    } else false
                                }
                            )
                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val color by animateColorAsState(
                                        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        animationSpec = tween(200),
                                        label = "swipe_color"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                    }
                                },
                                enableDismissFromStartToEnd = false
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    StockRow(
                                        stock = stock,
                                        onTrade = { tradeStock = stock }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        stockToRemove?.let { symbol ->
            ConfirmDialog(
                title = "Remove Stock",
                message = "Remove $symbol from your watchlist?",
                confirmText = "Remove",
                isDestructive = true,
                onConfirm = { onRemoveStock(symbol) },
                onDismiss = { stockToRemove = null }
            )
        }
        tradeStock?.let { stock ->
            TradeDialog(
                portfolios = portfolios,
                watchlistStocks = uiState.stocks,
                preSelectedStock = stock,
                onDismiss = { tradeStock = null },
                onConfirm = { portfolioId, symbol, name, type, size, entryPrice, tp, sl ->
                    onOpenTrade(portfolioId, symbol, name, type, size, entryPrice, tp, sl)
                    tradeStock = null
                }
            )
        }
    }
}