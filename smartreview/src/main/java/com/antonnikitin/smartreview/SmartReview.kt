package com.antonnikitin.smartreview

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface ReviewPrompter {

    /**
     * Should be called on app start.
     * Increments launch counter and sets firstLaunchAt if needed.
     */
    suspend fun onAppLaunched()

    /**
     * Returns whether the inline review prompt
     * can be shown at this moment.
     */
    suspend fun shouldPrompt(): Boolean

    /**
     * User explicitly expressed sentiment about the app.
     *
     * @param value true if user likes the app, false otherwise
     */
    suspend fun markSentimentPositive(value: Boolean)

    /**
     * User opted out from any future review prompts.
     * After this call, the prompt must never be shown again.
     */
    suspend fun markOptOut()

    /**
     * Marks that a review prompt attempt occurred.
     *
     * Used to start or update the regular cooldown
     * (for example after "Later" or a passive limit).
     */
    suspend fun markPromptShown()

    /**
     * Inline review block was shown,
     * but the user did not interact with it.
     *
     * Used to track passive impressions.
     */
    suspend fun markPassiveShown()

    /**
     * User interacted with the inline prompt.
     * Clears passive impression counters.
     */
    suspend fun clearPassiveShows()

    /**
     * Requests Google Play In-App Review flow.
     *
     * @return true if the request was completed successfully
     * (Google may still decide not to show the dialog).
     */
    suspend fun requestReview(activity: Activity): Boolean

    /**
     * Emits true when the review prompt
     * should be shown in the UI slot.
     */
    val willShowReview: StateFlow<Boolean>

    /**
     * Indicates whether the review prompt should currently be shown.
     *
     * This is a decision flag, not a reflection of the actual visibility
     * of the Google In-App Review dialog.
     */
    val isReviewActive: StateFlow<Boolean>
}