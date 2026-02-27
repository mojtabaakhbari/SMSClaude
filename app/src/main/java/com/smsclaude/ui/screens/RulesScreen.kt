package com.smsclaude.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smsclaude.data.model.ForwardingRule
import com.smsclaude.ui.components.RuleCard
import com.smsclaude.ui.components.errorTextFieldColors
import com.smsclaude.ui.components.tealTextFieldColors
import com.smsclaude.ui.components.ToastManager
import com.smsclaude.ui.components.ToastType
import com.smsclaude.ui.theme.*
import com.smsclaude.viewmodel.RulesViewModel

private val PHONE_REGEX = Regex("""^\+?[0-9\s\-\(\)]{7,15}$""")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(viewModel: RulesViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DeepCharcoal)) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FORWARDING RULES",
                            color = ElectricTeal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${uiState.rules.size} rules configured",
                            color = OnSurfaceMuted,
                            fontSize = 12.sp
                        )
                    }
                    FloatingActionButton(
                        onClick = { viewModel.showAddSheet() },
                        containerColor = ElectricTeal,
                        contentColor = DeepCharcoal,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Rule")
                    }
                }
            }

            if (uiState.rules.isEmpty()) {
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No rules yet", color = OnSurfaceMuted, fontSize = 14.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Tap + to create your first forwarding rule", color = OnSurfaceMuted, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(uiState.rules, key = { it.id }) { rule ->
                    RuleCard(
                        rule = rule,
                        onEdit = { viewModel.showEditSheet(rule) },
                        onDelete = { viewModel.deleteRule(rule.id) },
                        onToggle = { enabled -> viewModel.toggleRule(rule.id, enabled) }
                    )
                }
            }
        }

        if (uiState.showBottomSheet) {
            RuleBottomSheet(
                editingRule = uiState.editingRule,
                onDismiss = { viewModel.dismissSheet() },
                onSave = { rule ->
                    viewModel.saveRule(rule)
                    ToastManager.show(
                        if (uiState.editingRule != null) "Rule updated" else "Rule added",
                        ToastType.SUCCESS
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RuleBottomSheet(
    editingRule: ForwardingRule?,
    onDismiss: () -> Unit,
    onSave: (ForwardingRule) -> Unit
) {
    var sourcesText by remember { mutableStateOf(
        editingRule?.sources?.joinToString(", ") ?: ""
    ) }
    var destinationsText by remember { mutableStateOf(
        editingRule?.destinations?.joinToString(", ") ?: ""
    ) }
    var keyword by remember { mutableStateOf(editingRule?.keyword ?: "") }
    var enabled by remember { mutableStateOf(editingRule?.enabled ?: true) }

    var destinationTouched by remember { mutableStateOf(false) }
    var destinationFocused by remember { mutableStateOf(false) }

    val destinations = destinationsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
    val destinationValid = destinations.isNotEmpty() && destinations.all { PHONE_REGEX.matches(it) }
    val destinationError = when {
        destinationTouched && destinationsText.isBlank() -> "Destination is required"
        destinationTouched && !destinationValid -> "Invalid phone number"
        else -> null
    }

    val canSave = destinations.isNotEmpty() && destinationValid
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
        modifier = Modifier.heightIn(max = screenHeight * 0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (editingRule != null) "EDIT RULE" else "NEW RULE",
                color = ElectricTeal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

       
            OutlinedTextField(
                value = sourcesText,
                onValueChange = { sourcesText = it },
                label = { Text("Source Numbers (blank = ANY)") },
                placeholder = { Text("ANY or +1234567890, +9876543210", fontFamily = FontFamily.Monospace) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = tealTextFieldColors(),
                shape = RoundedCornerShape(4.dp)
            )

       
            OutlinedTextField(
                value = destinationsText,
                onValueChange = {
                    destinationsText = it
                    destinationTouched = true
                },
                label = { Text("Destination Number(s) *") },
                placeholder = { Text("+1234567890", fontFamily = FontFamily.Monospace) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        if (!state.isFocused && destinationFocused) {
                            destinationTouched = true
                        }
                        destinationFocused = state.isFocused
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = destinationError != null,
                supportingText = {
                    if (destinationError != null) {
                        Text(destinationError, color = RedError, fontSize = 11.sp)
                    } else if (destinationValid && destinationsText.isNotBlank()) {
                        Text("✓ Valid", color = ElectricTeal, fontSize = 11.sp)
                    }
                },
                colors = if (destinationError != null) {
                    errorTextFieldColors()
                } else if (destinationValid && destinationsText.isNotBlank()) {
                    tealTextFieldColors()
                } else {
                    tealTextFieldColors()
                },
                shape = RoundedCornerShape(4.dp)
            )

       
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("Keyword Filter (optional)") },
                placeholder = { Text("OTP, Bank, etc.") },
                modifier = Modifier.fillMaxWidth(),
                colors = tealTextFieldColors(),
                shape = RoundedCornerShape(4.dp)
            )

          
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Rule", color = OnSurface, fontSize = 14.sp)
                Switch(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DeepCharcoal,
                        checkedTrackColor = ElectricTeal
                    )
                )
            }

         
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceMuted),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (!canSave) {
                            destinationTouched = true
                            return@Button
                        }
                        val sources = if (sourcesText.isBlank()) listOf("ANY")
                        else sourcesText.split(",").map { it.trim() }.filter { it.isNotBlank() }

                        onSave(
                            ForwardingRule(
                                sources = sources,
                                destinations = destinations,
                                keyword = keyword.trim(),
                                enabled = enabled
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canSave) ElectricTeal else ElectricTeal.copy(alpha = 0.3f),
                        contentColor = if (canSave) DeepCharcoal else OnSurfaceMuted
                    )
                ) {
                    Text(if (editingRule != null) "Update" else "Add Rule", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
