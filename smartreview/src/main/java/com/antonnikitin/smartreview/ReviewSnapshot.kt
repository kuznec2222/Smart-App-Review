package com.antonnikitin.smartreview

data class ReviewSnapshot(
    val firstLaunchAt: Long?,
    val launchCount: Int,
    val lastPromptAt: Long?,
    val promptCount: Int,
    val optOut: Boolean,
    val sentimentPositive: Boolean?,
    val passiveShownCount: Int,
    val lastPassiveShownAt: Long?
)