package com.antonnikitin.smartreview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ReviewButtonStyle(
    val colors: ButtonColors,
    val textStyle: TextStyle,
    val border: BorderStroke?
)

data class ReviewInlineStyle(
    val titleTextStyle: TextStyle,
    val primaryButton: ReviewButtonStyle,
    val secondaryButton: ReviewButtonStyle,
    val spacing: Dp = 12.dp
)

@Composable
fun ReviewInlineBlock(
    state: ReviewUiState,
    step: ReviewStep,
    strings: ReviewStrings,
    style: ReviewInlineStyle,
    onAction: (ReviewUiAction) -> Unit,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal
) {
    if (state != ReviewUiState.Visible) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(style.spacing),
        horizontalAlignment = horizontalAlignment
    ) {
        when (step) {
            ReviewStep.SENTIMENT -> {
                Text(
                    text = strings.likeQuestion,
                    style = style.titleTextStyle,
                    textAlign = horizontalAlignmentToTextAlign(horizontalAlignment)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(style.spacing)
                ) {
                    Button(
                        onClick = { onAction(ReviewUiAction.Like) },
                        colors = style.primaryButton.colors,
                        border = style.primaryButton.border
                    ) {
                        Text(
                            text = strings.likePositive,
                            style = style.primaryButton.textStyle
                        )
                    }

                    OutlinedButton(
                        onClick = { onAction(ReviewUiAction.Dislike) },
                        colors = style.secondaryButton.colors,
                        border = style.secondaryButton.border
                    ) {
                        Text(
                            text = strings.likeNegative,
                            style = style.secondaryButton.textStyle
                        )
                    }
                }
            }

            ReviewStep.ASK_REVIEW -> {
                Text(
                    text = strings.rateQuestion,
                    style = style.titleTextStyle,
                    textAlign = horizontalAlignmentToTextAlign(horizontalAlignment)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(style.spacing)
                ) {
                    Button(
                        onClick = { onAction(ReviewUiAction.RateNow) },
                        colors = style.primaryButton.colors,
                        border = style.primaryButton.border
                    ) {
                        Text(
                            text = strings.rateNow,
                            style = style.primaryButton.textStyle
                        )
                    }

                    OutlinedButton(
                        onClick = { onAction(ReviewUiAction.RateLater) },
                        colors = style.secondaryButton.colors,
                        border = style.secondaryButton.border
                    ) {
                        Text(
                            text = strings.rateLater,
                            style = style.secondaryButton.textStyle
                        )
                    }

                    OutlinedButton(
                        onClick = { onAction(ReviewUiAction.RateNever) },
                        colors = style.secondaryButton.colors,
                        border = style.secondaryButton.border
                    ) {
                        Text(
                            text = strings.rateNever,
                            style = style.secondaryButton.textStyle
                        )
                    }
                }
            }
        }
    }
}

private fun horizontalAlignmentToTextAlign(horizontalAlignment: Alignment.Horizontal): TextAlign =
    when (horizontalAlignment) {
        Alignment.Start -> TextAlign.Start
        Alignment.CenterHorizontally -> TextAlign.Center
        Alignment.End -> TextAlign.End
        else -> TextAlign.Start
    }