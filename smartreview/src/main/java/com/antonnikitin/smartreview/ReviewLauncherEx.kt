package com.antonnikitin.smartreview

import android.content.Context

fun ReviewLauncher.Companion.playStore(context: Context): ReviewLauncher.PlayStore {
    return ReviewLauncher.PlayStore(
        packageName = context.packageName
    )
}