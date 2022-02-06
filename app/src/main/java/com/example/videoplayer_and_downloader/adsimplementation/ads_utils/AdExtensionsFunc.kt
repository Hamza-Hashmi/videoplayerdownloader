package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.ad_periority
import com.example.videoplayer_and_downloader.Utils.isInternetConnected
import com.facebook.ads.InterstitialAd
import com.facebook.ads.NativeAdLayout
import com.facebook.shimmer.ShimmerFrameLayout


import com.google.android.gms.ads.nativead.NativeAd
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.NativeAdsHelper


var TIME_TO_WAIT: Long = 0
var isAdLoad = MutableLiveData<Boolean>()
public val preLoadedNativeAd: Any? = null

var bottomSheetNative_Ad: Any? = null



fun Activity.ExistloadPrenativeAd(isAutoAdsRemoved: Boolean,TAG:String,onLoadNativeAd: (Any?) -> Unit) {
    Log.e(TAG, "ExistloadPrenativeAd: " )
    var nativeAdsHelper = NativeAdsHelper(this)
    if (!isAutoAdsRemoved) {
        if (isInternetConnected() && !isAutoAdsRemoved) {
            val placement =
                com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
            getNativeAdObject(TAG + "_Native", placement,
                admobId = getString(R.string.native_id),
                facebookId = getString(R.string.fb_native_ad),
                periority = ad_periority,
                onResult = { nativeAdO ->
//                        dismissShimmer(shimmerViewContainer)
                    nativeAdO?.let {
                        if (it is NativeAd) {
                            nativeAdsHelper.loadNativeAds11(
                                onFail = {
                                }, onLoad = {

                                    bottomSheetNative_Ad = it
                                    onLoadNativeAd.invoke(it)
                                })
                        }else if (it is com.facebook.ads.NativeAd) {
                            nativeAdsHelper.loadFbNativeAd(
                                onFail = {
                                }, onLoad = {

                                    bottomSheetNative_Ad = it
                                    Log.e(TAG, "ExistloadPrenativeAd: $it" )
                                    onLoadNativeAd.invoke(it)
                                })

                        }
                    }
                },
                onAdClicked = {})
        }
    }

}

fun Activity.setNativeAd(
    shimmerViewContainer: ShimmerFrameLayout,
    adFrame: FrameLayout,
    adFrameFb: NativeAdLayout,
    adLayout: ConstraintLayout,
    nativeAdScreen: Int,
    fbNativeAdScreen: Int,
    isAutoAdsRemoved:Boolean=false,
    TAG: String,
    adPeriority:Int,
    preLoadedNativeAd: Any? = null,
    adMobNativeId: String? = null
) {
        if (isInternetConnected() ) {
                adLayout.visibility = View.VISIBLE
                showShimmer(shimmerViewContainer)
                val placement =
                    com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
                if (preLoadedNativeAd == null) {
                    getNativeAdObject(TAG + "_Native", placement,
                        admobId = adMobNativeId ?: getString(R.string.native_id),
                        facebookId = getString(R.string.fb_native_ad),
                        periority = adPeriority,
                        onResult = { nativeAdO ->
                            dismissShimmer(shimmerViewContainer)
                            nativeAdO?.let {
                                if (it is NativeAd && !isAutoAdsRemoved) {
                                    NativeAdsHelper(this).setLoadedNativeAd(
                                        adFrame,
                                        nativeAdScreen,
                                        it
                                    )

                                } else if (it is com.facebook.ads.NativeAd && !isAutoAdsRemoved) {
                                    FacebookNativeAd(this).setFBLoadedNativeAd(
                                        adFrameFb,
                                        fbNativeAdScreen,
                                        it
                                    )
                                }
                            }
                        },
                        onAdClicked = {})
                } else {
                    dismissShimmer(shimmerViewContainer)
                    preLoadedNativeAd.let {
                        if (it is NativeAd && !isAutoAdsRemoved) {
                            NativeAdsHelper(this).setLoadedNativeAd(
                                adFrame,
                                nativeAdScreen,
                                it
                            )
                        } else if (it is com.facebook.ads.NativeAd && !isAutoAdsRemoved) {
                            FacebookNativeAd(this).setFBLoadedNativeAd(
                                adFrameFb,
                                fbNativeAdScreen,
                                it
                            )
                        }
                    }
                }
        } else {
            Log.e(TAG, "setNativeAd: false internet", )
            Handler(Looper.getMainLooper()).postDelayed({
                dismissShimmer(shimmerViewContainer)
            }, 3000)
            adLayout.visibility = View.GONE
            TIME_TO_WAIT = 2000L
        }
}

fun showShimmer(shimmerViewContainer: ShimmerFrameLayout?) {
    try {
        shimmerViewContainer?.startShimmer()
        shimmerViewContainer?.visibility = View.VISIBLE
    } catch (e: Exception) {
    }
}

fun dismissShimmer(shimmerViewContainer: ShimmerFrameLayout?) {
    try {
        shimmerViewContainer?.visibility = View.GONE
        shimmerViewContainer?.stopShimmer()
    } catch (e: Exception) {
    }
}

fun loadInterstitial(interstitialAdUpdated: InterstitialAdUpdated, context:Context, it: Any?, onAdFail: ((Any?) -> Unit)? = null) {
    if (it is com.google.android.gms.ads.interstitial.InterstitialAd) {
        interstitialAdUpdated.loadInterstitialAd(context)
    } else if (it is InterstitialAd && !it.isAdLoaded) {
        it.loadAd()
    } else  {
        onAdFail?.invoke(it)
    }
}


fun Activity.loadFaceBookNative(adFrame: NativeAdLayout, fbNativeAdScreen: Int) {
    FacebookNativeAd(this).loadFbNativeAd(adFrame, fbNativeAdScreen)
}

fun Activity.setPreloadNativeAd(
    adFrame: FrameLayout,
    adFrameFb: NativeAdLayout,
    nativeAdScreen: Int,
    fbNativeAdScreen: Int,
    preLoadedNativeAd: Any? = null
) {
    preLoadedNativeAd.let {
        if (it is com.google.android.gms.ads.nativead.NativeAd) {
            Log.e("TAG", "setPreloadNativeAd: native" )
            NativeAdsHelper(this).setLoadedNativeAd(
                adFrame,
                nativeAdScreen,
                it
            )
        } else if (it is com.facebook.ads.NativeAd) {
            FacebookNativeAd(this).setFBLoadedNativeAd(
                adFrameFb,
                fbNativeAdScreen,
                it
            )
        }
    }
}