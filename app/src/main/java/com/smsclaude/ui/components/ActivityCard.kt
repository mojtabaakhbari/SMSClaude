package com.smsclaude.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smsclaude.data.model.LogStatus
import com.smsclaude.data.model.RecentActivity
import com.smsclaude.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ActivityCard(activity: RecentActivity, modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM · HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    val dateTime = formatter.format(Instant.ofEpochMilli(activity.timestamp))

    val (statusColor, statusText) = when (activity.status) {
        LogStatus.FORWARDED -> ElectricTeal to "FWD"
        LogStatus.SKIPPED -> AmberWarning to "SKIP"
        LogStatus.FAILED -> RedError to "FAIL"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
    ) {
        
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(statusColor)
                .align(Alignment.CenterStart)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 8.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.sender,
                        color = ElectricTeal,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "→", color = OnSurfaceMuted, fontSize = 11.sp)
                    Text(
                        text = activity.destination,
                        color = OnSurface,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
              
                Text(
                    text = dateTime,
                    color = OnSurfaceMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.preview,
                    color = OnSurfaceMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
