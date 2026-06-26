package com.ihebhidouri.marketview.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ihebhidouri.marketview.models.PortfolioSummary
import com.ihebhidouri.marketview.models.Stock

@Composable
fun TradeDialog(
    portfolios: List<PortfolioSummary>,
    watchlistStocks: List<Stock>,
    preSelectedPortfolioId: Long? = null,
    preSelectedStock: Stock? = null,
    onDismiss: () -> Unit,
    onConfirm: (portfolioId: Long, symbol: String, name: String, type: String, size: Double, entryPrice: Double, tp: Double?, sl: Double?) -> Unit
) {
    var selectedPortfolio by remember {
        mutableStateOf(portfolios.find { it.portfolio.id == preSelectedPortfolioId })
    }
    var selectedStock by remember { mutableStateOf(preSelectedStock) }
    var tradeType by remember { mutableStateOf("BUY") }
    var sizeText by remember { mutableStateOf("") }
    var tp by remember { mutableStateOf("") }
    var sl by remember { mutableStateOf("") }
    var sizeError by remember { mutableStateOf<String?>(null) }

    val availableBalance = selectedPortfolio?.currentBalance ?: 0.0

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Open Trade",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (portfolios.isEmpty()) {
                    Text(
                        text = "Create a portfolio first.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    // Portfolio selector
                    Text(
                        text = "Portfolio",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        portfolios.forEach { summary ->
                            FilterChip(
                                selected = selectedPortfolio?.portfolio?.id == summary.portfolio.id,
                                onClick = { selectedPortfolio = summary },
                                label = {
                                    Text(
                                        "${summary.portfolio.name} — $${String.format("%.0f", summary.currentBalance)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }

                    // Stock selector
                    if (preSelectedStock == null) {
                        if (watchlistStocks.isEmpty()) {
                            Text(
                                text = "Add stocks to your watchlist first.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(
                                text = "Select Asset",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                watchlistStocks.forEach { stock ->
                                    FilterChip(
                                        selected = selectedStock?.symbol == stock.symbol,
                                        onClick = { selectedStock = stock },
                                        label = {
                                            Text("${stock.symbol} — $${String.format("%.2f", stock.price)}")
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "${preSelectedStock.symbol} — $${String.format("%.2f", preSelectedStock.price)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // BUY / SELL
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = tradeType == "BUY",
                            onClick = { tradeType = "BUY" },
                            label = { Text("BUY") }
                        )
                        FilterChip(
                            selected = tradeType == "SELL",
                            onClick = { tradeType = "SELL" },
                            label = { Text("SELL") }
                        )
                    }

                    // Size
                    OutlinedTextField(
                        value = sizeText,
                        onValueChange = {
                            sizeText = it
                            sizeError = null
                        },
                        label = { Text("Size (units)") },
                        singleLine = true,
                        isError = sizeError != null,
                        supportingText = sizeError?.let { error ->
                            { Text(text = error, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // TP / SL
                    OutlinedTextField(
                        value = tp,
                        onValueChange = { tp = it },
                        label = { Text("Take Profit (optional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = sl,
                        onValueChange = { sl = it },
                        label = { Text("Stop Loss (optional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Submit
                    Button(
                        onClick = {
                            val portfolio = selectedPortfolio ?: return@Button
                            val stock = selectedStock ?: return@Button
                            val sizeVal = sizeText.toDoubleOrNull() ?: return@Button

                            val cost = sizeVal * stock.price
                            if (cost > portfolio.currentBalance) {
                                sizeError = "Cost ($${String.format("%.2f", cost)}) exceeds balance ($${String.format("%.2f", portfolio.currentBalance)})"
                                return@Button
                            }

                            onConfirm(
                                portfolio.portfolio.id,
                                stock.symbol,
                                stock.name,
                                tradeType,
                                sizeVal,
                                stock.price,
                                tp.toDoubleOrNull(),
                                sl.toDoubleOrNull()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        enabled = selectedPortfolio != null && selectedStock != null && sizeText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tradeType == "BUY") MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = tradeType,
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}