package com.antonnikitin.smartreview

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReviewInlineController(
    private val reviewPrompter: ReviewPrompter
) {

    private companion object {
        const val TAG = "SmartReview"
    }

    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Hidden)
    val uiState: StateFlow<ReviewUiState> = _uiState

    private val _step = MutableStateFlow(ReviewStep.SENTIMENT)
    val step: StateFlow<ReviewStep> = _step

    suspend fun evaluate() {
        Log.d(TAG, "evaluate() called")

        val shouldShow = reviewPrompter.shouldPrompt()
        Log.d(TAG, "shouldPrompt = $shouldShow")

        if (shouldShow) {
            reviewPrompter.markPassiveShown()

            val recheck = reviewPrompter.shouldPrompt()

            if (recheck) {
                _step.value = ReviewStep.SENTIMENT
                _uiState.value = ReviewUiState.Visible
                Log.d(TAG, "UI -> Visible, step = SENTIMENT")
            } else {
                _uiState.value = ReviewUiState.Hidden
                Log.d(TAG, "UI -> Hidden (limit reached after increment)")
            }
        } else {
            _uiState.value = ReviewUiState.Hidden
            Log.d(TAG, "UI -> Hidden")
        }
    }

    suspend fun onAction(action: ReviewUiAction) {
        Log.d(TAG, "onAction: $action")

        reviewPrompter.clearPassiveShows()
        Log.d(TAG, "Passive shows cleared due to user action")

        when (action) {

            // ---------- ШАГ 1 ----------
            ReviewUiAction.Like -> {
                Log.d(TAG, "Sentiment: LIKE")
                reviewPrompter.markSentimentPositive(true)
                _step.value = ReviewStep.ASK_REVIEW
                Log.d(TAG, "Step -> ASK_REVIEW")
            }

            ReviewUiAction.Dislike -> {
                Log.d(TAG, "Sentiment: DISLIKE -> opt-out")
                reviewPrompter.markSentimentPositive(false)
                reviewPrompter.markOptOut()
                _uiState.value = ReviewUiState.Hidden
                Log.d(TAG, "UI -> Hidden (opt-out)")
            }

            // ---------- ШАГ 2 ----------
            ReviewUiAction.RateNow -> {
                Log.d(TAG, "User chose RATE NOW")
                _uiState.value = ReviewUiState.Hidden
                Log.d(TAG, "UI -> Hidden (waiting for Play Review)")
            }

            ReviewUiAction.RateLater -> {
                Log.d(TAG, "User chose RATE LATER -> cooldown")
                reviewPrompter.markPromptShown()
                _uiState.value = ReviewUiState.Hidden
                Log.d(TAG, "UI -> Hidden (cooldown)")
            }

            ReviewUiAction.RateNever -> {
                Log.d(TAG, "User chose RATE NEVER -> opt-out")
                reviewPrompter.markOptOut()
                _uiState.value = ReviewUiState.Hidden
                Log.d(TAG, "UI -> Hidden (opt-out)")
            }
        }
    }
}