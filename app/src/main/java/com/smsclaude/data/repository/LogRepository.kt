package com.smsclaude.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smsclaude.data.model.LogEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.logsDataStore: DataStore<Preferences> by preferencesDataStore(name = "logs")

class LogRepository(private val context: Context) {

    companion object {
        val LOGS_KEY = stringPreferencesKey("log_entries")
        const val MAX_LOGS = 500
    }

    val logsFlow: Flow<List<LogEntry>> = context.logsDataStore.data.map { prefs ->
        val json = prefs[LOGS_KEY] ?: "[]"
        try {
            Json.decodeFromString<List<LogEntry>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addEntry(entry: LogEntry) {
        context.logsDataStore.edit { prefs ->
            val json = prefs[LOGS_KEY] ?: "[]"
            val entries = try {
                Json.decodeFromString<List<LogEntry>>(json).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            entries.add(0, entry)
            if (entries.size > MAX_LOGS) {
                entries.subList(MAX_LOGS, entries.size).clear()
            }
            prefs[LOGS_KEY] = Json.encodeToString(entries)
        }
    }

    suspend fun clearLogs() {
        context.logsDataStore.edit { prefs ->
            prefs[LOGS_KEY] = "[]"
        }
    }
}
