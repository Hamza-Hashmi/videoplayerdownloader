package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.isAlreadyPurchased
import com.example.videoplayer_and_downloader.Utils.isInternetConnected

import com.facebook.ads.AdOptionsView
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAdLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NativeAdViewHolderHistory(
    val context: Context,
    var view: View,
    var nativeAdId: Int,
    var callback: NativeAdsCallBack
) : RecyclerView.ViewHolder(view) {

    private var unifiedNativeAd = view.findViewById<NativeAdView>(R.id.uniform)
    var fbNativeAdView = view.findViewById<NativeAdLayout>(R.id.fbNativeAdView)
    // private var fbNativeAdView: RowCatNativeAdItemBinding = RowCatNativeAdItemBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
    var layoutShimmer = view.findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container)

//    private var progressBar = view.findViewById<ProgressBar>(R.id.progressBar2)

    var mNativeAd: NativeAd? = null

    fun setData(nativeAd: Any?, position: Int, priority: Int) {
        // layoutShimmer.startShimmerAnimation()
//        dashbordViewModel.dashboardListNativeItem().let { remoteAd ->
//            periority = if (priority == -1) {
//                remoteAd.priority
//            } else {
//                priority
//            }
//        }

        if (nativeAd == null) {
            showShimmer()
            fbNativeAdView.visibility = View.GONE
            unifiedNativeAd.visibility = View.GONE

            val nativeAdPlacement = com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
            GlobalScope.launch(Dispatchers.IO) {
                itemView.context?.getNativeAdObject(
                    "ListNativeItem $position",
                    nativeAdPlacement, itemView.context.getString(nativeAdId), itemView.context.getString(
                        R.string.fb_native_ad), priority,
                    onResult = { nativeAd ->
                        GlobalScope.launch(Dispatchers.Main) {
                            nativeAd.let {
                                if (nativeAd == null) {
//                                admobContainer?.visibility = View.GONE
                                } else {
                                    callback.onNewAdLoaded(it!!, position)
                                    if (it is NativeAd) {

                                        mNativeAd = it
                                        unifiedNativeAd.post {
                                            GlobalScope.launch(Dispatchers.IO) {
                                                GlobalScope.launch(Dispatchers.Main) {
                                                    dismissShimmer()
                                                    populateUnifiedNativeAdView(
                                                        mNativeAd!!,
                                                        unifiedNativeAd
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        if (it is com.facebook.ads.NativeAd) {
                                            dismissShimmer()
                                            populateFbNativeAdOnView(it)
                                        }
                                    }
                                }
                            }
                        }
                    },
                    onAdClicked = {})
            }
        } else {

            GlobalScope.launch(Dispatchers.IO) {
                nativeAd.let { it ->
                    when (it) {
                        is NativeAd -> {
                            callback.onNewAdLoaded(it, position)
                            GlobalScope.launch(Dispatchers.Main) {
                                mNativeAd?.let {
                                    dismissShimmer()
                                    populateUnifiedNativeAdView(it, unifiedNativeAd)
                                }
                            }
                        }
                        is com.facebook.ads.NativeAd -> {
                            GlobalScope.launch(Dispatchers.Main) {
                                dismissShimmer()
                                populateFbNativeAdOnView(it)
                            }
                        }
                        else -> {
                            dismissShimmer()
                        }
                    }
                }

            }
        }
    }

    private fun showShimmer(){
        if(context.isInternetConnected() && !context.isAlreadyPurchased()) {
            try {
                layoutShimmer.startShimmer()
                layoutShimmer.visibility = View.VISIBLE
            } catch (e: Exception) { }
        }else
            dismissShimmer()
    }
    private fun dismissShimmer(){
        try {
            layoutShimmer.stopShimmer()
            layoutShimmer.visibility = View.GONE
        } catch (e: Exception) {
        }
    }

    private fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        val mediaView = adView.findViewById<com.google.android.gms.ads.nativead.MediaView>(R.id.ad_media)
        adView.mediaView = mediaView
        mediaView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                try {
                    if (child is ImageView) {
                        child.adjustViewBounds = true
                        child.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                } catch (e: Exception) {}
            }
            override fun onChildViewRemoved(parent: View, child: View) {}
        })

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)

        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        (adView.headlineView as TextView).text = nativeAd.headline
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            if (adView.callToActionView is Button)
                (adView.callToActionView as Button).text = nativeAd.callToAction
            if (adView.callToActionView is TextView) {
                (adView.callToActionView  as TextView).text = nativeAd.callToAction
            }
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            Glide.with(adView.context.applicationContext)
                .load(nativeAd.icon.drawable)
//                    .apply(RequestOptions.circleCropTransform())
                .into((adView.iconView as ImageView))
            adView.iconView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)

        unifiedNativeAd.visibility = View.VISIBLE
//        progressBar.visibility = View.GONE

    }

    private fun populateFbNativeAdOnView(nativeAd: com.facebook.ads.NativeAd?) {
        nativeAd?.let {
            nativeAd.unregisterView()
            // Add the AdOptionsView
            val adOptionsView = AdOptionsView(context, nativeAd, fbNativeAdView)
            fbNativeAdView.findViewById<LinearLayout>(R.id.fb_ad_choices_container).removeAllViews()
            fbNativeAdView.findViewById<LinearLayout>(R.id.fb_ad_choices_container).addView(adOptionsView, 0)
            // Set the Text.
            val fb_ad_media: MediaView = fbNativeAdView.findViewById(R.id.fb_ad_media)

            fbNativeAdView.findViewById<TextView>(R.id.fb_ad_headline).text = nativeAd.advertiserName
            fbNativeAdView.findViewById<TextView>(R.id.fb_ad_body).text = nativeAd.adBodyText
            if (nativeAd.hasCallToAction()) {
                fbNativeAdView.findViewById<Button>(R.id.fb_ad_call_to_action).visibility = View.VISIBLE
            } else {
                fbNativeAdView.findViewById<Button>(R.id.fb_ad_call_to_action).visibility = View.GONE
            }

            fbNativeAdView.findViewById<Button>(R.id.fb_ad_call_to_action).text = nativeAd.adCallToAction
            fbNativeAdView.let {
                // Create a list of clickable views
                val clickableViews = ArrayList<View>()
                clickableViews.add(it.findViewById<TextView>(R.id.fb_ad_headline))
                clickableViews.add(it.findViewById<Button>(R.id.fb_ad_call_to_action))
                // Register the Title and CTA button to listen for clicks.
                nativeAd.registerViewForInteraction(
                    it.findViewById(R.id.fbNativeAdView),
                    fb_ad_media,
                    it.findViewById<ImageView>(R.id.fb_ad_app_icon),
                    clickableViews
                )
            }
            Handler(Looper.myLooper()!!).postDelayed({
                fbNativeAdView.visibility = View.VISIBLE
            }, 300)
        }
    }

}