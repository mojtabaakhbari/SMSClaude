package com.smsclaude.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smsclaude.data.model.SmsRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.rulesDataStore: DataStore<Preferences> by preferencesDataStore(name = "rules")

class RulesRepository(private val context: Context) {

    companion object {
        val RULES_KEY = stringPreferencesKey("sms_rules")
    }

    val rulesFlow: Flow<List<SmsRule>> = context.rulesDataStore.data.map { prefs ->
        val json = prefs[RULES_KEY] ?: "[]"
        try {
            Json.decodeFromString<List<SmsRule>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRules(): List<SmsRule> = rulesFlow.first()

    suspend fun saveRules(rules: List<SmsRule>) {
        context.rulesDataStore.edit { prefs ->
            prefs[RULES_KEY] = Json.encodeToString(rules)
        }
    }

    suspend fun addRule(rule: SmsRule) {
        val rules = getRules().toMutableList()
        rules.add(rule)
        saveRules(rules)
    }

    suspend fun updateRule(rule: SmsRule) {
        val rules = getRules().toMutableList()
        val index = rules.indexOfFirst { it.id == rule.id }
        if (index >= 0) {
            rules[index] = rule
            saveRules(rules)
        }
    }

    suspend fun deleteRule(ruleId: String) {
        val rules = getRules().filter { it.id != ruleId }
        saveRules(rules)
    }

    suspend fun toggleRule(ruleId: String, enabled: Boolean) {
        val rules = getRules().toMutableList()
        val index = rules.indexOfFirst { it.id == ruleId }
        if (index >= 0) {
            rules[index] = rules[index].copy(enabled = enabled)
            saveRules(rules)
        }
    }
}
