

# Smart App Review

**Smart App Review** is a lightweight Android library that displays an **inline in‑app review prompt** based on real user behavior instead of intrusive dialogs.

The goal is to ask for a Google Play review **at the right moment**, without breaking user flow and without annoying repeated prompts.

---

## ✨ Key Features

- Inline review prompt (no dialogs, no interruptions)
- Google Play In‑App Review API integration
- Usage‑based policy (launch count, cooldowns, passive impressions)
- Opt‑out and sentiment handling
- Fully customizable UI via style‑based API
- Localization‑ready (no hardcoded strings)
- Jetpack Compose first
- No forced MaterialTheme defaults

---

## 📦 Installation

*(Distribution method TBD)*

---

## 🚀 Basic Usage

### 1️⃣ Create ReviewPrompter

```kotlin
val reviewPrompter = SmartReviewImplementation(context)

lifecycleScope.launch {
    reviewPrompter.onAppLaunched()
}
```

Call `onAppLaunched()` once per app start.

---

### 2️⃣ Show ReviewInline in Compose

```kotlin
ReviewInline(
    reviewPrompter = reviewPrompter,
    activity = activity,
    strings = AppReviewStrings(context),
    style = DefaultReviewInlineStyle.material()
)
```

The library automatically decides **whether** and **when** the review prompt should appear.

---

## 🎨 UI Customization (Style‑based API)

The library controls **logic and flow**.  
The host application controls **visual appearance**.

### Style models

```kotlin
data class ReviewInlineStyle(
    val titleTextStyle: TextStyle,
    val primaryButton: ReviewButtonStyle,
    val secondaryButton: ReviewButtonStyle,
    val spacing: Dp = 12.dp
)

data class ReviewButtonStyle(
    val colors: ButtonColors,
    val textStyle: TextStyle
)
```

### Example custom style

```kotlin
val customStyle = ReviewInlineStyle(
    titleTextStyle = MaterialTheme.typography.headlineSmall,
    primaryButton = ReviewButtonStyle(
        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
        textStyle = MaterialTheme.typography.labelLarge
    ),
    secondaryButton = ReviewButtonStyle(
        colors = ButtonDefaults.outlinedButtonColors(),
        textStyle = MaterialTheme.typography.labelMedium
    )
)
```

---

## 🌍 Localization

The library does **not** use `strings.xml`.

Instead, you provide your own implementation of `ReviewStrings`:

```kotlin
class AppReviewStrings(context: Context) : ReviewStrings {

    private val r = context.resources

    override val likeQuestion = r.getString(R.string.review_like_question)
    override val likePositive = r.getString(R.string.review_like_yes)
    override val likeNegative = r.getString(R.string.review_like_no)

    override val rateQuestion = r.getString(R.string.review_rate_question)
    override val rateNow = r.getString(R.string.review_rate_now)
    override val rateLater = r.getString(R.string.review_rate_later)
    override val rateNever = r.getString(R.string.review_rate_never)
}
```

This makes localization explicit and SDK‑friendly.

---

## 🧠 Review Flow Logic

Default behavior:

1. Library evaluates whether the prompt should be shown
2. User is asked: **“Do you like the app?”**
3. If **No** → user is opted out permanently
4. If **Yes** → user is asked to rate the app
5. Available actions:
   - **Rate now** → Google Play review flow
   - **Later** → cooldown applied
   - **Never** → opt‑out
6. Passive impressions are tracked
7. After a passive limit, the prompt enters cooldown automatically

All decisions are handled internally by the library.

---

## 🧩 What the Library Controls

- When the prompt appears
- Cooldowns and frequency
- Passive impression limits
- Sentiment and opt‑out logic
- Google Play review request

---

## 🎯 What the App Controls

- Localization
- Typography
- Button styles
- Colors
- Layout containers (Card, Surface, animations)

---

## ❌ What This Library Does NOT Do

- No modal dialogs
- No forced UI themes
- No implicit Material defaults
- No ViewModel coupling
- No intrusive prompts

---

## 🛠 API Stability

- Designed for **v1.0 stability**
- Style‑based customization is preferred over slot‑based DSL
- Slot‑based API may be introduced in a future major version

---

## 👤 Author

**Anton Nikitin**