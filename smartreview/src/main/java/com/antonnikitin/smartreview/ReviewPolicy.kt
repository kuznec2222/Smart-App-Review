package com.antonnikitin.smartreview

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class ReviewPolicy(
    private val minDaysSinceFirstLaunch: Duration = 0.days,
    private val minLaunchCount: Int = 2,
    private val cooldown: Duration = 2.days,
    private val maxPrompts: Int = 10,
    private val maxPassiveShows: Int = 2
) {

    fun shouldPrompt(
        snapshot: ReviewSnapshot,
        now: Long
    ): Boolean {

        // Пользователь отказался — больше НИКОГДА не показываем
        if (snapshot.optOut) return false

        val firstLaunchAt = snapshot.firstLaunchAt ?: return false

        // Слишком рано после установки
        if (now - firstLaunchAt < minDaysSinceFirstLaunch.inWholeMilliseconds) {
            return false
        }

        // Недостаточно запусков
        if (snapshot.launchCount < minLaunchCount) return false

        // Превышен лимит показов
        if (snapshot.promptCount >= maxPrompts) return false

        // Слишком много пассивных показов — не показываем
        if (snapshot.passiveShownCount >= maxPassiveShows) {
            return false
        }

        // Кулдаун
        val lastPromptAt = snapshot.lastPromptAt
        if (lastPromptAt != null &&
            now - lastPromptAt < cooldown.inWholeMilliseconds
        ) {
            return false
        }

        return true
    }
}