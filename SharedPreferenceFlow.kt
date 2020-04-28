package com.daikaz.shared.preferences.coroutines.flow


import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun SharedPreferences.put(key: String, value: String) {
    this.edit().putString(key, value).apply()
}

fun SharedPreferences.put(key: String, value: Boolean) {
    this.edit().putBoolean(key, value).apply()
}

fun SharedPreferences.put(key: String, value: Long) {
    this.edit().putLong(key, value).apply()
}

fun SharedPreferences.put(key: String, value: Set<String>) {
    this.edit().putStringSet(key, value).apply()
}

fun SharedPreferences.put(key: String, value: Float) {
    this.edit().putFloat(key, value).apply()
}

fun SharedPreferences.put(key: String, value: Int) {
    this.edit().putInt(key, value).apply()
}

fun SharedPreferences.remove(key: String) {
    this.edit().remove(key).apply()
}

fun SharedPreferences.getString(key: String) = this.getString(key, "") ?: ""

fun SharedPreferences.getBoolean(key: String) = this.getBoolean(key, false)

fun SharedPreferences.getFloat(key: String) = this.getFloat(key, 0f)

fun SharedPreferences.getInt(key: String) = this.getInt(key, 0)

fun SharedPreferences.getLong(key: String) = this.getLong(key, 0L)

fun SharedPreferences.getStringSet(key: String) = this.getStringSet(key, HashSet<String>()) ?: HashSet<String>()

private fun <T> asFlow(sharedPrefs: SharedPreferences, key: String, get: ((key: String) -> T)): Flow<T> = callbackFlow {
    val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        if (key == k) {
            this@callbackFlow.sendBlocking(get.invoke(key))
        }
    }
    sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    awaitClose {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}

fun SharedPreferences.stringFlow(key: String) = asFlow<String>(this, key, ::getString)

fun SharedPreferences.booleanFlow(key: String) = asFlow<Boolean>(this, key, ::getBoolean)

fun SharedPreferences.floatFlow(key: String) = asFlow<Float>(this, key, ::getFloat)

fun SharedPreferences.longFlow(key: String) = asFlow<Long>(this, key, ::getLong)

fun SharedPreferences.stringSetFlow(key: String) = asFlow<Set<String>>(this, key, ::getStringSet)
