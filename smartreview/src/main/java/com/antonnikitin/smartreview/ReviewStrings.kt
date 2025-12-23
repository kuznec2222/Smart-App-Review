package com.antonnikitin.smartreview

import androidx.compose.runtime.Immutable

@Immutable
interface ReviewStrings {

    /* Step 1 — sentiment */
    val likeQuestion: String
    val likePositive: String
    val likeNegative: String

    /* Step 2 — review intent */
    val rateQuestion: String
    val rateNow: String
    val rateLater: String
    val rateNever: String
}