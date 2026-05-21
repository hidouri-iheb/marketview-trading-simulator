package com.ihebhidouri.marketview.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val AppBackground = Color(0xFF070A18)
private val DeepPanel = Color(0xFF0B1026)
private val CardPanel = Color(0xFF151B3D)
private val NeonCyan = Color(0xFF18E6FF)
private val NeonMint = Color(0xFF6BFFB8)
private val MutedText = Color(0xFF7D89B0)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeHeader()
        SearchHolder()
        MarketPulseHolder()
        MarketIndexesHolder()
        TrendingHolder()
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Welcome back",
                color = MutedText,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "MarketView",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF11183A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFFB5C0EA)
            )
        }
    }
}

@Composable
private fun SearchHolder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = DeepPanel
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = NeonCyan
            )

            Text(
                text = "Search stocks, ETFs, indexes...",
                color = MutedText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun MarketPulseHolder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF202866),
                            Color(0xFF12183D),
                            Color(0xFF0E1432)
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "MARKET PULSE",
                        color = Color(0xFF9AA7D4),
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "65",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall
                    )

                    Text(
                        text = "Greed Mode",
                        color = NeonMint,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0x3318E6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Market pulse",
                        tint = NeonCyan,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            FakeChartLine()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Fear", color = MutedText, style = MaterialTheme.typography.labelSmall)
                Text("Neutral", color = MutedText, style = MaterialTheme.typography.labelSmall)
                Text("Greed", color = MutedText, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun FakeChartLine() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                NeonCyan,
                                NeonMint
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun MarketIndexesHolder() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MarketIndexMiniCard(
            title = "S&P 500",
            value = "5,234",
            change = "+0.82%",
            isPositive = true,
            modifier = Modifier.weight(1f)
        )

        MarketIndexMiniCard(
            title = "NASDAQ",
            value = "16,441",
            change = "+1.23%",
            isPositive = true,
            modifier = Modifier.weight(1f)
        )

        MarketIndexMiniCard(
            title = "DOW",
            value = "38,904",
            change = "-0.14%",
            isPositive = false,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MarketIndexMiniCard(
    title: String,
    value: String,
    change: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF11183A)
        )
    ) {
        Column(
            modifier = Modifier.padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                color = MutedText,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = change,
                color = if (isPositive) NeonMint else Color(0xFFFF496D),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun TrendingHolder() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Signals",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "See all",
                color = NeonCyan,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardPanel
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                StockPreviewRow("AAPL", "Apple Inc.", "Technology", "$189.84", "+2.34%", true)
                StockPreviewRow("TSLA", "Tesla Inc.", "Automotive", "$247.20", "-1.44%", false)
                StockPreviewRow("NVDA", "NVIDIA Corp.", "AI Chips", "$875.39", "+3.21%", true)
            }
        }
    }
}

@Composable
private fun StockPreviewRow(
    symbol: String,
    name: String,
    category: String,
    price: String,
    change: String,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isPositive) Color(0x226BFFB8) else Color(0x22FF496D)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol,
                color = if (isPositive) NeonMint else Color(0xFFFF8BA2),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = category,
                color = MutedText,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Icon(
            imageVector = Icons.Default.ShowChart,
            contentDescription = "Chart",
            tint = if (isPositive) NeonMint else Color(0xFFFF496D),
            modifier = Modifier.size(34.dp)
        )

        Spacer(modifier = Modifier.size(10.dp))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = price,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = change,
                color = if (isPositive) NeonMint else Color(0xFFFF496D),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}