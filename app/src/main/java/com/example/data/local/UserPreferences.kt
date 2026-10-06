package com.example.data.local

import android.content.Context

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "jobpulse_user")

/** Small local preferences, independent of the replaceable recruitment cache. */
interface UserPreferences {
    val bookmarks: Flow<Set<Int>>
    val darkTheme: Flow<Boolean>
    suspend fun saveBookmarks(ids: Set<Int>)
    suspend fun saveDarkTheme(dark: Boolean)
    fun alertPreference(key: String): Flow<Boolean>
    suspend fun saveAlertPreference(key: String, enabled: Boolean)
}

class AndroidUserPreferences(context: Context) : UserPreferences {
    private val dataStore = context.applicationContext.dataStore
    private val BOOKMARKS_KEY = stringSetPreferencesKey("bookmarks")
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    override val bookmarks: Flow<Set<Int>> = dataStore.data.map { prefs ->
        prefs[BOOKMARKS_KEY]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }
    
    override val darkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DARK_THEME_KEY] ?: true
    }

    override suspend fun saveBookmarks(ids: Set<Int>) {
        dataStore.edit { prefs ->
            prefs[BOOKMARKS_KEY] = ids.map { it.toString() }.toSet()
        }
    }

    override suspend fun saveDarkTheme(dark: Boolean) {
        dataStore.edit { prefs ->
            prefs[DARK_THEME_KEY] = dark
        }
    }

    override fun alertPreference(key: String): Flow<Boolean> {
        val prefKey = booleanPreferencesKey("alert_$key")
        return dataStore.data.map { prefs -> prefs[prefKey] ?: false }
    }

    override suspend fun saveAlertPreference(key: String, enabled: Boolean) {
        val prefKey = booleanPreferencesKey("alert_$key")
        dataStore.edit { prefs -> prefs[prefKey] = enabled }
    }
}
