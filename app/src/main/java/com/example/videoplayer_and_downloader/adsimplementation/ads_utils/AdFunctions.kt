
package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.example.videoplayer_and_downloader.Utils.isInternetConnected
import com.example.videoplayer_and_downloader.Utils.printLog

import com.facebook.ads.*
import com.facebook.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.example.videoplayer_and_downloader.adsimplementation.enums.AdType

fun loadFbInterstitial(
    context: Context,
    facebookId: String,
    tag: String,
    onAdClosed: (interstitial: Any?) -> Unit,
    onResult: (Any?) -> Unit
) {
    val fbInterstitialAd = InterstitialAd(context, facebookId)
    val interstitialAdListener = object : InterstitialAdListener {

        override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
            p1?.errorMessage?.let { Log.e("error", it) }
        }

        override fun onAdLoaded(p0: Ad?) {
            onResult(fbInterstitialAd)
        }

        override fun onInterstitialDismissed(p0: Ad?) {
            onAdClosed(fbInterstitialAd)
        }

        override fun onInterstitialDisplayed(p0: Ad?) {

        }

        override fun onAdClicked(p0: Ad?) {
        }

        override fun onLoggingImpression(p0: Ad?) {

        }
    }
    fbInterstitialAd.loadAd(
        fbInterstitialAd.buildLoadAdConfig()
            .withAdListener(interstitialAdListener)
            .build()
    )
}

    fun Context.getInterstitialAdObject(
        tag: String,
        admobId: String,
        facebookId: String,
        periority: Int,
        onResult: (Any?) -> Unit,
        onAdClosed: (interstitial: Any?) ->
        Unit,
        onAdClicked: () -> Unit
    ) {
        val context = this
        when (periority) {
            0 -> {
                loadAdmobInterstitial(context, admobId, tag, onAdClosed, onResult)
            }
            1 -> {
                val interstitialAd = com.facebook.ads.InterstitialAd(this, facebookId)
                val interstitialAdListener = object : InterstitialAdListener {

                    override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                        p1?.errorMessage?.let { Log.e("error", it) }
                    }

                    override fun onAdLoaded(p0: Ad?) {
                        onResult(interstitialAd)
                    }

                    override fun onInterstitialDisplayed(p0: Ad?) {
                    }

                    override fun onAdClicked(p0: Ad?) {
                    }

                    override fun onInterstitialDismissed(p0: Ad?) {
                        onAdClosed(interstitialAd)
                    }

                    override fun onLoggingImpression(p0: Ad?) {

                    }
                }
                interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build()
                )
            }
            2 -> {

                val adRequest: AdRequest = AdRequest.Builder().build()

                com.google.android.gms.ads.interstitial.InterstitialAd.load(context, admobId, adRequest,
                    object : InterstitialAdLoadCallback() {

                        override fun onAdLoaded(@NonNull interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                            // The mInterstitialAd reference will be null until
                            interstitialAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        onAdClosed(interstitialAd)
                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError?) {
                                        loadFbInterstitial(
                                            context,
                                            facebookId,
                                            tag,
                                            onAdClosed,
                                            onResult
                                        )
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        Log.d(tag, "Ad showed fullscreen content.")
                                    }
                                }
                            onResult(interstitialAd)
                            Log.i(tag, "onAdLoaded")
                        }
                        override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                            Log.i(tag, loadAdError.message)
                            loadFbInterstitial(context, facebookId, tag, onAdClosed, onResult)
                        }
                    })

            }
            3 -> {
                val interstitialAd = com.facebook.ads.InterstitialAd(this, facebookId)
                val interstitialAdListener = object : InterstitialAdListener {

                    override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                        p1?.errorMessage?.let { Log.e("error", it) }
                        loadAdmobInterstitial(context, admobId, tag, onAdClosed, onResult)
                    }

                    override fun onAdLoaded(p0: Ad?) {
                        onResult(interstitialAd)
                    }

                    override fun onInterstitialDismissed(p0: Ad?) {
                        onAdClosed(interstitialAd)
                    }

                    override fun onInterstitialDisplayed(p0: Ad?) {

                    }

                    override fun onAdClicked(p0: Ad?) {
                    }

                    override fun onLoggingImpression(p0: Ad?) {

                    }
                }
                interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build()
                )
            }
        }
    }

private fun loadAdmobInterstitial(
    context: Context,
    admobId: String,
    tag: String,
    onAdClosed: (interstitial: Any?) -> Unit,
    onResult: (Any?) -> Unit
) {
    val adRequest: AdRequest = AdRequest.Builder().build()

    com.google.android.gms.ads.interstitial.InterstitialAd.load(context, admobId, adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(@NonNull interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                // an ad is loaded.
                var fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(tag, "Ad was dismissed.")
                        onAdClosed(interstitialAd)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError?) {
                        Log.d(tag, "Ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(tag, "Ad showed fullscreen content.")
                    }
                }
                interstitialAd?.fullScreenContentCallback = fullScreenContentCallback
                onResult(interstitialAd)
                Log.i(tag, "onAdLoaded")

            }

            override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                Log.i(tag, loadAdError.message)
                onResult(null)
            }
        })
}

