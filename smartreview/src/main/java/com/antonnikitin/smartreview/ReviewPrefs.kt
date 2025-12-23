package com.antonnikitin.smartreview

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

internal object ReviewPrefs {
    val FIRST_LAUNCH_AT = longPreferencesKey("first_launch_at")
    val LAUNCH_COUNT = intPreferencesKey("launch_count")
    val LAST_PROMPT_AT = longPreferencesKey("last_prompt_at")
    val PROMPT_COUNT = intPreferencesKey("prompt_count")
    val OPT_OUT = booleanPreferencesKey("opt_out")
    val SENTIMENT_POSITIVE = booleanPreferencesKey("sentiment_positive")
    val PASSIVE_SHOWN_COUNT = intPreferencesKey("passive_shown_count")
    val LAST_PASSIVE_SHOWN_AT = longPreferencesKey("last_passive_shown_at")
}