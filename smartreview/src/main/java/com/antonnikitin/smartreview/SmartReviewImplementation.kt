package com.antonnikitin.smartreview

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.tasks.await

class SmartReviewImplementation(
    context: Context,
    config: SmartReviewConfig = SmartReviewConfig()
) : ReviewPrompter {
    private val policy: ReviewPolicy = ReviewPolicy(config.policy)

    private val appContext = context.applicationContext
    private val storage = ReviewStorage(appContext)

    override suspend fun onAppLaunched() {
        storage.onAppLaunched(now())
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
        return policy.shouldPrompt(snapshot, now())
    }


    override suspend fun markSentimentPositive(value: Boolean) {
        storage.markSentimentPositive(value)
    }

    override suspend fun markOptOut() {
        storage.markOptOut()
    }

    override suspend fun markPromptShown() {
        storage.markPromptShown(now())
    }

    override suspend fun markPassiveShown() {
        storage.markPassiveShown(now())
    }

    override suspend fun clearPassiveShows() {
        storage.clearPassiveShows()
    }

    override suspend fun requestReview(activity: Activity): Boolean {
        Log.d("SmartReview", "requestReview() called")

        val snapshot = storage.snapshot()
        Log.d(
            "SmartReview",
            "requestReview snapshot → " +
                    "launchCount=${snapshot.launchCount}, " +
                    "promptCount=${snapshot.promptCount}, " +
                    "lastPromptAt=${snapshot.lastPromptAt}, " +
                    "optOut=${snapshot.optOut}, " +
                    "passiveShownCount=${snapshot.passiveShownCount}, " +
                    "sentiment=${snapshot.sentimentPositive}"
        )

        if (!policy.shouldPrompt(snapshot, now())) {
            Log.d("SmartReview", "requestReview aborted → policy.shouldPrompt=false")
            return false
        }

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
}