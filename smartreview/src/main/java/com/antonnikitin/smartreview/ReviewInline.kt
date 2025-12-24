package com.antonnikitin.smartreview

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun ReviewInline(
    reviewPrompter: ReviewPrompter,
    activity: Activity,
    style: ReviewInlineStyle = DefaultReviewInlineStyle.material(),
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    strings: ReviewStrings = DefaultReviewStrings,
) {
    val scope = rememberCoroutineScope()

    val controller = remember {
        ReviewInlineController(reviewPrompter)
    }

    val uiState by controller.uiState.collectAsState()
    val step by controller.step.collectAsState()

    LaunchedEffect(Unit) {
        controller.evaluate()
    }

    ReviewInlineBlock(
        state = uiState,
        step = step,
        strings = strings,
        style = style,
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        onAction = { action ->
            scope.launch {
                controller.onAction(action)

                if (action == ReviewUiAction.RateNow) {
                    reviewPrompter.requestReview(activity)
                }
            }
        }
    )
}