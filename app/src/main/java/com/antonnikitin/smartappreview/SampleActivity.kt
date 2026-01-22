package com.antonnikitin.smartappreview

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.antonnikitin.smartreview.ReviewButtonStyle
import com.antonnikitin.smartreview.ReviewInline
import com.antonnikitin.smartreview.ReviewInlineBlock
import com.antonnikitin.smartreview.ReviewInlineController
import com.antonnikitin.smartreview.ReviewInlineStyle
import com.antonnikitin.smartreview.ReviewLauncher
import com.antonnikitin.smartreview.ReviewPolicyConfig
import com.antonnikitin.smartreview.ReviewPrompter
import com.antonnikitin.smartreview.ReviewStrings
import com.antonnikitin.smartreview.ReviewUiAction
import com.antonnikitin.smartreview.SmartReviewConfig
import com.antonnikitin.smartreview.SmartReviewImplementation
import com.antonnikitin.smartreview.playStore
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days

class SampleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reviewPrompter = SmartReviewImplementation(
            context = this,
            config = SmartReviewConfig(
                launcher = ReviewLauncher.playStore(applicationContext),
                policy = ReviewPolicyConfig(
                    minDaysSinceFirstLaunch = 0.days,
                    maxPassiveShows = 100
                )
            )
        )

        lifecycleScope.launch {
            reviewPrompter.onAppLaunched()
        }

        setContent {
            MaterialTheme {
                SampleScreen(
                    reviewPrompter = reviewPrompter,
                    activity = this
                )
            }
        }
    }
}

@Composable
fun SampleScreen(
    reviewPrompter: ReviewPrompter,
    activity: Activity
) {
    val willShowReview by reviewPrompter.willShowReview.collectAsState()
    val isReviewActive by reviewPrompter.isReviewActive.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text(
            text = "Sample Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        val customStyle = ReviewInlineStyle(
            titleTextStyle = MaterialTheme.typography.headlineSmall,
            primaryButton = ReviewButtonStyle(
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                textStyle = MaterialTheme.typography.labelLarge,
                border = null
            ),
            secondaryButton = ReviewButtonStyle(
                colors = ButtonDefaults.outlinedButtonColors(),
                textStyle = MaterialTheme.typography.labelMedium,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            )
        )

        ReviewInline(
            reviewPrompter = reviewPrompter,
            activity = activity,
            strings = AppReviewStrings(LocalContext.current),
            style = customStyle,
            horizontalAlignment = Alignment.End
        )

//        when {
//            willShowReview -> {
//                ReviewInline(
//                    reviewPrompter = reviewPrompter,
//                    activity = activity,
//                    strings = AppReviewStrings(LocalContext.current),
//                    style = customStyle,
//                    horizontalAlignment = Alignment.End
//                )
//            }
//            else -> {
//                Text("Admob Banner, etc.")
//            }
//        }

        HorizontalDivider()

        Text("Review is active: $isReviewActive")

        HorizontalDivider()

        Text(
            text = "Основной контент экрана ниже",
            style = MaterialTheme.typography.bodyLarge
        )

        repeat(10) {
            Text("Контентный элемент #$it")
        }
    }
}

class AppReviewStrings(context: Context) : ReviewStrings {

    private val r = context.resources

    override val likeQuestion = r.getString(R.string.review_like_question)
    override val likePositive = r.getString(R.string.review_like_yes)
    override val likeNegative = r.getString(R.string.review_like_no)

    override val rateQuestion = r.getString(R.string.review_rate_question)
    override val rateNow = r.getString(R.string.review_rate_now)
    override val rateLater = r.getString(R.string.review_rate_later)
    override val rateNever = r.getString(R.string.review_rate_never)
}