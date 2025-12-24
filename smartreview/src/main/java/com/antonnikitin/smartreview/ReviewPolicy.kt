package com.antonnikitin.smartreview

import kotlin.Int
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Controls when the inline review UI is allowed to appear.
 */
data class ReviewPolicyConfig(
    val minLaunchCount: Int = 3,
    val minDaysSinceFirstLaunch: Duration = 2.days,
    val cooldown: Duration = 14.days,
    val maxPrompts: Int = 10,
    val maxPassiveShows: Int = 2
)

class ReviewPolicy(
    private val config: ReviewPolicyConfig = ReviewPolicyConfig()
) {

    fun shouldPrompt(
        snapshot: ReviewSnapshot,
        now: Long
    ): Boolean {

        // Пользователь отказался — больше НИКОГДА не показываем
        if (snapshot.optOut) return false

        val firstLaunchAt = snapshot.firstLaunchAt ?: return false

        // Слишком рано после установки
        if (now - firstLaunchAt < config.minDaysSinceFirstLaunch.inWholeMilliseconds) {
            return false
        }

        // Недостаточно запусков
        if (snapshot.launchCount < config.minLaunchCount) return false

        // Превышен лимит показов
        if (snapshot.promptCount >= config.maxPrompts) return false

        // Слишком много пассивных показов — не показываем
        if (snapshot.passiveShownCount >= config.maxPassiveShows) {
            return false
        }

        // Кулдаун
        val lastPromptAt = snapshot.lastPromptAt
        if (lastPromptAt != null &&
            now - lastPromptAt < config.cooldown.inWholeMilliseconds
        ) {
            return false
        }

        return true
    }
}