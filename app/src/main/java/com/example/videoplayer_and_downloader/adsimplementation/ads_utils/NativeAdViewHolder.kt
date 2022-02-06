package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.isAlreadyPurchased
import com.example.videoplayer_and_downloader.Utils.isInternetConnected
import com.example.videoplayer_and_downloader.databinding.LayoutAdContainerListBinding
import com.example.videoplayer_and_downloader.databinding.LayoutAdFbListNativeBinding
import com.example.videoplayer_and_downloader.databinding.LayoutAdListNativeBinding

import com.facebook.ads.AdOptionsView
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAdLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NativeAdViewHolder(
    var binding: LayoutAdContainerListBinding,
    var nativeAdId: String,
    var fbNative: String,
    var callback: NativeAdsCallBack,
    var periority: Int
)
    : RecyclerView.ViewHolder(binding.root) {

//    var mNativeAd: Any? = null

    fun setData(nativeAd: Any?, position: Int) {

        binding.mprogressBar.visibility = View.VISIBLE

        if (nativeAd == null) {
            Log.e("TAG", "setData: native ad not null " )
            GlobalScope.launch(Dispatchers.IO){
                itemView.context.getNativeAdObject("ListNativeAD", NativeAdOptions.ADCHOICES_TOP_LEFT, nativeAdId, fbNative,
                    periority, { nativeAdO->
                        GlobalScope.launch(Dispatchers.Main){
                            binding.mprogressBar.visibility = View.GONE

                            nativeAdO?.let {
                                Log.e("TAG", "setData: it $it " )
                                when (it) {
                                    is com.facebook.ads.NativeAd -> {
//                                        mNativeAd = it
                                        it?.let {
                                            Log.e("TAG", "setData: facbook native ad" )
                                            binding.mprogressBar.visibility =View.GONE
                                            callback.onNewAdLoaded(it, position)
                                            val inflate = LayoutAdFbListNativeBinding.inflate(LayoutInflater.from(binding.listAdContainer.context), binding.listAdContainer, false)
                                            populateFBDrawerNative(it as com.facebook.ads.NativeAd, inflate, binding.listAdContainer)
                                            binding.listAdContainer.visibility = View.VISIBLE
                                        }
                                    }
                                    is com.google.android.gms.ads.nativead.NativeAd -> {
                                        binding.mprogressBar.visibility = View.GONE
                                        callback.onNewAdLoaded(it, position)
                                        val inflate = LayoutAdListNativeBinding.inflate(LayoutInflater.from(binding.listAdContainer.context), binding.listAdContainer, false)
                                        populateDrawerNative(it, inflate, binding.listAdContainer)
                                        binding.listAdContainer.visibility = View.VISIBLE
                                    }
                                    else -> {

                                    }
                                }
                            }
                        }
                    } , {
                        callback.onAdClicked(position)
                    })
            }
        } else {
            Log.e("TAG", "setData:  else" )
            binding.mprogressBar.visibility = View.GONE
            GlobalScope.launch(Dispatchers.Main){
                nativeAd?.let { it ->
                    when (it) {
                        is com.facebook.ads.NativeAd -> {
//                            mNativeAd = it
                            it?.let {
                                binding.mprogressBar.visibility = View.GONE
                                val itemView = LayoutAdFbListNativeBinding.inflate(LayoutInflater.from(binding.listAdContainer.context), binding.listAdContainer, false)
                                populateFBDrawerNative(it as com.facebook.ads.NativeAd, itemView, binding.listAdContainer)
                            }
                        }
                        is com.google.android.gms.ads.nativead.NativeAd -> {
                            binding.mprogressBar.visibility = View.GONE
                            val adView = LayoutAdListNativeBinding.inflate(LayoutInflater.from(binding.listAdContainer.context), binding.listAdContainer, false)
                            populateDrawerNative(it, adView, binding.listAdContainer)
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun populateFBDrawerNative(nativeAd: com.facebook.ads.NativeAd?, fbItem: LayoutAdFbListNativeBinding, nativeContainer: FrameLayout) {
        binding.mprogressBar.visibility = View.GONE

        nativeContainer.removeAllViews()
        nativeContainer.addView(fbItem.root)

        nativeAd?.let {
            binding.mprogressBar.visibility = View.GONE
            val nativeAdLayout = fbItem.nativeAdLayoutFb
            val llAdChoiceContainer = fbItem.llAdChoiceContainer
            val tvAdTitle = fbItem.tvFbAdTitle
            val fbMediaView = fbItem.fbMediaView//adView?.findViewById<com.facebook.ads.MediaView>(R.id.fbMediaView)
            val tvAdBody = fbItem.tvFbAdBody//adView?.findViewById<TextView>(R.id.tvFbAdBody)
            val btnAdCallToAction = fbItem.btnFbAdCallToAction//adView?.findViewById<Button>(R.id.btnFbAdCallToAction)
            val ivAdIcon = fbItem.ivFbAdIcon//adView?.findViewById<ImageView>(R.id.ivFbAdIcon)
            nativeAd.unregisterView()
            // Add the AdOptionsView
            val adOptionsView = AdOptionsView(fbItem.tvFbAdTitle.context, nativeAd, nativeAdLayout)
            llAdChoiceContainer?.removeAllViews()
            llAdChoiceContainer?.addView(adOptionsView, 0)
            // Set the Text.
            tvAdTitle?.text = nativeAd.advertiserName
            tvAdBody?.text = nativeAd.adBodyText
            if (nativeAd.hasCallToAction()) {
                btnAdCallToAction?.visibility = View.VISIBLE
            } else btnAdCallToAction?.visibility = View.GONE

            btnAdCallToAction?.text = nativeAd.adCallToAction

            // Create a list of clickable views
            val clickableViews = ArrayList<View>()
            clickableViews.add(fbMediaView)
            clickableViews.add(ivAdIcon)
            clickableViews.add(tvAdTitle)
            clickableViews.add(btnAdCallToAction)
            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(
                nativeAdLayout,
                fbMediaView,
                ivAdIcon,
                clickableViews)
        }
    }

    private fun populateDrawerNative(nativeAd: com.google.android.gms.ads.nativead.NativeAd, adView: LayoutAdListNativeBinding, nativeContainer: FrameLayout) {
        binding.mprogressBar.visibility = View.GONE

        nativeContainer.removeAllViews()
        nativeContainer.addView(adView.root)
        var rootView = adView.root

        rootView.mediaView = adView.admobMediaView
        adView.admobMediaView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                try {
                    if (child is ImageView) {
                        child.adjustViewBounds = true
                        child.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                } catch (e: Exception) {

                }

            }

            override fun onChildViewRemoved(parent: View, child: View) {}
        })


        val tvAdTitle = adView.tvAdTitle//findViewById<TextView>(R.id.tvAdTitle)
        val tvAdBody = adView.tvAdBody//findViewById<TextView>(R.id.tvAdBody)
        val btnAdCallToAction = adView.btnAdCallToAction//findViewById<Button>(R.id.btnAdCallToAction)
        val ivAdIcon = adView.ivAdIcon//findViewById<ImageView>(R.id.ivAdIcon)

        rootView.headlineView = tvAdTitle
        rootView.callToActionView = btnAdCallToAction
        rootView.iconView = ivAdIcon
        (rootView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.callToAction == null) {
            rootView.callToActionView.visibility = View.INVISIBLE
        } else {
            rootView.callToActionView.visibility = View.VISIBLE
            (rootView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            rootView.iconView.visibility = View.GONE
        } else {
            (rootView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            rootView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.body != null) {
            tvAdBody.visibility = View.VISIBLE
            tvAdBody.text = nativeAd.body
        } else {
            tvAdBody.visibility = View.GONE
        }

        rootView.setNativeAd(nativeAd)
        rootView.visibility = View.VISIBLE

    }

}
