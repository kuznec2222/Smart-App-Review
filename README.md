# Smart App Review

**Smart App Review** is a lightweight Android library that displays an **inline in-app review prompt** based on real user behavior instead of intrusive dialogs.

The goal is to ask for a Google Play review **at the right moment**, without breaking user flow and without annoying repeated prompts.

---

## ✨ Key Features

* Inline review prompt (no dialogs, no interruptions)
* Google Play In-App Review API integration
* Direct link to app page in Google Play Store
* Usage-based policy (launch count, cooldowns, passive impressions)
* Opt-out and sentiment handling
* Fully customizable UI via style-based API
* Localization-ready (no hardcoded strings)
* Jetpack Compose first
* No forced MaterialTheme defaults

---

## 📦 Installation

The library is distributed via **JitPack**.

In your **settings.gradle** or **settings.gradle.kts**:
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

In your app's **build.gradle.kts**:
```gradle
dependencies {
    implementation("com.github.kuznec2222:Smart-App-Review:v1.2.0")
}
```

---

## 🚀 Quick Start

### 1️⃣ Create ReviewPrompter
```kotlin
val reviewPrompter = SmartReviewImplementation(
    context = applicationContext,
    config = SmartReviewConfig(
        policy = ReviewPolicyConfig(
            minLaunchCount = 3,
            cooldown = 14.days
        )
    )
)

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

## 🎯 Review Launch Strategies

### In-App Review (Default)

Uses Google's native in-app review dialog. The most seamless UX, but Google controls when/if the dialog actually appears.
```kotlin
val reviewPrompter = SmartReviewImplementation(
    context = context,
    config = SmartReviewConfig(
        launcher = ReviewLauncher.InApp  // Default
    )
)
```

**Pros:**
- ✅ Native UX, no context switching
- ✅ Higher conversion rates
- ✅ User stays in the app

**Cons:**
- ⚠️ Google quota limits apply
- ⚠️ Google may decide not to show the dialog
- ⚠️ Only rating, no text review

---

### Play Store Direct Link

Opens the app's page directly in Google Play Store, allowing users to leave detailed reviews.
```kotlin
val reviewPrompter = SmartReviewImplementation(
    context = context,
    config = SmartReviewConfig(
        launcher = ReviewLauncher.PlayStore(
            packageName = "com.yourcompany.yourapp"
        )
    )
)
```

Or use the current app's package automatically:
```kotlin
val reviewPrompter = SmartReviewImplementation(
    context = context,
    config = SmartReviewConfig(
        launcher = ReviewLauncher.PlayStore(context.packageName)
    )
)
```

**Pros:**
- ✅ Always works (no quota limits)
- ✅ Users can write detailed text reviews
- ✅ Fallback to browser if Play Store not installed

**Cons:**
- ⚠️ Takes user out of the app
- ⚠️ Slightly lower conversion rates

---

## ⚙️ Configuration

All review behavior is controlled via `SmartReviewConfig`.

Each policy parameter is optional and has a reasonable default value.
```kotlin
SmartReviewImplementation(
    context = context,
    config = SmartReviewConfig(
        policy = ReviewPolicyConfig(
            minLaunchCount = 3,
            minDaysSinceFirstLaunch = 2.days,
            cooldown = 14.days,
            maxPrompts = 10,
            maxPassiveShows = 2
        ),
        launcher = ReviewLauncher.InApp  // or ReviewLauncher.PlayStore(packageName)
    )
)
```

You can override only the parameters you need:
```kotlin
SmartReviewImplementation(
    context = context,
    config = SmartReviewConfig(
        policy = ReviewPolicyConfig(
            cooldown = 30.days
        ),
        launcher = ReviewLauncher.PlayStore(context.packageName)
    )
)
```

If no configuration is provided, all default values are used.

---

### Policy Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `minLaunchCount` | `3` | Minimum number of app launches before showing prompt |
| `minDaysSinceFirstLaunch` | `2.days` | Minimum days since first app launch |
| `cooldown` | `14.days` | Time between review prompts |
| `maxPrompts` | `10` | Maximum lifetime review prompts |
| `maxPassiveShows` | `2` | Maximum passive impressions before cooldown |

---

## 🎨 UI Customization

The library controls **logic and flow**.  
The host application controls **visual appearance**.

### Style Models
```kotlin
data class ReviewInlineStyle(
    val titleTextStyle: TextStyle,
    val primaryButton: ReviewButtonStyle,
    val secondaryButton: ReviewButtonStyle,
    val spacing: Dp = 12.dp
)

