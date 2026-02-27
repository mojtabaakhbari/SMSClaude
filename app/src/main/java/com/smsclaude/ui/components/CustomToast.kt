package com.smsclaude.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smsclaude.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.LinkedList
import java.util.Queue

enum class ToastType { SUCCESS, WARNING, ERROR, INFO }

data class ToastMessage(
    val message: String,
    val type: ToastType = ToastType.INFO,
    val id: Long = System.nanoTime()  
)

object ToastManager {
    private val _currentToast = MutableStateFlow<ToastMessage?>(null)
    val currentToast: StateFlow<ToastMessage?> = _currentToast.asStateFlow()

    private val queue: Queue<ToastMessage> = LinkedList()
    private var isProcessing = false

    fun show(message: String, type: ToastType = ToastType.INFO) {
        val toast = ToastMessage(message, type)
        synchronized(queue) {
            queue.offer(toast)
            if (!isProcessing) {
                isProcessing = true
                showNext()
            }
        }
    }

    private fun showNext() {
        val next = synchronized(queue) { queue.poll() }
        if (next != null) {
            _currentToast.value = next
        } else {
            synchronized(queue) { isProcessing = false }
        }
    }

    internal fun onToastFinished() {
        _currentToast.value = null
       
        showNext()
    }
}

@Composable
fun ToastHost(modifier: Modifier = Modifier) {
    val toast by ToastManager.currentToast.collectAsState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(toast) {
        if (toast != null) {
            visible = true
            delay(3000)
            visible = false
            delay(300) 
            ToastManager.onToastFinished()
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible && toast != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeIn(animationSpec = tween(200)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(250)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            toast?.let { t -> ToastItem(toast = t) }
        }
    }
}

@Composable
private fun ToastItem(toast: ToastMessage) {
    val (icon, accentColor) = when (toast.type) {
        ToastType.SUCCESS -> Icons.Default.CheckCircle to ElectricTeal
        ToastType.WARNING -> Icons.Default.Warning to AmberWarning
        ToastType.ERROR   -> Icons.Default.Error to RedError
        ToastType.INFO    -> Icons.Default.Info to BlueInfo
    }

    val progress = remember(toast.id) { Animatable(1f) }
    LaunchedEffect(toast.id) {
        progress.animateTo(0f, animationSpec = tween(3000, easing = LinearEasing))
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(50.dp),
                ambientColor = accentColor.copy(alpha = 0.3f),
                spotColor = accentColor.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xF01E1E2E))  
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = toast.message,
                color = OnSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }

     
        LinearProgressIndicator(
            progress = { progress.value },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)),
            color = accentColor,
            trackColor = Color.Transparent
        )
    }
}
