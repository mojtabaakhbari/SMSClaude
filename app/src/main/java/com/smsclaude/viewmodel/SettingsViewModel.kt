package com.smsclaude.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smsclaude.data.model.AppSettings
import com.smsclaude.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.settingsFlow.collect { s ->
                _settings.value = s
            }
        }
    }

    fun setStartOnBoot(enabled: Boolean) {
        viewModelScope.launch {
            val s = _settings.value
            settingsRepo.updateUserSettings(
                startOnBoot = enabled,
                forwardDelay = s.forwardDelay,
                prefix = s.prefix,
                suffix = s.suffix
            )
        }
    }

    fun setForwardDelay(delay: Int) {
        viewModelScope.launch {
            val s = _settings.value
            settingsRepo.updateUserSettings(
                startOnBoot = s.startOnBoot,
                forwardDelay = delay,
                prefix = s.prefix,
                suffix = s.suffix
            )
        }
    }

    fun setPrefix(prefix: String) {
        viewModelScope.launch {
            val s = _settings.value
            settingsRepo.updateUserSettings(
                startOnBoot = s.startOnBoot,
                forwardDelay = s.forwardDelay,
                prefix = prefix,
                suffix = s.suffix
            )
        }
    }

    fun setSuffix(suffix: String) {
        viewModelScope.launch {
            val s = _settings.value
            settingsRepo.updateUserSettings(
                startOnBoot = s.startOnBoot,
                forwardDelay = s.forwardDelay,
                prefix = s.prefix,
                suffix = suffix
            )
        }
    }
}
