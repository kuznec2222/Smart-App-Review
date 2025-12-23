package com.antonnikitin.smartappreview

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
import com.antonnikitin.smartreview.ReviewPrompter
import com.antonnikitin.smartreview.ReviewStrings
import com.antonnikitin.smartreview.ReviewUiAction
import com.antonnikitin.smartreview.SmartReviewImplementation
import kotlinx.coroutines.launch

class SampleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reviewPrompter = SmartReviewImplementation(this)

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
                textStyle = MaterialTheme.typography.labelLarge
            ),
            secondaryButton = ReviewButtonStyle(
                colors = ButtonDefaults.outlinedButtonColors(),
                textStyle = MaterialTheme.typography.labelMedium
            )
        )

        ReviewInline(
            reviewPrompter = reviewPrompter,
            activity = activity,
            strings = AppReviewStrings(LocalContext.current),
            style = customStyle
        )

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