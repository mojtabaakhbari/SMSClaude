package com.smsclaude.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smsclaude.data.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val START_ON_BOOT = booleanPreferencesKey("start_on_boot")
        val SENT_DELAY = intPreferencesKey("sent_delay")
        val PREFIX = stringPreferencesKey("prefix")
        val SUFFIX = stringPreferencesKey("suffix")
        val IS_USER_STOPPED = booleanPreferencesKey("is_user_stopped")
        val IS_SERVICE_RUNNING = booleanPreferencesKey("is_service_running")
        val TOTAL_SENT = intPreferencesKey("total_sent")
        val TODAY_SENT = intPreferencesKey("today_sent")
        val LAST_SENT_TIMESTAMP = longPreferencesKey("last_sent_timestamp")
        val LAST_SENT_DATE = stringPreferencesKey("last_sent_date")
        val LAST_CHECK_TODAY_COUNT_DATE = stringPreferencesKey("last_check_today_count_date")
    }

    val settingsFlow: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            startOnBoot = prefs[START_ON_BOOT] ?: false,
            sentDelay = prefs[SENT_DELAY] ?: 0,
            prefix = prefs[PREFIX] ?: "",
            suffix = prefs[SUFFIX] ?: "",
            isUserStopped = prefs[IS_USER_STOPPED] ?: true,
            isServiceRunning = prefs[IS_SERVICE_RUNNING] ?: false,
            totalSent= prefs[TOTAL_SENT] ?: 0,
            todaySent = prefs[TODAY_SENT] ?: 0,
            lastSentTimestamp = prefs[LAST_SENT_TIMESTAMP] ?: 0L,
            lastSentDate = prefs[LAST_SENT_DATE] ?: "",
            lastCheckTodayCountDate = prefs[LAST_CHECK_TODAY_COUNT_DATE] ?: ""
        )
    }

    suspend fun getSettings(): AppSettings = settingsFlow.first()

    suspend fun setUserStopped(stopped: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[IS_USER_STOPPED] = stopped
        }
    }

    suspend fun setServiceRunning(running: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[IS_SERVICE_RUNNING] = running
        }
    }

  
    suspend fun updateUserSettings(
        startOnBoot: Boolean,
        sentDelay: Int,
        prefix: String,
        suffix: String
    ) {
        context.settingsDataStore.edit { prefs ->
            prefs[START_ON_BOOT] = startOnBoot
            prefs[SENT_DELAY] = sentDelay
            prefs[PREFIX] = prefix
            prefs[SUFFIX] = suffix
        }
    }

    suspend fun incrementSentCount(today: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[LAST_CHECK_TODAY_COUNT_DATE] = today
            prefs[TOTAL_SENT] = (prefs[TOTAL_SENT] ?: 0) + 1
            val lastDate = prefs[LAST_SENT_DATE] ?: ""
            if (lastDate == today) {
                prefs[TODAY_SENT] = (prefs[TODAY_SENT] ?: 0) + 1
            } else {
                prefs[TODAY_SENT] = 1
                prefs[LAST_SENT_DATE] = today
            }
            prefs[LAST_SENT_TIMESTAMP] = System.currentTimeMillis()

        }
    }
    suspend fun resetCount() {
        context.settingsDataStore.edit { prefs ->
            prefs[TOTAL_SENT] = 0
            prefs[TODAY_SENT] = 0
            prefs[LAST_SENT_TIMESTAMP] = 0L
            prefs[LAST_SENT_DATE] = ""
            prefs[LAST_CHECK_TODAY_COUNT_DATE] = ""
        }
    }
    suspend fun setTodaySent(count: Int, dt: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[TODAY_SENT] = count
            prefs[LAST_CHECK_TODAY_COUNT_DATE] = dt
        }
    }

    suspend fun getLastCheckTodayCountDate(): String {
        return getSettings().lastCheckTodayCountDate
    }

}