data class ReviewButtonStyle(
    val colors: ButtonColors,
    val textStyle: TextStyle,
    val border: BorderStroke?
)
```

### Custom Style Example
```kotlin
val customStyle = ReviewInlineStyle(
    titleTextStyle = MaterialTheme.typography.headlineSmall,
    primaryButton = ReviewButtonStyle(
        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
        textStyle = MaterialTheme.typography.labelLarge,
        border = null
    ),
    secondaryButton = ReviewButtonStyle(
        colors = ButtonDefaults.outlinedButtonColors(),
        textStyle = MaterialTheme.typography.labelMedium,
        border = BorderStroke(1.dp, Color.Gray)
    )
)

ReviewInline(
    reviewPrompter = reviewPrompter,
    activity = activity,
    style = customStyle
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

Or use the built-in defaults:
```kotlin
ReviewInline(
    reviewPrompter = reviewPrompter,
    activity = activity,
    strings = DefaultReviewStrings  // English strings
)
```

This makes localization explicit and SDK-friendly.

---

## 🧠 Review Flow Logic

The flow is controlled by `ReviewPolicyConfig` defaults unless overridden.

### Default Behavior

1. Library evaluates whether the prompt should be shown
2. User is asked: **"Do you like this app?"**
3. If **No** → user is opted out permanently
4. If **Yes** → user is asked to rate the app
5. Available actions:
    * **Rate now** → Google Play review flow (InApp) or Play Store page (PlayStore)
    * **Later** → cooldown applied
    * **Never** → opt-out
6. Passive impressions are tracked
7. After reaching passive limit, cooldown starts automatically

All decisions are handled internally by the library.

---

## 🔄 Passive Shows Explained

A **passive show** occurs when:
- The ReviewInline UI is visible
- But the user doesn't interact with it

This prevents annoying users with prompts they're ignoring.

**Behavior:**
```
Launch 1: UI shown → user ignores → passive count = 1
Launch 2: UI shown → user ignores → passive count = 2
Launch 3: UI NOT shown (maxPassiveShows reached)
         → cooldown starts automatically
```

After cooldown expires, the counter resets and the prompt can appear again.

---

## 📊 Comparison: InApp vs PlayStore

| Feature | InApp Review | PlayStore Link |
|---------|-------------|----------------|
| **User Experience** | ⭐⭐⭐⭐⭐ Seamless | ⭐⭐⭐ Leaves app |
| **Control** | ⭐⭐ Google decides | ⭐⭐⭐⭐⭐ Always works |
| **Conversion Rate** | ⭐⭐⭐⭐ Higher | ⭐⭐⭐ Lower |
| **Reliability** | ⭐⭐⭐ Quota limits | ⭐⭐⭐⭐⭐ Always available |
| **Review Detail** | ⭐⭐⭐ Rating only | ⭐⭐⭐⭐⭐ Rating + text |
| **Quota Limits** | ✅ Yes | ❌ No |

### When to Use InApp Review
- You want the highest conversion rates
- You're okay with Google's quota system
- You prefer seamless UX

### When to Use PlayStore Link
- You want guaranteed availability
- You want users to leave detailed text reviews
- You need predictable behavior

---

## 🛠 Advanced Usage

### Debug Mode

For testing, you can temporarily bypass all restrictions:
```kotlin
// In your debug build
val reviewPrompter = SmartReviewImplementation(
    context = context,
    config = SmartReviewConfig(
        policy = ReviewPolicyConfig(
            minLaunchCount = 1,
            minDaysSinceFirstLaunch = 0.seconds,
            cooldown = 0.seconds  // No cooldown in debug
        )
    )
)
```

### Custom Positioning

Control the UI alignment:
```kotlin
ReviewInline(
    reviewPrompter = reviewPrompter,
    activity = activity,
    horizontalAlignment = Alignment.Start,  // Left-aligned
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
)
```

### Programmatic Control

You can manually check if the prompt should be shown:
```kotlin
lifecycleScope.launch {
    if (reviewPrompter.shouldPrompt()) {
        // Show custom UI or trigger at specific moment
    }
}

// Observe state reactively
reviewPrompter.willShowReview.collect { shouldShow ->
    // React to state changes
}
```

---

## 📱 Complete Example
```kotlin
class MainActivity : ComponentActivity() {
    
    private lateinit var reviewPrompter: ReviewPrompter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        reviewPrompter = SmartReviewImplementation(
            context = applicationContext,
            config = SmartReviewConfig(
                policy = ReviewPolicyConfig(
                    minLaunchCount = 5,
                    minDaysSinceFirstLaunch = 3.days,
                    cooldown = 30.days,
                    maxPassiveShows = 3
                ),
                launcher = ReviewLauncher.InApp
                // or: ReviewLauncher.PlayStore(packageName)
            )
        )
        
        lifecycleScope.launch {
            reviewPrompter.onAppLaunched()
        }
        
        setContent {
            MyAppTheme {
                Scaffold { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Your app content
                        MyAppContent()
                        
                        // Review prompt at bottom
                        ReviewInline(
                            reviewPrompter = reviewPrompter,
                            activity = this@MainActivity,
                            strings = AppReviewStrings(this@MainActivity),
                            style = DefaultReviewInlineStyle.material(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
```

---

## 🛡️ Best Practices

### When to Show the Prompt

✅ **Good moments:**
- After user completes a key action successfully
- On the main screen after several uses
- In settings page (non-intrusive)

❌ **Avoid:**
- During onboarding
- Right after app install
- During critical user flows (checkout, signup)
- On error/crash screens

### Recommended Settings

**Conservative (B2B apps, professional tools):**
```kotlin
ReviewPolicyConfig(
    minLaunchCount = 7,
    minDaysSinceFirstLaunch = 7.days,
    cooldown = 60.days,
    maxPassiveShows = 2
)
```

**Balanced (most consumer apps):**
```kotlin
ReviewPolicyConfig(
    minLaunchCount = 3,
    minDaysSinceFirstLaunch = 2.days,
    cooldown = 14.days,
    maxPassiveShows = 2
)
```

**Aggressive (games, viral apps):**
```kotlin
ReviewPolicyConfig(
    minLaunchCount = 2,
    minDaysSinceFirstLaunch = 1.days,
    cooldown = 7.days,
    maxPassiveShows = 3
)
```

---

## 🔧 Migration from v1.x

### What's New in v2.0

1. **Play Store Direct Link Support:**
    - New `ReviewLauncher.PlayStore` option
    - Fallback to browser if Play Store not installed
    - Guaranteed availability (no quota limits)

2. **Race Condition Fix:**
    - Improved passive show tracking
    - Automatic cooldown when passive limit reached
    - More reliable prompt behavior

3. **Enhanced Logging:**
    - Detailed debug logs
    - Clear indication of launcher type used
    - Better troubleshooting

### Breaking Changes

No breaking API changes. All v1.x code continues to work.

**Optional updates:**
```kotlin
// v1.x - still works
SmartReviewImplementation(context, config)

// v2.0 - new launcher option
SmartReviewImplementation(
    context,
    config = SmartReviewConfig(
        launcher = ReviewLauncher.PlayStore(packageName)  // New feature
    )
)
```

---

## 🐛 Troubleshooting

### Prompt Never Shows

**Check:**
1. Is `onAppLaunched()` called on every app start?
2. Have you met `minLaunchCount` and `minDaysSinceFirstLaunch`?
3. Is the user in cooldown period?
4. Did the user opt-out previously?

**Debug:**
```kotlin
lifecycleScope.launch {
    Log.d("Review", "Should prompt: ${reviewPrompter.shouldPrompt()}")
}
```

Enable detailed logs by filtering for `SmartReview` tag in Logcat.

---

### InApp Review Dialog Doesn't Appear

This is **normal behavior**. Google controls when the InApp Review dialog actually shows, even if you call the API.

**Google's limitations:**
- Quota limits per user
- Device/account restrictions
- Testing limitations (may not show in debug)

**Solutions:**
1. Use `ReviewLauncher.PlayStore` for guaranteed availability
2. Test on multiple devices/accounts
3. Use the [Testing Guide](https://developer.android.com/guide/playcore/in-app-review/test) from Google

---

### Play Store Link Opens Browser Instead of App

This happens when:
- Play Store app is not installed
- Play Store app is disabled
- Device doesn't have Google Play Services

The library automatically falls back to the browser - this is intentional and ensures the link always works.

---

## 📝 API Reference

### ReviewPrompter Interface
```kotlin
interface ReviewPrompter {
    suspend fun onAppLaunched()
    suspend fun shouldPrompt(): Boolean
    suspend fun markSentimentPositive(value: Boolean)
    suspend fun markOptOut()
    suspend fun markPromptShown()
    suspend fun markPassiveShown()
    suspend fun clearPassiveShows()
    suspend fun requestReview(activity: Activity): Boolean
    
    val willShowReview: StateFlow<Boolean>
    val isReviewActive: StateFlow<Boolean>
}
```

### ReviewLauncher Types
```kotlin
sealed interface ReviewLauncher {
    object InApp : ReviewLauncher
    data class PlayStore(val packageName: String) : ReviewLauncher
}
```

### ReviewPolicyConfig
```kotlin
data class ReviewPolicyConfig(
    val minLaunchCount: Int = 3,
    val minDaysSinceFirstLaunch: Duration = 2.days,
    val cooldown: Duration = 14.days,
    val maxPrompts: Int = 10,
    val maxPassiveShows: Int = 2
)
```

---

## 🧪 Testing

### Unit Testing
```kotlin
@Test
fun `review prompt respects cooldown`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val reviewPrompter = SmartReviewImplementation(
        context = context,
        config = SmartReviewConfig(
            policy = ReviewPolicyConfig(
                minLaunchCount = 1,
                cooldown = 14.days
            )
        )
    )
    
    reviewPrompter.onAppLaunched()
    assertTrue(reviewPrompter.shouldPrompt())
    
    reviewPrompter.markPromptShown()
    assertFalse(reviewPrompter.shouldPrompt())
}
```

### Manual Testing
```kotlin
// Quick testing configuration
SmartReviewImplementation(
    context = context,
    config = SmartReviewConfig(
        policy = ReviewPolicyConfig(
            minLaunchCount = 1,
            minDaysSinceFirstLaunch = 0.seconds
        ),
        launcher = ReviewLauncher.PlayStore(context.packageName)
    )
)
```

---

## 🧩 What the Library Controls

* When the prompt appears
* Cooldowns and frequency
* Passive impression limits
* Sentiment and opt-out logic
* Google Play review request
* Play Store link opening

---

## 🎯 What the App Controls

* Localization
* Typography
* Button styles
* Colors
* Layout containers
* Choice between InApp and PlayStore launcher

---

## ❌ What This Library Does NOT Do

* No modal dialogs
* No forced UI themes
* No implicit Material defaults
* No ViewModel coupling
* No intrusive prompts
* No background services
* No analytics tracking

---

## 👤 Author

**Anton Nikitin**

---

## 📄 License
```
Copyright 2024 Anton Nikitin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📞 Support

- 🐛 **Bug reports:** [GitHub Issues](https://github.com/kuznec2222/Smart-App-Review/issues)
- 💬 **Questions:** [GitHub Discussions](https://github.com/kuznec2222/Smart-App-Review/discussions)
- 📧 **Email:** [Contact via GitHub](https://github.com/kuznec2222)

---

## ⭐ Show Your Support

If this library helped you, please consider giving it a ⭐ on GitHub!