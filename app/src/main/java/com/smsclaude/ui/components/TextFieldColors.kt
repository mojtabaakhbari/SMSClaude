package com.smsclaude.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import com.smsclaude.ui.theme.*

@Composable
fun tealTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ElectricTeal,
    unfocusedBorderColor = BorderColor,
    focusedLabelColor = ElectricTeal,
    unfocusedLabelColor = OnSurfaceMuted,
    cursorColor = ElectricTeal,
    focusedTextColor = OnSurface,
    unfocusedTextColor = OnSurface,
    focusedPlaceholderColor = OnSurfaceMuted,
    unfocusedPlaceholderColor = OnSurfaceMuted
)

@Composable
fun errorTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = RedError,
    unfocusedBorderColor = RedError,
    focusedLabelColor = RedError,
    unfocusedLabelColor = RedError,
    cursorColor = RedError,
    focusedTextColor = OnSurface,
    unfocusedTextColor = OnSurface,
    focusedPlaceholderColor = OnSurfaceMuted,
    unfocusedPlaceholderColor = OnSurfaceMuted
)
