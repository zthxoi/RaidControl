package ru.checkraid.app.data.prefs

import android.content.Context

data class UserSettings(
    val defaultInspector: String = "",
    val defaultCity: String = "",
    val notificationsEnabled: Boolean = true
)

class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadSettings(): UserSettings {
        return UserSettings(
            defaultInspector = prefs.getString(KEY_INSPECTOR, "") ?: "",
            defaultCity = prefs.getString(KEY_CITY, "") ?: "",
            notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        )
    }

    fun saveSettings(settings: UserSettings) {
        prefs.edit()
            .putString(KEY_INSPECTOR, settings.defaultInspector.trim())
            .putString(KEY_CITY, settings.defaultCity.trim())
            .putBoolean(KEY_NOTIFICATIONS, settings.notificationsEnabled)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "raid_control_prefs"
        private const val KEY_INSPECTOR = "default_inspector"
        private const val KEY_CITY = "default_city"
        private const val KEY_NOTIFICATIONS = "notifications_enabled"
    }
}