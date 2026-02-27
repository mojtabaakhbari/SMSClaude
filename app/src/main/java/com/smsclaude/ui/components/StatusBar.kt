package com.smsclaude.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smsclaude.ui.theme.*

@Composable
fun StatusBar(isActive: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(if (isActive) ElectricTeal.copy(alpha = 0.4f) else BorderColor)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Router,
                    contentDescription = null,
                    tint = if (isActive) ElectricTeal else OnSurface,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "SMS CLAUDE",
                    color = OnSurface,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isActive) {
                    PulsingDot(size = 6.dp)
                } else {
                    StaticDot(color = OnSurface, size = 6.dp)
                }
                Text(
                    text = if (isActive) "ACTIVE" else "INACTIVE",
                    color = if (isActive) ElectricTeal else OnSurface,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
