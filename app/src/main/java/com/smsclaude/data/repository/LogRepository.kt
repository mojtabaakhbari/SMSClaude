package com.smsclaude.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smsclaude.data.model.LogEntry
import com.smsclaude.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId

private val Context.logsDataStore: DataStore<Preferences> by preferencesDataStore(name = "logs")

class LogRepository(private val context: Context, private val settingsRepository: SettingsRepository) {

    companion object {
        val LOGS_KEY = stringPreferencesKey("log_entries")
        const val MAX_LOGS = 500
    }

    private val json = Json

    val logsFlow: Flow<List<LogEntry>> = context.logsDataStore.data.map { prefs ->
        val jsonString = prefs[LOGS_KEY] ?: "[]"
        try {
            json.decodeFromString<List<LogEntry>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addEntry(entry: LogEntry) {
        context.logsDataStore.edit { prefs ->
            val jsonString = prefs[LOGS_KEY] ?: "[]"

            val entries = try {
                json.decodeFromString<List<LogEntry>>(jsonString).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }

            entries.add(0, entry)

            if (entries.size > MAX_LOGS) {
                entries.subList(MAX_LOGS, entries.size).clear()
            }

            prefs[LOGS_KEY] = json.encodeToString(entries)
        }
    }

    suspend fun clearLogs() {
        context.logsDataStore.edit { prefs ->
            prefs[LOGS_KEY] = "[]"
        }
    }

    suspend fun checkTodayCount() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        if (settingsRepository.getLastCheckTodayCountDate() == today) {
            return
        }
        val zone = ZoneId.systemDefault()
        val now = LocalDate.now()

        val startOfToday = now.atStartOfDay(zone).toInstant().toEpochMilli()
        val startOfTomorrow = now.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val logsPrefs = context.logsDataStore.data.first()
        val jsonString = logsPrefs[LOGS_KEY] ?: "[]"

        val entries = try {
            json.decodeFromString<List<LogEntry>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }

        val todayCount = entries.count {
            it.timestamp in startOfToday until startOfTomorrow
        }

        settingsRepository.setTodaySent(todayCount, today)
    }
}
