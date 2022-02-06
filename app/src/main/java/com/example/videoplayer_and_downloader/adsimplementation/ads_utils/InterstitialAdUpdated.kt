package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.isAlreadyPurchased
import com.example.videoplayer_and_downloader.Utils.isInternetConnected

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


open class InterstitialAdUpdated {
    private var TAG="InterstitialAd"

    var mInterstitialAd: InterstitialAd? = null
     var counter = 0
         var adShown = false
        private set

    companion object {
        var mediaPlayCounter = 0


        @Volatile
        private var instance: InterstitialAdUpdated? = null
        var onCloseCallback: (() -> Unit)? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: InterstitialAdUpdated().also {
                    instance = it
                }
            }
    }

    fun loadInterstitialAd(context: Context,isCallFromSplash: Boolean =false) {
        context.let {
            // isCallFromSplash  ${isCallFromSplash} ", )

        /*    val intersitial_id= if (isCallFromSplash)
            {
                Log.e(TAG, "loadInterstitialAd:  true splash ", )
                it.getString(R.string.splash_intersitial_id)
            }else{
                Log.e(TAG, "loadInterstitialAd:  else ", )
*/
            val intersitial_id =  it.getString(R.string.inter_id)
  //          }

            InterstitialAd.load(
                it, intersitial_id,

                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {

                    override fun onAdFailedToLoad(ad: LoadAdError) {
                        OpenApp.isInterstitialShown = false
                        if (counter == 2) {
                            onCloseCallback?.invoke()
                        } else {
                            counter++
                            onCloseCallback?.invoke()
                            loadInterstitialAd(context)
                        }
                        adShown = false
//                            onCloseCallback?.invoke()
//                            loadInterstitialAd(context)
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.e(TAG, "onAdLoaded:  ", )
                        mInterstitialAd = ad
                        mInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    OpenApp.isInterstitialShown = false
                                    adShown=false
                                    mInterstitialAd = null
                                    loadInterstitialAd(context)
                                    Log.e("InterstiatialReload", "Reloaded___")
                                    onCloseCallback?.invoke()
                                }

                                override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                                    Log.e(TAG, "onAdFailedToShowFullScreenContent: ", )
                                    OpenApp.isInterstitialShown = false
                                    adShown=false
                                    super.onAdFailedToShowFullScreenContent(p0)
                                    onCloseCallback?.invoke()
                                }

                                override fun onAdShowedFullScreenContent() {
                                    Log.e(TAG, "onAdShowedFullScreenContent: ", )
                                    OpenApp.isInterstitialShown = true
                                    adShown=true
                                    super.onAdShowedFullScreenContent()
                                }
                            }
                        Log.e("Interstitial____", "AdLoaded____")
                    }
                })
        }
    }


    // to show Interstitial Ad Activity reference must be given
    fun showInterstitialAdNew(activity: Activity, onAction: (() -> Unit)? = null) {
        if(!activity.isAlreadyPurchased() && activity.isInternetConnected()) {
            Log.e("TAG", "showInterstitialAdNew:  calll", )
            if (mInterstitialAd != null) {
                if (!adShown) {
                    activity.let { mInterstitialAd?.show(it) }
                }
                onCloseCallback = {
                    onAction?.invoke()
                    onCloseCallback = null
                }
            } else {
                loadInterstitialAd(activity)
                onAction?.invoke()
            }
        }else{
            Log.e("TAG", "showInterstitialAdNew:  else call", )

            onAction?.invoke()
        }
    }
}