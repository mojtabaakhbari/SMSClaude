package com.smsclaude.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smsclaude.data.model.AppSettings
import com.smsclaude.data.model.RecentActivity
import com.smsclaude.data.repository.ActivityRepository
import com.smsclaude.data.repository.SettingsRepository
import com.smsclaude.permission.PermissionManager
import com.smsclaude.service.SmsForwarderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isServiceRunning: Boolean = false,
    val canStartService: Boolean = false,
    val settings: AppSettings = AppSettings(),
    val recentActivity: List<RecentActivity> = emptyList(),
    val showClearActivityDialog: Boolean = false
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo    = SettingsRepository(application)
    private val activityRepo    = ActivityRepository(application)
    private val permissionManager = PermissionManager(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

   
    private val _serviceRunning = MutableStateFlow(false)

    init {
       
        viewModelScope.launch {
            val initial = settingsRepo.getSettings()
            _serviceRunning.value = initial.isServiceRunning
        }


        viewModelScope.launch {
            combine(
                _serviceRunning,
                settingsRepo.settingsFlow,
                activityRepo.activityFlow
            ) { running, settings, activity ->
              
                val dialogOpen = _uiState.value.showClearActivityDialog
                DashboardUiState(
                    isServiceRunning      = running,
                    canStartService       = permissionManager.canStartService(),
                    settings              = settings,
                    recentActivity        = activity,
                    showClearActivityDialog = dialogOpen
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    /** Called from MainActivity every time the app resumes */
    fun refreshPermissionState() {
        _uiState.value = _uiState.value.copy(
            canStartService = permissionManager.canStartService()
        )
    }

    fun startService() {
        if (!permissionManager.canStartService()) return
        val context = getApplication<Application>()

       
        _serviceRunning.value = true

        context.startForegroundService(
            Intent(context, SmsForwarderService::class.java)
        )
        viewModelScope.launch {
            settingsRepo.setUserStopped(false)
            settingsRepo.setServiceRunning(true)
        }
    }

    fun stopService() {
        val context = getApplication<Application>()

       
        _serviceRunning.value = false

        context.startService(
            Intent(context, SmsForwarderService::class.java).apply {
                action = SmsForwarderService.ACTION_STOP
            }
        )
        viewModelScope.launch {
            settingsRepo.setUserStopped(true)
            settingsRepo.setServiceRunning(false)
        }
    }

    fun showClearActivityDialog() {
        _uiState.value = _uiState.value.copy(showClearActivityDialog = true)
    }

    fun dismissClearActivityDialog() {
        _uiState.value = _uiState.value.copy(showClearActivityDialog = false)
    }

    fun clearActivity() {
        viewModelScope.launch {
            activityRepo.clearActivity()
            _uiState.value = _uiState.value.copy(showClearActivityDialog = false)
        }
    }
}
