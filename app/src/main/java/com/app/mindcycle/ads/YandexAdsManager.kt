package com.app.mindcycle.ads

import android.app.Activity
import android.content.Context
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.banner.BannerAdSize

/**
 * Utility class for managing Yandex ads
 */
class YandexAdsManager {
    
    companion object {
        // Real ad unit IDs from Yandex Ads
        private const val BANNER_AD_UNIT_ID = "R-M-15975859-1"
        private const val INTERSTITIAL_AD_UNIT_ID = "R-M-15975859-3"
        private const val REWARDED_AD_UNIT_ID = "R-M-15975859-4"
    }
    
    /**
     * Load and display a banner ad
     */
    fun setupBanner(
        bannerAdView: BannerAdView,
        onLoaded: (() -> Unit)? = null,
        onFailed: ((AdRequestError) -> Unit)? = null
    ) {
        bannerAdView.setAdUnitId(BANNER_AD_UNIT_ID)
        bannerAdView.setAdSize(BannerAdSize.stickySize(bannerAdView.context, 320))
        bannerAdView.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() { onLoaded?.invoke() }
            override fun onAdFailedToLoad(error: AdRequestError) { onFailed?.invoke(error) }
            override fun onLeftApplication() {}
            override fun onReturnedToApplication() {}
            override fun onAdClicked() {}
            override fun onImpression(impressionData: ImpressionData?) {}
        })
        bannerAdView.loadAd(AdRequest.Builder().build())
    }
    
    /**
     * Load an interstitial ad
     */
    fun loadAndShowInterstitial(
        context: Context,
        activity: Activity,
        onLoaded: (() -> Unit)? = null,
        onFailed: ((AdRequestError) -> Unit)? = null
    ) {
        val adLoader = InterstitialAdLoader(context)
        adLoader.setAdLoadListener(object : InterstitialAdLoadListener {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                onLoaded?.invoke()
                interstitialAd.setAdEventListener(object : InterstitialAdEventListener {
                    override fun onAdShown() {}
                    override fun onAdFailedToShow(error: AdError) {}
                    override fun onAdDismissed() {}
                    override fun onAdClicked() {}
                    override fun onAdImpression(impressionData: ImpressionData?) {}
                })
                interstitialAd.show(activity)
            }
            override fun onAdFailedToLoad(error: AdRequestError) { onFailed?.invoke(error) }
        })
        val config = AdRequestConfiguration.Builder(INTERSTITIAL_AD_UNIT_ID).build()
        adLoader.loadAd(config)
    }
    
    /**
     * Load an App Open Ad (using Interstitial Ad unit)
     */
    fun loadAndShowAppOpenAd(
        context: Context,
        activity: Activity,
        onLoaded: (() -> Unit)? = null,
        onFailed: ((AdRequestError) -> Unit)? = null
    ) {
        val adLoader = InterstitialAdLoader(context)
        adLoader.setAdLoadListener(object : InterstitialAdLoadListener {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                onLoaded?.invoke()
                interstitialAd.setAdEventListener(object : InterstitialAdEventListener {
                    override fun onAdShown() {}
                    override fun onAdFailedToShow(error: AdError) {}
                    override fun onAdDismissed() {}
                    override fun onAdClicked() {}
                    override fun onAdImpression(impressionData: ImpressionData?) {}
                })
                interstitialAd.show(activity)
            }
            override fun onAdFailedToLoad(error: AdRequestError) { onFailed?.invoke(error) }
        })
        val config = AdRequestConfiguration.Builder(REWARDED_AD_UNIT_ID).build()
        adLoader.loadAd(config)
    }
} 