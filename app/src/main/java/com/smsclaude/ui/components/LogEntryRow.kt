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
import com.smsclaude.data.model.LogEntry
import com.smsclaude.data.model.LogStatus
import com.smsclaude.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LogEntryRow(entry: LogEntry, modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    val dateTime = formatter.format(Instant.ofEpochMilli(entry.timestamp))

    val (statusColor, statusLabel) = when (entry.status) {
        LogStatus.FORWARDED -> ElectricTeal to "FORWARDED"
        LogStatus.SKIPPED -> AmberWarning to "SKIPPED"
        LogStatus.FAILED -> RedError to "FAILED"
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
                .background(statusColor.copy(alpha = 0.7f))
                .align(Alignment.CenterStart)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateTime,
                    color = OnSurfaceMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(entry.sender, color = ElectricTeal, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("→", color = OnSurfaceMuted, fontSize = 11.sp)
                Text(entry.destination, color = OnSurface, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = entry.preview,
                color = OnSurfaceMuted,
                fontSize = 12.sp,
                maxLines = 2
            )
        }
    }
}
