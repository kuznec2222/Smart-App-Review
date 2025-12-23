package com.antonnikitin.smartreview

sealed class ReviewUiAction {

    // Шаг 1 — отношение к приложению
    object Like : ReviewUiAction()
    object Dislike : ReviewUiAction()

    // Шаг 2 — готовность поставить оценку
    object RateNow : ReviewUiAction()
    object RateLater : ReviewUiAction()
    object RateNever : ReviewUiAction()
}