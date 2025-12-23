package com.antonnikitin.smartreview

import android.content.Context
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.edit
import com.antonnikitin.smartreview.ReviewPrefs.LAST_PASSIVE_SHOWN_AT
import com.antonnikitin.smartreview.ReviewPrefs.PASSIVE_SHOWN_COUNT

internal class ReviewStorage(
    context: Context
) {

    private val ds = context.reviewDataStore

    suspend fun onAppLaunched(now: Long) {
        ds.edit { prefs ->
            val first = prefs[ReviewPrefs.FIRST_LAUNCH_AT]
            if (first == null) {
                prefs[ReviewPrefs.FIRST_LAUNCH_AT] = now
            }
            prefs[ReviewPrefs.LAUNCH_COUNT] =
                (prefs[ReviewPrefs.LAUNCH_COUNT] ?: 0) + 1
        }
    }

    suspend fun markPromptShown(now: Long) {
        ds.edit { prefs ->
            prefs[ReviewPrefs.LAST_PROMPT_AT] = now
            prefs[ReviewPrefs.PROMPT_COUNT] =
                (prefs[ReviewPrefs.PROMPT_COUNT] ?: 0) + 1
        }
    }


    suspend fun snapshot(): ReviewSnapshot {
        val prefs = ds.data.first()
        return ReviewSnapshot(
            firstLaunchAt = prefs[ReviewPrefs.FIRST_LAUNCH_AT],
            launchCount = prefs[ReviewPrefs.LAUNCH_COUNT] ?: 0,
            lastPromptAt = prefs[ReviewPrefs.LAST_PROMPT_AT],
            promptCount = prefs[ReviewPrefs.PROMPT_COUNT] ?: 0,
            optOut = prefs[ReviewPrefs.OPT_OUT] ?: false,
            sentimentPositive = prefs[ReviewPrefs.SENTIMENT_POSITIVE],
            passiveShownCount = prefs[PASSIVE_SHOWN_COUNT] ?: 0,
            lastPassiveShownAt = prefs[LAST_PASSIVE_SHOWN_AT],
        )
    }

    suspend fun markOptOut() {
        ds.edit { prefs ->
            prefs[ReviewPrefs.OPT_OUT] = true
        }
    }

    suspend fun markSentimentPositive(value: Boolean) {
        ds.edit { prefs ->
            prefs[ReviewPrefs.SENTIMENT_POSITIVE] = value
        }
    }

    suspend fun markPassiveShown(now: Long) {
        ds.edit { prefs ->
            val current = prefs[PASSIVE_SHOWN_COUNT] ?: 0
            prefs[PASSIVE_SHOWN_COUNT] = current + 1
            prefs[LAST_PASSIVE_SHOWN_AT] = now
        }
    }

    suspend fun clearPassiveShows() {
        ds.edit { prefs ->
            prefs.remove(PASSIVE_SHOWN_COUNT)
            prefs.remove(LAST_PASSIVE_SHOWN_AT)
        }
    }
}