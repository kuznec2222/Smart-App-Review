package com.antonnikitin.smartreview

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Default visual style for ReviewInline.
 *
 * Uses MaterialTheme tokens but does NOT apply them implicitly.
 * Host application must explicitly pass this style if desired.
 */
object DefaultReviewInlineStyle {

    @Composable
    fun material(): ReviewInlineStyle {
        return ReviewInlineStyle(
            titleTextStyle = MaterialTheme.typography.titleMedium,
            primaryButton = ReviewButtonStyle(
                colors = ButtonDefaults.buttonColors(),
                textStyle = MaterialTheme.typography.labelLarge,
                border = null
            ),
            secondaryButton = ReviewButtonStyle(
                colors = ButtonDefaults.outlinedButtonColors(),
                textStyle = MaterialTheme.typography.labelLarge,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ),
            spacing = 12.dp
        )
    }
}