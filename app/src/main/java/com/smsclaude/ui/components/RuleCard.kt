package com.smsclaude.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smsclaude.data.model.SmsRule
import com.smsclaude.ui.theme.*

@Composable
fun RuleCard(
    rule: SmsRule,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceCard)
            .border(1.dp, if (rule.enabled) ElectricTeal.copy(alpha = 0.3f) else BorderColor, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (rule.enabled) ElectricTeal else OnSurfaceMuted)
                .align(Alignment.CenterStart)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 12.dp, top = 13.3.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FROM",
                            color = OnSurfaceMuted,
                            fontSize = 9.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = rule.sources.joinToString(", "),
                            color = if (rule.enabled) ElectricTeal else OnSurfaceMuted,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(Modifier.height(4.5.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TO",
                            color = OnSurfaceMuted,
                            fontSize = 9.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = rule.destinations.joinToString(", "),
                            color = OnSurface,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(Modifier.height(4.5.dp))
                    val (kwdText, kwdColor) = when {
                        rule.keyword.isBlank() -> "NOT SET" to RedInfo
                        rule.keyword.length > 21 -> "\"${rule.keyword.take(21)} ...\"" to AmberWarning
                        else -> "\"${rule.keyword}\"" to AmberWarning
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KWD",
                            color = OnSurfaceMuted,
                            fontSize = 9.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = kwdText,
                            color = kwdColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(Modifier.height(4.5.dp))
                    val (replyText, replyColor) = when {
                        rule.default_text.isBlank() -> "NOT SET" to RedInfo
                        rule.default_text.length > 21 -> rule.default_text.take(21) + "..." to AmberWarning
                        else -> rule.default_text to AmberWarning
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REPLY WITH",
                            color = OnSurfaceMuted,
                            fontSize = 9.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = replyText,
                            color = replyColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )


                    }

                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(24.4.dp)
                ) {
                    Switch(
                        checked = rule.enabled,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepCharcoal,
                            checkedTrackColor = ElectricTeal,
                            uncheckedThumbColor = OnSurfaceMuted,
                            uncheckedTrackColor = BorderColor
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ElectricTeal, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
