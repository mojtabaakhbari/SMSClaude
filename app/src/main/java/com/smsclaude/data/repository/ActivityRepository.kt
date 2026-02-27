package com.smsclaude.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smsclaude.data.model.RecentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.activityDataStore: DataStore<Preferences> by preferencesDataStore(name = "activity")

class ActivityRepository(private val context: Context) {

    companion object {
        val ACTIVITY_KEY = stringPreferencesKey("recent_activity")
        const val MAX_ACTIVITY = 50
    }

    val activityFlow: Flow<List<RecentActivity>> = context.activityDataStore.data.map { prefs ->
        val json = prefs[ACTIVITY_KEY] ?: "[]"
        try {
            Json.decodeFromString<List<RecentActivity>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addActivity(activity: RecentActivity) {
        context.activityDataStore.edit { prefs ->
            val json = prefs[ACTIVITY_KEY] ?: "[]"
            val items = try {
                Json.decodeFromString<List<RecentActivity>>(json).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            items.add(0, activity)
            if (items.size > MAX_ACTIVITY) {
                items.subList(MAX_ACTIVITY, items.size).clear()
            }
            prefs[ACTIVITY_KEY] = Json.encodeToString(items)
        }
    }

    suspend fun clearActivity() {
        context.activityDataStore.edit { prefs ->
            prefs[ACTIVITY_KEY] = "[]"
        }
    }
}
