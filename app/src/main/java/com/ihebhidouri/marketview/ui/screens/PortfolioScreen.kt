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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ihebhidouri.marketview.models.PortfolioSummary
import com.ihebhidouri.marketview.viewmodels.PortfolioListUiState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import com.ihebhidouri.marketview.ui.components.ConfirmDialog


@Composable
fun PortfolioScreen(
    uiState: PortfolioListUiState,
    onPortfolioClick: (Long) -> Unit,
    onCreatePortfolio: (String, String, Double) -> Unit,
    onDeletePortfolio: (Long) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var portfolioToDelete by remember { mutableStateOf<Long?>(null) }
    var fabVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { fabVisible = true }
    val fabScale by animateFloatAsState(
        targetValue = if (fabVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "fab"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Trading",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Manage your portfolios and track your trades.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.portfolios.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No portfolios yet",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to create your first portfolio.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.portfolios, key = { it.portfolio.id }) { summary ->
                        PortfolioCard(
                            summary = summary,
                            onClick = { onPortfolioClick(summary.portfolio.id) },
                            onDelete = { portfolioToDelete = summary.portfolio.id }
                        )
                    }

                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .graphicsLayer {
                    scaleX = fabScale
                    scaleY = fabScale
                },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Portfolio",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (showCreateDialog) {
            CreatePortfolioDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, style, balance ->
                    onCreatePortfolio(name, style, balance)
                    showCreateDialog = false
                }
            )
        }
        portfolioToDelete?.let { id ->
            ConfirmDialog(
                title = "Delete Portfolio",
                message = "This will permanently delete this portfolio and all its trades.",
                confirmText = "Delete",
                isDestructive = true,
                onConfirm = { onDeletePortfolio(id) },
                onDismiss = { portfolioToDelete = null }
            )
        }
    }
}

@Composable
private fun PortfolioCard(
    summary: PortfolioSummary,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val portfolio = summary.portfolio

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = portfolio.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = portfolio.style,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Balance",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format("%.2f", summary.currentBalance)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Column {
                        Text(
                            text = "P&L",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${if (summary.pnlPercent >= 0) "+" else ""}${String.format("%.2f", summary.pnlPercent)}%",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (summary.pnlPercent >= 0) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CreatePortfolioDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var strategy by remember { mutableStateOf("") }
    var selectedBalance by remember { mutableStateOf<Double?>(null) }
    val balanceOptions = listOf(5_000.0, 10_000.0, 50_000.0, 100_000.0)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Create Portfolio",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Starting Balance",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        balanceOptions.take(2).forEach { amount ->
                            FilterChip(
                                selected = selectedBalance == amount,
                                onClick = { selectedBalance = amount },
                                label = {
                                    Text(
                                        text = "$${String.format("%,.0f", amount)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        balanceOptions.drop(2).forEach { amount ->
                            FilterChip(
                                selected = selectedBalance == amount,
                                onClick = { selectedBalance = amount },
                                label = {
                                    Text(
                                        text = "$${String.format("%,.0f", amount)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = strategy,
                    onValueChange = { strategy = it },
                    label = { Text("Strategy (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val balance = selectedBalance ?: return@Button
                        if (name.isNotBlank()) {
                            onCreate(
                                name.trim(),
                                strategy.ifBlank { "No strategy" },
                                balance
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = name.isNotBlank() && selectedBalance != null
                ) {
                    Text(
                        text = "Create",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}