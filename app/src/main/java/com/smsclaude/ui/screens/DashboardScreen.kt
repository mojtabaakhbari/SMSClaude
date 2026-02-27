package com.smsclaude.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smsclaude.data.model.AppSettings
import com.smsclaude.ui.components.*
import com.smsclaude.ui.theme.*
import com.smsclaude.viewmodel.DashboardViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()

  
    if (uiState.showClearActivityDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissClearActivityDialog() },
            title = { Text("Clear Activity", color = OnSurface) },
            text = {
                Text(
                    "This will permanently delete all recent activity entries. Continue?",
                    color = OnSurfaceMuted
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearActivity() }) {
                    Text("Clear", color = RedError)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissClearActivityDialog() }) {
                    Text("Cancel", color = OnSurfaceMuted)
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(4.dp)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoal)
            .padding(top = 8.dp)
            .statusBarsPadding(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "DASHBOARD",
                color = ElectricTeal,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "SMS Claude",
                color = OnSurface,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

     
        item {
            ServiceToggleCard(
                isRunning = uiState.isServiceRunning,
                canStart = uiState.canStartService,
                onToggle = { wantsOn ->
                    if (!uiState.canStartService && wantsOn) return@ServiceToggleCard
                    if (wantsOn) {
                        viewModel.startService()
                        ToastManager.show("SMS Claude started", ToastType.SUCCESS)
                    } else {
                        viewModel.stopService()
                        ToastManager.show("SMS Claude stopped", ToastType.INFO)
                    }
                }
            )
        }

        
        item { StatsRow(settings = uiState.settings) }

      
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RECENT ACTIVITY",
                        color = OnSurfaceMuted,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${uiState.recentActivity.size} / 50 entries",
                        color = OnSurfaceMuted,
                        fontSize = 11.sp
                    )
                }
                IconButton(
                    onClick = { viewModel.showClearActivityDialog() },
                    enabled = uiState.recentActivity.isNotEmpty()
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Clear Activity",
                        tint = if (uiState.recentActivity.isNotEmpty())
                            RedError.copy(alpha = 0.8f)
                        else
                            OnSurfaceMuted.copy(alpha = 0.3f)
                    )
                }
            }
        }

        if (uiState.recentActivity.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderColor, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No activity yet", color = OnSurfaceMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(uiState.recentActivity, key = { it.id }) { activity ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    ActivityCard(activity = activity)
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ServiceToggleCard(
    isRunning: Boolean,
    canStart: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceCard)
            .border(
                width = 1.dp,
                color = if (isRunning) ElectricTeal.copy(alpha = 0.5f) else BorderColor,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        
        if (isRunning) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(ElectricTeal.copy(alpha = 0.7f))
                    .align(Alignment.TopCenter)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isRunning) PulsingDot() else StaticDot(color = OnSurfaceMuted)
                    Text(
                        text = if (isRunning) "SERVICE ACTIVE" else "SERVICE INACTIVE",
                        color = if (isRunning) ElectricTeal else OnSurfaceMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isRunning) "Monitoring and forwarding incoming SMS"
                    else "Toggle to start SMS Claude",
                    color = OnSurfaceMuted,
                    fontSize = 12.sp
                )
                if (!canStart && !isRunning) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AmberWarning.copy(alpha = 0.1f))
                            .border(1.dp, AmberWarning.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Permissions Required",
                                color = AmberWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Switch(
                checked = isRunning,
                onCheckedChange = onToggle,
                enabled = canStart || isRunning,  
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DeepCharcoal,
                    checkedTrackColor = ElectricTeal,
                    uncheckedThumbColor = OnSurfaceMuted,
                    uncheckedTrackColor = SurfaceElevated,
                    disabledUncheckedThumbColor = OnSurfaceMuted.copy(alpha = 0.4f),
                    disabledUncheckedTrackColor = SurfaceElevated.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun StatsRow(settings: AppSettings) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard("TODAY", settings.todayForwarded.toString(), modifier = Modifier.weight(1f))
        StatCard("TOTAL", settings.totalForwarded.toString(), modifier = Modifier.weight(1f))
        StatCard(
            label = "LAST",
            value = if (settings.lastForwardedTimestamp > 0) {
                DateTimeFormatter.ofPattern("HH:mm")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.ofEpochMilli(settings.lastForwardedTimestamp))
            } else "—",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = label,
                color = OnSurfaceMuted,
                fontSize = 9.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                color = ElectricTeal,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
