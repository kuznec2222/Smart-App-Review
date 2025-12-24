package com.antonnikitin.smartreview

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class SmartReviewImplementation(
    context: Context,
    config: SmartReviewConfig = SmartReviewConfig()
) : ReviewPrompter {
    private val policy: ReviewPolicy = ReviewPolicy(config.policy)

    private val appContext = context.applicationContext
    private val storage = ReviewStorage(appContext)
    private val _willShowReview = MutableStateFlow(false)
    override val willShowReview: StateFlow<Boolean> = _willShowReview
    private val _isReviewActive = MutableStateFlow(false)
    override val isReviewActive: StateFlow<Boolean> = _isReviewActive

    override suspend fun onAppLaunched() {
        storage.onAppLaunched(now())
        recomputeWillShowReview()
    }

    override suspend fun shouldPrompt(): Boolean {
        val snapshot = storage.snapshot()
        Log.d(
            "SmartReview",
            buildString {
                append("Storage snapshot → ")
                append("firstLaunchAt=").append(snapshot.firstLaunchAt).append(", ")
                append("launchCount=").append(snapshot.launchCount).append(", ")
                append("lastPromptAt=").append(snapshot.lastPromptAt).append(", ")
                append("promptCount=").append(snapshot.promptCount).append(", ")
                append("optOut=").append(snapshot.optOut).append(", ")
                append("passiveShownCount=").append(snapshot.passiveShownCount).append(", ")
                append("sentiment=").append(snapshot.sentimentPositive)
            }
        )
        val shouldPrompt = policy.shouldPrompt(snapshot, now())
        _isReviewActive.value = shouldPrompt
        return shouldPrompt
    }


    override suspend fun markSentimentPositive(value: Boolean) {
        storage.markSentimentPositive(value)
    }

    override suspend fun markOptOut() {
        storage.markOptOut()
        _isReviewActive.value = false
    }

    override suspend fun markPromptShown() {
        storage.markPromptShown(now())
        _isReviewActive.value = false
    }

    override suspend fun markPassiveShown() {
        storage.markPassiveShown(now())
    }

    override suspend fun clearPassiveShows() {
        storage.clearPassiveShows()
    }

    override suspend fun requestReview(activity: Activity): Boolean {
        Log.d("SmartReview", "requestReview() called")
        return try {
            Log.d("SmartReview", "requestReview → requesting ReviewManager")

            val manager = ReviewManagerFactory.create(activity)

            Log.d("SmartReview", "requestReview → requestReviewFlow()")
            val reviewInfo = manager.requestReviewFlow().await()

            Log.d("SmartReview", "requestReview → launchReviewFlow()")
            manager.launchReviewFlow(activity, reviewInfo).await()

            Log.d("SmartReview", "requestReview SUCCESS → applying optOut + cooldown")
            storage.markOptOut()
            storage.markPromptShown(now())
            _isReviewActive.value = false
            true
        } catch (e: Exception) {
            Log.d(
                "SmartReview",
                "requestReview FAILED → cooldown only, error=${e.javaClass.simpleName}: ${e.message}"
            )

            storage.markPromptShown(now())
            false
        }
    }

    private fun now(): Long = System.currentTimeMillis()

    private suspend fun recomputeWillShowReview() {
        val snapshot = storage.snapshot()
        _willShowReview.value = policy.shouldPrompt(snapshot, now())
    }
}