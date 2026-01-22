package com.antonnikitin.smartreview

/**
 * Public SDK configuration.
 */
data class SmartReviewConfig(
    val policy: ReviewPolicyConfig = ReviewPolicyConfig(),
    val launcher: ReviewLauncher = ReviewLauncher.InApp
)