package com.smsclaude.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smsclaude.data.model.SmsRule
import com.smsclaude.data.repository.RulesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RulesUiState(
    val rules: List<SmsRule> = emptyList(),
    val showBottomSheet: Boolean = false,
    val editingRule: SmsRule? = null
) {
    val enabledRulesCount: Int
        get() = rules.count { it.enabled }
}

class RulesViewModel(application: Application) : AndroidViewModel(application) {

    private val rulesRepo = RulesRepository(application)

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState: StateFlow<RulesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            rulesRepo.rulesFlow.collect { rules ->
                _uiState.value = _uiState.value.copy(rules = rules)
            }
        }
    }

    fun showAddSheet() {
        _uiState.value = _uiState.value.copy(
            showBottomSheet = true,
            editingRule = null
        )
    }

    fun showEditSheet(rule: SmsRule) {
        _uiState.value = _uiState.value.copy(
            showBottomSheet = true,
            editingRule = rule
        )
    }

    fun dismissSheet() {
        _uiState.value = _uiState.value.copy(
            showBottomSheet = false,
            editingRule = null
        )
    }

    fun saveRule(rule: SmsRule) {
        viewModelScope.launch {
            val existing = _uiState.value.editingRule
            if (existing != null) {
                rulesRepo.updateRule(rule.copy(id = existing.id))
            } else {
                rulesRepo.addRule(rule)
            }
            dismissSheet()
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            rulesRepo.deleteRule(ruleId)
        }
    }

    fun toggleRule(ruleId: String, enabled: Boolean) {
        viewModelScope.launch {
            rulesRepo.toggleRule(ruleId, enabled)
        }
    }
}
