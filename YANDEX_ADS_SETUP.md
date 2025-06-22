# Yandex Ads Integration

This project has been configured with Yandex Mobile Ads SDK for displaying advertisements.

## Setup Completed

✅ Added Yandex repository to project-level `build.gradle`  
✅ Added Yandex Mobile Ads SDK dependency (version 7.14.0)  
✅ Initialized SDK in `MindCycleApplication`  
✅ Created utility classes for managing ads  
✅ Created Compose components for displaying ads  
✅ Configured with real ad unit IDs  

## Ad Unit IDs

- **Баннер**: `R-M-15975859-1`
- **Межстраничная реклама**: `R-M-15975859-3` 
- **Реклама с вознаграждением**: `R-M-15975859-4`

## Usage

### 1. Banner Ads (Баннер)

To display a banner ad in your Compose screen:

```kotlin
import com.app.mindcycle.ui.components.AdBanner

@Composable
fun MyScreen() {
    Column {
        // Your content here
        
        AdBanner(
            onAdLoaded = {
                // Ad loaded successfully
            },
            onAdFailed = { error ->
                // Handle ad loading failure
            }
        )
    }
}
```

### 2. Interstitial Ads (Межстраничная реклама)

To show interstitial ads:

```kotlin
import com.app.mindcycle.ads.YandexAdsManager

class MyActivity : ComponentActivity() {
    private var interstitialAd: InterstitialAd? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load interstitial ad
        interstitialAd = YandexAdsManager().loadInterstitialAd(
            context = this,
            onAdLoaded = {
                // Ad loaded successfully
            },
            onAdFailed = { error ->
                // Handle ad loading failure
            }
        )
    }
    
    private fun showInterstitialAd() {
        interstitialAd?.show()
    }
}
```

### 3. Rewarded Ads (Реклама с вознаграждением)

To show rewarded ads (using interstitial ad unit):

```kotlin
import com.app.mindcycle.ads.YandexAdsManager

class MyActivity : ComponentActivity() {
    private var rewardedAd: InterstitialAd? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load rewarded ad
        rewardedAd = YandexAdsManager().loadRewardedAd(
            context = this,
            onAdLoaded = {
                // Ad loaded successfully
            },
            onAdFailed = { error ->
                // Handle ad loading failure
            },
            onAdDismissed = {
                // Ad was dismissed, give reward
                giveReward()
            }
        )
    }
    
    private fun showRewardedAd() {
        rewardedAd?.show()
    }
    
    private fun giveReward() {
        // Implement your reward logic here
    }
}
```

## Ad Types

### Banner Ads (`R-M-15975859-1`)
- Small rectangular ads displayed at the bottom or top of screens
- Best for continuous monetization
- Non-intrusive user experience

### Interstitial Ads (`R-M-15975859-3`)
- Full-screen ads that appear between app screens
- Best for natural break points (level completion, app restart)
- Higher revenue potential

### Rewarded Ads (`R-M-15975859-4`)
- Full-screen ads that users choose to watch for rewards
- Implemented using interstitial ad unit
- Best for unlocking premium features, extra lives, etc.

## Implementation Tips

1. **Banner Ads**: Place at the bottom of screens for best performance
2. **Interstitial Ads**: Show at natural break points (every 2-3 minutes of usage)
3. **Rewarded Ads**: Offer valuable rewards to encourage viewing
4. **Ad Loading**: Pre-load ads when possible for better user experience
5. **Error Handling**: Always handle ad loading failures gracefully

## Important Notes

- The SDK is initialized in `MindCycleApplication.onCreate()`
- Real ad unit IDs are configured and ready for production
- Test ads thoroughly before release
- Follow Yandex Ads policies and guidelines
- Monitor ad performance in Yandex Ads dashboard
- Note: Yandex Mobile Ads SDK doesn't have dedicated AppOpenAd support

## Dependencies Added

- `com.yandex.android:mobileads:7.14.0` - Yandex Mobile Ads SDK

## Files Created/Modified

- `build.gradle` - Added Yandex repository and other ad network repositories
- `app/build.gradle` - Added Yandex Mobile Ads SDK dependency
- `MindCycleApplication.kt` - Added SDK initialization
- `YandexAdsManager.kt` - Utility class for managing ads with real IDs
- `AdBanner.kt` - Compose component for banner ads

## Testing

Your app is now configured with real ad unit IDs. Ads should start appearing once your app is published and approved by Yandex Ads. 