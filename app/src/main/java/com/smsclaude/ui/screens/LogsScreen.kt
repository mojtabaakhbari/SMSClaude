package com.smsclaude.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smsclaude.ui.components.LogEntryRow
import com.smsclaude.ui.components.ToastManager
import com.smsclaude.ui.components.ToastType
import com.smsclaude.ui.theme.*
import com.smsclaude.viewmodel.LogFilter
import com.smsclaude.viewmodel.LogsViewModel

@Composable
fun LogsScreen(viewModel: LogsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showClearDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissClearDialog() },
            title = { Text("Clear All Logs", color = OnSurface) },
            text = {
                Text(
                    "This will permanently delete all log entries and counts. Continue?",
                    color = OnSurfaceMuted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearLogs()
                    ToastManager.show("All logs cleared", ToastType.SUCCESS)
                }) {
                    Text("Clear", color = RedError)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissClearDialog() }) {
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EVENT LOGS",
                        color = ElectricTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${uiState.logs.size} ${if (uiState.logs.size == 1) "entry" else "entries"}",
                        color = OnSurfaceMuted,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick = { viewModel.showClearDialog() },
                    enabled = uiState.logs.isNotEmpty()
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Clear Logs",
                        tint = if (uiState.logs.isNotEmpty()) RedError.copy(alpha = 0.8f) else OnSurfaceMuted.copy(alpha = 0.3f)
                    )
                }
            }
        }

       
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LogFilter.entries.toTypedArray()) { filter ->
                    FilterChip(
                        selected = uiState.selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(
                                filter.name,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricTeal.copy(alpha = 0.2f),
                            selectedLabelColor = ElectricTeal,
                            containerColor = SurfaceCard,
                            labelColor = OnSurfaceMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uiState.selectedFilter == filter,
                            selectedBorderColor = ElectricTeal.copy(alpha = 0.5f),
                            borderColor = BorderColor
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
                }
            }
        }

        if (uiState.filteredLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderColor, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No log entries", color = OnSurfaceMuted, fontSize = 13.sp)
                }
            }
        } else {
            items(uiState.filteredLogs) { entry ->
                LogEntryRow(entry = entry)
            }
        }
    }
}
