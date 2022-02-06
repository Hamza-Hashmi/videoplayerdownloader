package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.NativeAdListener
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.example.videoplayer_and_downloader.R
class NativeAdsHelper(private val activity: Activity) {
    private var nativeAd: NativeAd? = null
    var adLoader: AdLoader? = null

    fun loadNativeAds(
        onFail: ((String?) -> Unit)? = null,
        onLoad: ((NativeAd?) -> Unit)? = null,
    ) {
        val builder = AdLoader.Builder(activity, activity.resources.getString(R.string.native_id))
        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forNativeAd { unifiedNativeAd: NativeAd? ->
            if (nativeAd != null) {
                nativeAd!!.destroy()
            }
            nativeAd = unifiedNativeAd
            nativeAd?.let { onLoad?.invoke(it) }
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("Add Failed", loadAdError.message + "-ECode " + loadAdError.code)
                    super.onAdFailedToLoad(loadAdError)
                    //onNativeAdLoad.onFail(nativeAd);
                    onFail?.invoke(loadAdError.message)
                }
            })
            .withNativeAdOptions(adOptions)
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun loadNativeAds11(
        onFail: ((String?) -> Unit)? = null,
        onLoad: ((NativeAd?) -> Unit)? = null,
       ) {


            val builder =
                AdLoader.Builder(activity, activity.resources.getString(R.string.native_id))
            // OnUnifiedNativeAdLoadedListener implementation.
            builder.forNativeAd { unifiedNativeAd: NativeAd? ->
                if (nativeAd != null) {
                    nativeAd!!.destroy()
                }
                nativeAd = unifiedNativeAd
                nativeAd?.let { onLoad?.invoke(it) }
            }
            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
            val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
            builder.withNativeAdOptions(adOptions)
            val adLoader = builder
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e("Add Failed", loadAdError.message + "-ECode " + loadAdError.code)
                        super.onAdFailedToLoad(loadAdError)
                        //onNativeAdLoad.onFail(nativeAd);
                        onFail?.invoke(loadAdError.message)
                    }
                })
                .withNativeAdOptions(adOptions)
                .build()
            adLoader.loadAd(AdRequest.Builder().build())

    }

    fun loadFbNativeAd(
        onLoad: ((Ad) -> Unit)? = null,
        onFail: ((AdError?) -> Unit)? = null
    ) {
        val tag = "FbNativeAd"
        val TAG = "FbNativeAd"
        val nativeAdFb =
            com.facebook.ads.NativeAd(activity, activity.getString(R.string.fb_native_ad))
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {
                // Native ad finished downloading all assets

                Log.e(TAG, "Native ad finished downloading all assets.", )
            }

            @SuppressLint("LongLogTag")
            override fun onError(ad: Ad?, adError: AdError) {
                // Native ad failed to load
                Log.e("Native ad failed to load: %s", adError.errorMessage, )
                onFail?.invoke(adError)
            }

            override fun onAdLoaded(ad: Ad) {
                nativeAdFb.let { nativeAd ->
                    // Inflate Native Ad into Container
                    Log.e(tag,"Native ad is loaded and ready to be displayed!")

                    onLoad?.invoke(ad)
                }
            }

            override fun onAdClicked(ad: Ad) {
                // Native ad clicked
                Log.d(tag, "Native ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                // Native ad impression
                Log.d(tag, "Native ad impression logged!")
            }
        }
        // Request an ad
        nativeAdFb.apply {
            loadAd(buildLoadAdConfig().withAdListener(nativeAdListener).build())
        }
    }


    fun setNativeAd(
        frameLayout: FrameLayout, layoutId: Int,
        onFail: ((String?) -> Unit)? = null,
        onLoad: ((NativeAd?) -> Unit)? = null,
    ) {
        val builder = AdLoader.Builder(activity, activity.resources.getString(R.string.native_id))
        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forNativeAd { unifiedNativeAd: NativeAd ->
            if (nativeAd != null) {
                nativeAd!!.destroy()
            }
            nativeAd = unifiedNativeAd
            val adView =
                (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    layoutId,
                    null
                ) as NativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
            onLoad?.invoke(nativeAd!!)
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("Add Failed", loadAdError.message + "-ECode " + loadAdError.code)
                    super.onAdFailedToLoad(loadAdError)
                    nativeAd?.let { onFail?.invoke(loadAdError.message) }
                }
            })
            .withNativeAdOptions(adOptions)
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun setLoadedNativeAd(frameLayout: FrameLayout, layoutId: Int, nativeAd: NativeAd) {
        // OnUnifiedNativeAdLoadedListener implementation.
        val adView =
            (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                layoutId,
                null
            ) as NativeAdView
        populateUnifiedNativeAdView(nativeAd, adView)
        frameLayout.removeAllViews()
        frameLayout.addView(adView)
    }

    fun setNativeAd(
        frameLayout: FrameLayout,
        layoutId: Int,
        sdIs: String?,
        onFail: ((String?) -> Unit)? = null,
        onLoad: ((NativeAd?) -> Unit)? = null,
    ) {
        val builder = AdLoader.Builder(activity, sdIs)
        builder.forNativeAd { unifiedNativeAd: NativeAd ->
            if (nativeAd != null) {
                nativeAd!!.destroy()
            }
            nativeAd = unifiedNativeAd
            val adView =
                (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    layoutId,
                    null
                ) as NativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
            onLoad?.invoke(nativeAd!!)
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("Add Failed", loadAdError.message + "-ECode " + loadAdError.code)
                    super.onAdFailedToLoad(loadAdError)
                    nativeAd?.let { onFail?.invoke(loadAdError.message) }
                }
            })
            .withNativeAdOptions(adOptions)
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        val mediaView: MediaView = adView.findViewById(R.id.ad_media)
        adView.mediaView = mediaView
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.bodyView = adView.findViewById(R.id.ad_body)


        adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        if (adView.mediaView != null) {
            adView.mediaView.setVisibility(View.VISIBLE);
        } else {
            adView.mediaView?.visibility = View.GONE
        }

        if (nativeAd.headline != null) (adView.headlineView as TextView).text = nativeAd.headline
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon != null) {
            adView.iconView?.visibility = View.VISIBLE
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
        }
//        else adView.iconView?.visibility = View.GONE

        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.GONE
        } else {
            (adView.bodyView as TextView).text = nativeAd.body
            adView.bodyView?.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)
    }
}