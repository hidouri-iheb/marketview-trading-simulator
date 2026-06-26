package com.ihebhidouri.marketview.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ihebhidouri.marketview.models.Stock
import com.ihebhidouri.marketview.viewmodels.WatchlistUiState
import com.ihebhidouri.marketview.models.PortfolioSummary
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@Composable
fun WatchlistScreen(
    uiState: WatchlistUiState,
    onRemoveStock: (String) -> Unit,
    portfolios: List<PortfolioSummary>,
    onOpenTrade: (Long, String, String, String, Double, Double, Double?, Double?) -> Unit
){
    var tradeStock by remember { mutableStateOf<Stock?>(null) }

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

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.stocks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No stocks yet. Search and add from Home.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(uiState.stocks, key = { it.symbol }) { stock ->
                            WatchlistStockRow(
                                stock = stock,
                                onRemove = { onRemoveStock(stock.symbol) },
                                onTrade = { tradeStock = stock }
                            )
                        }
                    }
                }
            }
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
@Composable
private fun WatchlistStockRow(
    stock: Stock,
    onRemove: () -> Unit,
    onTrade: () -> Unit
) {
    StockRow(
        stock = stock,
        showRemoveButton = true,
        onRemove = onRemove,
        onTrade = onTrade
    )
}