fun Context.getNativeAdObject(
    tag: String, placement: Int, admobId: String, facebookId: String,
    periority: Int, onResult: (Any?) -> Unit, onAdClicked: () -> Unit) {
    val context = this
    if (!isInternetConnected()) {
        onResult(null)
        return
    }

    when (periority) {
        0 -> {
            AdUtility.loadNativeAd(tag, context, admobId, placement, object : AdListener2 {
                override fun onAdLoaded(ad: Any?, type: AdType) {
                    onResult(ad)
                    printLog("$tag native_status", "Priority $periority Native Ad Loaded")
                }

                override fun onAdClosed(ad: Any?, type: AdType) {
                    printLog(
                        "$tag native_status",
                        "Priority $periority Native Ad Closed"
                    )
                    onResult(ad)
                }

                override fun onAdFailed(error: String?, extraCode: Int?, type: AdType) {

                    printLog(
                        "$tag native_status",
                        "Priority $periority Native Ad Failed  ${error}, Message =${extraCode}"
                    )
                    onResult(null)
                }
            })
        }
        1 -> {
            AdUtility.loadFbNative(tag, periority, context, facebookId, object : AdListener2 {
                override fun onAdLoaded(ad: Any?, type: AdType) {
                    onResult(ad)
                    printLog("$tag native_status", "FB Native Loaded Successfuly")
                }

                override fun onAdClosed(ad: Any?, type: AdType) {
                }

                override fun onAdFailed(error: String?, extraCode: Int?, type: AdType) {
                    onResult(null)
                    printLog("$tag native_status", "FB Native Loading Failed")
                }
            })

        }
        2 -> {
            AdUtility.loadNativeAd(
                tag,
                context,
                admobId,
                NativeAdOptions.ADCHOICES_TOP_RIGHT,
                object : AdListener2 {
                    override fun onAdLoaded(ad: Any?, type: AdType) {
                        printLog(
                            "$tag native_status",
                            "Priority $periority Admob Native Ad Loaded Successfully"
                        )
                        onResult(ad)
                    }

                    override fun onAdClosed(ad: Any?, type: AdType) {
                        onResult(ad)
                    }

                    override fun onAdFailed(error: String?, extraCode: Int?, type: AdType) {
                        printLog(
                            "$tag native_status",
                            "Priority $periority Native Ad Failed and FB Native Request Sent"
                        )
                        val nativeAd = NativeAd(context, facebookId)
                        val nativeAdListener = object : NativeAdListener {
                            override fun onAdLoaded(p0: Ad?) {
                                onResult(nativeAd)
                            }

                            override fun onAdClicked(p0: Ad?) {
                            }

                            override fun onError(p0: Ad?, p1: AdError?) {
                                printLog(
                                    "$tag native_status",
                                    "Priority $periority FB Native Ad Failed, Native Ad Request sent to load"
                                )
                            }

                            override fun onLoggingImpression(p0: Ad?) {

                            }

                            override fun onMediaDownloaded(p0: Ad?) {

                            }
                        }
                        nativeAd.loadAd(
                            nativeAd.buildLoadAdConfig()
                                .withAdListener(nativeAdListener)
                                .build()
                        )
                    }
                })
        }
        3 -> {
            val nativeAd = NativeAd(context, facebookId)
            val nativeAdListener = object : NativeAdListener {
                override fun onAdLoaded(p0: Ad?) {
                    printLog("$tag native_status", "FB Native Ad Loaded successfully")
                    onResult(nativeAd)
                }

                override fun onAdClicked(p0: Ad?) {}

                override fun onError(p0: Ad?, p1: AdError?) {
                    printLog(
                        "$tag native_status",
                        "Priority $periority FB Native Ad Failed, Native Ad Request sent to load"
                    )
                    AdUtility.loadNativeAd(
                        tag,
                        context,
                        admobId,
                        NativeAdOptions.ADCHOICES_TOP_RIGHT,
                        object : AdListener2 {
                            override fun onAdLoaded(ad: Any?, type: AdType) {
                                printLog(
                                    "$tag native_status",
                                    "Priority $periority Native Ad loaded after FB Native Failed"
                                )
                                onResult(ad)
                            }

                            override fun onAdClosed(ad: Any?, type: AdType) {
                                onResult(ad)
                            }

                            override fun onAdFailed(error: String?, extraCode: Int?, type: AdType) {
                                printLog(
                                    "$tag native_status",
                                    "Priority $periority Native Ad also Failed after FB Native Failed to load"
                                )
                                onResult(null)
                            }
                        })
                }

                override fun onLoggingImpression(p0: Ad?) {

                }

                override fun onMediaDownloaded(p0: Ad?) {

                }
            }
            nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                    .withAdListener(nativeAdListener)
                    .build()
            )
        }
    }
}
