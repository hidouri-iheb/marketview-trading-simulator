package com.ihebhidouri.marketview.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import com.ihebhidouri.marketview.ui.theme.AvatarColors



@Composable
fun UserAvatar(
    displayName: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val name = displayName ?: "?"
    val initial = name.take(1).uppercase()
    val color = AvatarColors[
        name.hashCode().absoluteValue % AvatarColors.size
    ]

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.5.dp,
                color = color.copy(alpha = 0.6f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = color,
            fontSize = (size.value * 0.4f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}