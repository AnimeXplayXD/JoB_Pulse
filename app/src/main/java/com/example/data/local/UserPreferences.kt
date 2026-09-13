package com.example.data.local

import android.content.Context

/** Small local preferences, independent of the replaceable recruitment cache. */
interface UserPreferences {
    fun bookmarks(): Set<Int>
    fun darkTheme(): Boolean
    fun saveBookmarks(ids: Set<Int>)
    fun saveDarkTheme(dark: Boolean)
    fun alertPreference(key: String): Boolean
    fun saveAlertPreference(key: String, enabled: Boolean)
}

class AndroidUserPreferences(context: Context) : UserPreferences {
    private val prefs = context.applicationContext.getSharedPreferences("jobpulse_user", Context.MODE_PRIVATE)
    override fun bookmarks(): Set<Int> = prefs.getStringSet("bookmarks", emptySet()).orEmpty().mapNotNull { it.toIntOrNull() }.toSet()
    override fun darkTheme(): Boolean = prefs.getBoolean("dark_theme", true)
    override fun saveBookmarks(ids: Set<Int>) { prefs.edit().putStringSet("bookmarks", ids.map { it.toString() }.toSet()).apply() }
    override fun saveDarkTheme(dark: Boolean) { prefs.edit().putBoolean("dark_theme", dark).apply() }
    override fun alertPreference(key: String): Boolean = prefs.getBoolean("alert_$key", false)
    override fun saveAlertPreference(key: String, enabled: Boolean) { prefs.edit().putBoolean("alert_$key", enabled).apply() }
}
