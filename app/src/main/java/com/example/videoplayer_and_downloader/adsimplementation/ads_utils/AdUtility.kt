package com.example.videoplayer_and_downloader.adsimplementation.ads_utils


import android.content.Context
import com.example.videoplayer_and_downloader.Utils.printLog

import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.NativeAdListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd

import com.google.android.gms.ads.nativead.NativeAd
import com.example.videoplayer_and_downloader.adsimplementation.enums.AdType

object AdUtility {

    fun spl(msg: String?) {
        println(msg)
    }

    fun lpl(tag: String, msg: String) {
        printLog(tag, "lpl: $msg")
    }

    const val UNKNOWN_ERROR_OCCURED = "UNKNOWN_ERROR_OCCURED"
    const val UNKNOWN_ERROR_CODE = -94000



    private var isAdLoaded: Boolean = false
    private var adFailedReloadCount = 0
    var mInterstitialAd: InterstitialAd? = null


    val videoOptions = VideoOptions.Builder()
        .setStartMuted(true)
        .build()

    fun loadNativeAd(
        tag: String,
        context: Context,
        adId: String,
        adChoicePlacement: Int,
        listner: AdListener2
    ) {
        val adLoader1 = AdLoader.Builder(context, adId)
            .forNativeAd { ad: NativeAd ->
                listner.onAdLoaded(ad, AdType.ADMOB_NATIVE)
                printLog(tag, "AdLoaded ${AdType.ADMOB_NATIVE}")
            }

            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError?) {
                    listner.apply {
                        val error = p0?.message ?: UNKNOWN_ERROR_OCCURED
                        val code = p0?.code ?: UNKNOWN_ERROR_CODE
                        this.onAdFailed(error, code, AdType.ADMOB_NATIVE)
                    }
                    printLog(tag, "AdFailedToLoad with code ${p0?.code} ${AdType.ADMOB_NATIVE}")
                }
            })
            .withNativeAdOptions(
                com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
                    .setMediaAspectRatio(com.google.android.gms.ads.nativead.NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT)
                    .setAdChoicesPlacement(adChoicePlacement)
                    .setVideoOptions(videoOptions)
                    .build()
            )
            .build()
        adLoader1.loadAd(AdRequest.Builder().build())


    }


    fun loadFbNative(tag: String, periority: Int, context: Context, facebookId: String, adListener: AdListener2) {
        val nativeAd = com.facebook.ads.NativeAd(context, facebookId)
        val nativeAdListener = object: NativeAdListener {
            override fun onAdLoaded(p0: Ad?) {
                printLog("$tag native_status", "Priority $periority FB Native Ad Loaded")
//                onResult(p0)
                adListener.onAdLoaded(nativeAd, AdType.FACEBOOK_NATIVE)
            }
            override fun onAdClicked(p0: Ad?) {
                adListener.onAdClicked(nativeAd, AdType.FACEBOOK_NATIVE)
            }
            override fun onError(p0: Ad?, p1: AdError?) {
                adListener.onAdFailed(p1?.errorMessage, p1?.errorCode, AdType.FACEBOOK_NATIVE)
                printLog("$tag native_status", "Priority $periority FB Native Ad Failed")

            }
            override fun onLoggingImpression(p0: Ad?) {

            }
            override fun onMediaDownloaded(p0: Ad?) {

            }
        }
        nativeAd.loadAd(
            nativeAd.buildLoadAdConfig()
                .withAdListener(nativeAdListener)
                .build())

    }

}