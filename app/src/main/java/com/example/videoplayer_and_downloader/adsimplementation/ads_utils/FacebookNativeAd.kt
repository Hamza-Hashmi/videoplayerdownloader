package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.LayoutRes
import com.facebook.ads.*
import java.util.ArrayList
import com.example.videoplayer_and_downloader.R
class FacebookNativeAd(private val activity: Activity) {
    fun loadFbNativeAd(
        nativeAdLayout: NativeAdLayout,
        @LayoutRes layoutRes: Int,
        onLoad: ((Ad) -> Unit)? = null,
        onFail: ((AdError?) -> Unit)? = null
    ) {
        val tag = "FbNativeAd"
        val TAG = "FbNativeAd"
        val nativeAdFb = NativeAd(activity, activity.getString(R.string.fb_native_ad))
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
                    inflateFbAd(nativeAd, nativeAdLayout, layoutRes)
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

    private fun inflateFbAd(
        nativeAdFb: NativeAd,
        nativeAdLayout: NativeAdLayout, @LayoutRes layoutRes: Int,
    ) {
        nativeAdFb.unregisterView()
        // Add the Ad view into the ad container.
        val inflater = LayoutInflater.from(activity)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        val adView: View = inflater.inflate(layoutRes, nativeAdLayout, false)
        nativeAdLayout.addView(adView)
        // Add the AdOptionsView
        val adChoicesContainer = activity.findViewById<LinearLayout>(R.id.fb_ad_choices_container)
        val adOptionsView = AdOptionsView(activity, nativeAdFb, nativeAdLayout)

        try {
            adChoicesContainer.let {
                it.removeAllViews()
                it.addView(adOptionsView, 0)
            }
        } catch (e: Exception) {
        }
        // Create native UI using the ad metadata.

        val nativeAdIcon: ImageView = adView.findViewById(R.id.fb_ad_app_icon)
        val nativeAdTitle: TextView = adView.findViewById(R.id.fb_ad_headline)
        val nativeAdMedia: MediaView = adView.findViewById(R.id.fb_ad_media)
        val nativeAdBody: TextView = adView.findViewById(R.id.fb_ad_body)
        val sponsoredLabel: TextView = adView.findViewById(R.id.fb_adLabel)
        val nativeAdCallToAction: Button = adView.findViewById(R.id.fb_ad_call_to_action)

        // Set the Text.
        nativeAdTitle.text = nativeAdFb.advertiserName
        nativeAdBody.text = nativeAdFb.adBodyText
        //nativeAdSocialContext.text = nativeAdFb.adSocialContext
        if (nativeAdFb.hasCallToAction())
            nativeAdCallToAction.visibility = View.VISIBLE
        else
            nativeAdCallToAction.visibility = View.INVISIBLE

        nativeAdCallToAction.text = nativeAdFb.adCallToAction
        sponsoredLabel.text = activity.getString(R.string.ad)
        // Create a list of clickable views
        val clickableViews: ArrayList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)
        // Register the Title and CTA button to listen for clicks.
        nativeAdFb.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews)

    }

    fun setFBLoadedNativeAd(nativeAdLayout: NativeAdLayout, layoutId: Int, nativeAd: NativeAd) {
        inflateFbAd(nativeAd, nativeAdLayout, layoutId)
    }
}