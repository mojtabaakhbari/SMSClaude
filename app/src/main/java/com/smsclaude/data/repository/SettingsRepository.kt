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

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val START_ON_BOOT = booleanPreferencesKey("start_on_boot")
        val FORWARD_DELAY = intPreferencesKey("forward_delay")
        val PREFIX = stringPreferencesKey("prefix")
        val SUFFIX = stringPreferencesKey("suffix")
        val IS_USER_STOPPED = booleanPreferencesKey("is_user_stopped")
        val IS_SERVICE_RUNNING = booleanPreferencesKey("is_service_running")
        val TOTAL_FORWARDED = intPreferencesKey("total_forwarded")
        val TODAY_FORWARDED = intPreferencesKey("today_forwarded")
        val LAST_FORWARDED_TIMESTAMP = longPreferencesKey("last_forwarded_timestamp")
        val LAST_FORWARDED_DATE = stringPreferencesKey("last_forwarded_date")
    }

    val settingsFlow: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            startOnBoot = prefs[START_ON_BOOT] ?: false,
            forwardDelay = prefs[FORWARD_DELAY] ?: 0,
            prefix = prefs[PREFIX] ?: "",
            suffix = prefs[SUFFIX] ?: "",
            isUserStopped = prefs[IS_USER_STOPPED] ?: true,
            isServiceRunning = prefs[IS_SERVICE_RUNNING] ?: false,
            totalForwarded = prefs[TOTAL_FORWARDED] ?: 0,
            todayForwarded = prefs[TODAY_FORWARDED] ?: 0,
            lastForwardedTimestamp = prefs[LAST_FORWARDED_TIMESTAMP] ?: 0L,
            lastForwardedDate = prefs[LAST_FORWARDED_DATE] ?: ""
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
        forwardDelay: Int,
        prefix: String,
        suffix: String
    ) {
        context.settingsDataStore.edit { prefs ->
            prefs[START_ON_BOOT] = startOnBoot
            prefs[FORWARD_DELAY] = forwardDelay
            prefs[PREFIX] = prefix
            prefs[SUFFIX] = suffix
        }
    }

    suspend fun incrementForwardedCount(today: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[TOTAL_FORWARDED] = (prefs[TOTAL_FORWARDED] ?: 0) + 1
            val lastDate = prefs[LAST_FORWARDED_DATE] ?: ""
            if (lastDate == today) {
                prefs[TODAY_FORWARDED] = (prefs[TODAY_FORWARDED] ?: 0) + 1
            } else {
                prefs[TODAY_FORWARDED] = 1
                prefs[LAST_FORWARDED_DATE] = today
            }
            prefs[LAST_FORWARDED_TIMESTAMP] = System.currentTimeMillis()
        }
    }
}
