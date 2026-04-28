package com.smsclaude.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smsclaude.ui.components.tealTextFieldColors
import com.smsclaude.ui.theme.*
import com.smsclaude.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsState()

    var prefixText by remember(settings.prefix) { mutableStateOf(settings.prefix) }
    var suffixText by remember(settings.suffix) { mutableStateOf(settings.suffix) }

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
                text = "SETTINGS",
                color = ElectricTeal,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(4.dp))
            Text("Configuration", color = OnSurface, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }

        item {
            SettingsSectionHeader("BEHAVIOR")
        }

        item {
            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Start on Boot", color = OnSurface, fontSize = 14.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Auto-start when device reboots",
                            color = OnSurfaceMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = settings.startOnBoot,
                        onCheckedChange = { viewModel.setStartOnBoot(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepCharcoal,
                            checkedTrackColor = ElectricTeal,
                            uncheckedThumbColor = OnSurfaceMuted,
                            uncheckedTrackColor = BorderColor
                        )
                    )
                }
            }
        }

        item {
            SettingsCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sending Delay", color = OnSurface, fontSize = 14.sp)
                        Text(
                            "${settings.sentDelay}s",
                            color = ElectricTeal,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Delay before sending each SMS",
                        color = OnSurfaceMuted,
                        fontSize = 11.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Slider(
                        value = settings.sentDelay.toFloat(),
                        onValueChange = { viewModel.setSendingDelay(it.toInt()) },
                        valueRange = 0f..60f,
                        steps = 59,
                        modifier = Modifier.height(30.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricTeal,
                            activeTrackColor = ElectricTeal,
                            inactiveTrackColor = BorderColor
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0s", color = OnSurfaceMuted, fontSize = 10.sp)
                        Text("60s", color = OnSurfaceMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        item {
            SettingsSectionHeader("MESSAGE FORMAT")
        }

        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = prefixText,
                        onValueChange = {
                            prefixText = it
                            viewModel.setPrefix(it)
                        },
                        label = { Text("Message Prefix") },
                        placeholder = { Text("[FWD] ") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = tealTextFieldColors(),
                        shape = RoundedCornerShape(4.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = suffixText,
                        onValueChange = {
                            suffixText = it
                            viewModel.setSuffix(it)
                        },
                        label = { Text("Message Suffix") },
                        placeholder = { Text(" [End]") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = tealTextFieldColors(),
                        shape = RoundedCornerShape(4.dp),
                        singleLine = true
                    )
                    if (prefixText.isNotBlank() || suffixText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElectricTeal.copy(alpha = 0.05f))
                                .border(1.dp, ElectricTeal.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "${prefixText}From +1234567890: Your message here${suffixText}",
                                color = OnSurfaceMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        item {
            SettingsSectionHeader("ABOUT")
        }

        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AboutRow("Version", "1.1.0")
                    HorizontalDivider(color = BorderColor)
                    AboutRow("Author", "[Mojtaba Akhbari]")
                    HorizontalDivider(color = BorderColor)
                    AboutRow("GitHub", "github.com/mojtabaakhbari", isLink = true)
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = OnSurfaceMuted,
        fontSize = 10.sp,
        letterSpacing = 2.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun AboutRow(label: String, value: String, isLink: Boolean = false) {
    val context = LocalContext.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = OnSurfaceMuted, fontSize = 13.sp)
        Text(
            value, 
            color = if (isLink) ElectricTeal else OnSurface, 
            fontSize = 13.sp, 
            fontFamily = FontFamily.Monospace,
            modifier = if (isLink) {
                Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://$value"))
                    context.startActivity(intent)
                }
            } else {
                Modifier
            }
        )
    }
}
