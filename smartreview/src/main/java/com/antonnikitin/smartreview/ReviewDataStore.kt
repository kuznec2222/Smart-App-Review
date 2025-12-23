package com.antonnikitin.smartreview

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal val Context.reviewDataStore by preferencesDataStore(
    name = "smart_review_prefs"
)