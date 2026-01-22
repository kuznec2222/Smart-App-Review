package com.antonnikitin.smartreview

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri

class SmartReviewImplementation(
    context: Context,
    private val config: SmartReviewConfig = SmartReviewConfig()
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
        Log.d("SmartReview", "requestReview() called with launcher: ${config.launcher}")

        return when (val launcher = config.launcher) {
            ReviewLauncher.InApp -> requestInAppReview(activity)
            is ReviewLauncher.PlayStore -> requestPlayStoreReview(activity, launcher.packageName)
        }
    }

    private suspend fun requestInAppReview(activity: Activity): Boolean {
        return try {
            Log.d("SmartReview", "InApp Review → requesting ReviewManager")

            val manager = ReviewManagerFactory.create(activity)

            Log.d("SmartReview", "InApp Review → requestReviewFlow()")
            val reviewInfo = manager.requestReviewFlow().await()

            Log.d("SmartReview", "InApp Review → launchReviewFlow()")
            manager.launchReviewFlow(activity, reviewInfo).await()

            Log.d("SmartReview", "InApp Review SUCCESS → applying optOut + cooldown")
            storage.markOptOut()
            storage.markPromptShown(now())
            _isReviewActive.value = false
            true
        } catch (e: Exception) {
            Log.d(
                "SmartReview",
                "InApp Review FAILED → cooldown only, error=${e.javaClass.simpleName}: ${e.message}"
            )
            storage.markPromptShown(now())
            false
        }
    }

    private suspend fun requestPlayStoreReview(activity: Activity, packageName: String): Boolean {
        return try {
            Log.d("SmartReview", "PlayStore Review → opening for package: $packageName")

            val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "market://details?id=$packageName&showAllReviews=true".toUri()
                setPackage("com.android.vending")
            }

            activity.startActivity(playStoreIntent)

            Log.d("SmartReview", "PlayStore Review → opened successfully")

            storage.markOptOut()
            storage.markPromptShown(now())
            _isReviewActive.value = false
            true

        } catch (_: ActivityNotFoundException) {
            Log.d("SmartReview", "PlayStore app not found → trying browser")

            return try {
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://play.google.com/store/apps/details?id=$packageName&showAllReviews=true".toUri()
                }
                activity.startActivity(browserIntent)

                Log.d("SmartReview", "PlayStore Review → opened in browser")

                storage.markOptOut()
                storage.markPromptShown(now())
                _isReviewActive.value = false
                true

            } catch (e: Exception) {
                Log.e("SmartReview", "PlayStore Review FAILED → ${e.javaClass.simpleName}: ${e.message}")
                storage.markPromptShown(now())
                false
            }
        } catch (e: Exception) {
            Log.e("SmartReview", "PlayStore Review FAILED → ${e.javaClass.simpleName}: ${e.message}")
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