package com.smsclaude

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smsclaude.permission.PermissionManager
import com.smsclaude.ui.components.StatusBar
import com.smsclaude.ui.components.ToastHost
import com.smsclaude.ui.screens.*
import com.smsclaude.ui.theme.*
import com.smsclaude.viewmodel.DashboardViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Rules     : Screen("rules",     "Rules",     Icons.Default.Rule)
    object Logs      : Screen("logs",      "Logs",      Icons.Default.List)
    object Settings  : Screen("settings",  "Settings",  Icons.Default.Settings)
}

private val screens = listOf(Screen.Dashboard, Screen.Rules, Screen.Logs, Screen.Settings)

class MainActivity : ComponentActivity() {

    lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager = PermissionManager(this)
        enableEdgeToEdge()
        setContent {
            SmsForwarderTheme {
                MainApp(activity = this)
            }
        }
    }
}

@Composable
private fun PermissionItem(name: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(ElectricTeal.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = name,
            color      = ElectricTeal,
            fontSize   = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.width(140.dp)
        )
        Text(
            description,
            color    = OnSurfaceMuted,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MainApp(activity: MainActivity) {
    val permissionManager = activity.permissionManager
    val dashboardViewModel: DashboardViewModel = viewModel()
    val dashboardState by dashboardViewModel.uiState.collectAsState()


    var permCheckTick by remember { mutableIntStateOf(0) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            permCheckTick++
        }
    }

    val allPermsGranted by remember(permCheckTick) {
        derivedStateOf { permissionManager.allPermissionsGranted() }
    }
    val batteryOk by remember(permCheckTick) {
        derivedStateOf { permissionManager.isBatteryOptimizationDisabled() }
    }

    val showPermissionDialog = !allPermsGranted
    val showBatteryDialog    = allPermsGranted && !batteryOk


    LaunchedEffect(permCheckTick) {
        dashboardViewModel.refreshPermissionState()
    }


    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { /* non-dismissible . user must grant */ },
            title = {
                Text("Permissions Required", color = OnSurface, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "SMS Claude needs all of the following permissions. " +
                        "Without them the forwarding service cannot run.",
                        color    = OnSurfaceMuted,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    PermissionItem("RECEIVE_SMS",        "Listen for incoming text messages")
                    PermissionItem("READ_SMS",           "Access SMS content")
                    PermissionItem("SEND_SMS",           "Forward messages to configured numbers")
                    PermissionItem("POST_NOTIFICATIONS", "Show foreground service notification")
                }
            },
            confirmButton = {
                Button(
                    onClick = { permissionManager.requestMissingPermissions(activity) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricTeal,
                        contentColor   = DeepCharcoal
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Grant Permissions", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { permissionManager.openAppSettings() }) {
                    Text("Open App Settings", color = ElectricTeal)
                }
            },
            containerColor = SurfaceCard,
            shape          = RoundedCornerShape(4.dp)
        )
    }

   
    if (showBatteryDialog) {
        AlertDialog(
            onDismissRequest = { /* non-dismissible */ },
            title = {
                Text(
                    "Battery Optimization Must Be Disabled",
                    color      = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "To ensure SMS forwarding works reliably in the background, " +
                    "this app must be excluded from battery optimization. " +
                    "Without this the OS may kill the service at any time.",
                    color = OnSurfaceMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = { permissionManager.openBatterySettings() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricTeal,
                        contentColor   = DeepCharcoal
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Disable Now", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceCard,
            shape          = RoundedCornerShape(4.dp)
        )
    }

 
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepCharcoal)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
      
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                NavHost(
                    navController    = navController,
                    startDestination = Screen.Dashboard.route,
                    modifier         = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Dashboard.route) {
                        DashboardScreen(viewModel = dashboardViewModel)
                    }
                    composable(Screen.Rules.route)    { RulesScreen() }
                    composable(Screen.Logs.route)     { LogsScreen() }
                    composable(Screen.Settings.route) { SettingsScreen() }
                }
            }

          
            NavigationBar(
                containerColor = SurfaceCard,
                tonalElevation = 0.dp,
                windowInsets   = WindowInsets(0)
            ) {
                screens.forEach { screen ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.label,
                                modifier           = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                screen.label,
                                fontSize   = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        selected = selected,
                        onClick  = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = ElectricTeal,
                            selectedTextColor   = ElectricTeal,
                            unselectedIconColor = OnSurfaceMuted,
                            unselectedTextColor = OnSurfaceMuted,
                            indicatorColor      = ElectricTeal.copy(alpha = 0.15f)
                        )
                    )
                }
            }

            StatusBar(
                isActive = dashboardState.isServiceRunning,
                modifier = Modifier.navigationBarsPadding()
            )
        }

    
        ToastHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 180.dp)
        )
    }
}
