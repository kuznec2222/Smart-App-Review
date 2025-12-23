package com.antonnikitin.smartreview

sealed class ReviewUiState {
    data object Hidden : ReviewUiState()
    data object Visible : ReviewUiState()
}