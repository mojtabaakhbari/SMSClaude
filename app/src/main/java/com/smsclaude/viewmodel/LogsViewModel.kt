package com.smsclaude.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smsclaude.data.model.LogEntry
import com.smsclaude.data.model.LogStatus
import com.smsclaude.data.repository.LogRepository
import com.smsclaude.data.repository.SettingsRepository
import com.smsclaude.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LogsUiState(
    val logs: List<LogEntry> = emptyList(),
    val filteredLogs: List<LogEntry> = emptyList(),
    val selectedFilter: LogFilter = LogFilter.ALL,
    val showClearDialog: Boolean = false
)

enum class LogFilter { ALL, FORWARDED, REPLIED, FAILED }

class LogsViewModel(application: Application) : AndroidViewModel(application) {


    private val activityRepo = ActivityRepository(application)
    private val settingsRepo = SettingsRepository(application)
    private val logRepo = LogRepository(application, settingsRepo)
    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            logRepo.logsFlow.collect { logs ->
                val current = _uiState.value
                _uiState.value = current.copy(
                    logs = logs,
                    filteredLogs = applyFilter(logs, current.selectedFilter)
                )
            }
        }
    }

    fun setFilter(filter: LogFilter) {
        val logs = _uiState.value.logs
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredLogs = applyFilter(logs, filter)
        )
    }

    private fun applyFilter(logs: List<LogEntry>, filter: LogFilter): List<LogEntry> {
        return when (filter) {
            LogFilter.ALL -> logs
            LogFilter.FORWARDED -> logs.filter { it.status == LogStatus.FORWARDED }
            LogFilter.REPLIED -> logs.filter { it.status == LogStatus.REPLIED }
            LogFilter.FAILED -> logs.filter { it.status == LogStatus.FWD_FAILED || it.status == LogStatus.RPL_FAILED }
        }
    }

    fun showClearDialog() {
        _uiState.value = _uiState.value.copy(showClearDialog = true)
    }

    fun dismissClearDialog() {
        _uiState.value = _uiState.value.copy(showClearDialog = false)
    }

    fun clearLogs() {
        viewModelScope.launch {
            logRepo.clearLogs()
            activityRepo.clearActivity()
            settingsRepo.resetCount()
            _uiState.value = _uiState.value.copy(showClearDialog = false)
        }
    }
}
