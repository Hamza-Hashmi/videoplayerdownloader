package com.example.videoplayer_and_downloader.UI.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData
import com.example.videoplayer_and_downloader.Utils.ad_periority
import com.example.videoplayer_and_downloader.Utils.shareVideoLink
import com.example.videoplayer_and_downloader.adapters.TrendingFavAdapter
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.getNativeAdObject
import com.example.videoplayer_and_downloader.databinding.ActivityTrendingFavouritesBinding
import com.example.videoplayer_and_downloader.databinding.FbExistScreenAdLayoutBinding
import com.example.videoplayer_and_downloader.databinding.NativeExitScreenAdsBinding
import com.example.videoplayer_and_downloader.listeners.onTrendingMediaClick
import com.example.videoplayer_and_downloader.viewModels.DatabaseViewModel
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception

class TrendingFavouritesActivity : AppCompatActivity(),onTrendingMediaClick {
    val dbViewModel:DatabaseViewModel by viewModel()
    lateinit var adapter:TrendingFavAdapter
    lateinit var binding:ActivityTrendingFavouritesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendingFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        CoroutineScope(Dispatchers.IO).launch {
            dbViewModel.getAllFav()
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                dbViewModel.allFavourites.observe(this@TrendingFavouritesActivity) {
                    Log.e("tag", "onCreate: trending fav ${it.size}")
                    if (it.size>0){
                        binding.noDataLayout.visibility = View.GONE
                        adapter = TrendingFavAdapter(it as ArrayList<TrendingVideoData>,this@TrendingFavouritesActivity)
                        binding.favrtRv.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                    else{
                        binding.noDataLayout.visibility = View.VISIBLE
                        loadBottomNativeAd()
                    }
                }
            }
        }


    }

    override fun onPlayClick(video: TrendingVideoData) {

    }

    override fun onRemoveFromFav(video: TrendingVideoData) {
        super.onRemoveFromFav(video)
        CoroutineScope(Dispatchers.IO).launch {
            dbViewModel.deleteFavourite(video.url)
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch{
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onShareVideo(video: TrendingVideoData) {
        super.onShareVideo(video)
        shareVideoLink(video.url)
    }

    private fun loadBottomNativeAd() {
        getNativeAdObject("VideoFolderTopNatvie", NativeAdOptions.ADCHOICES_TOP_LEFT, getString(
            R.string.native_id), getString(R.string.fb_native_ad),
            ad_periority, { nativeAdO->

                nativeAdO?.let {
                    when (it) {
                        is NativeAd -> {
                            val inflate = FbExistScreenAdLayoutBinding.inflate(LayoutInflater.from(this@TrendingFavouritesActivity), binding.layoutDetailAudioAdContainer, false)
                            populateFbNativeBanner(it, inflate, binding.layoutDetailAudioAdContainer)
                        }
                        is com.google.android.gms.ads.nativead.NativeAd -> {
                            val inflate = NativeExitScreenAdsBinding.inflate(LayoutInflater.from(this@TrendingFavouritesActivity), binding.layoutDetailAudioAdContainer, false)
                            populateNativeBanner(it, inflate, binding.layoutDetailAudioAdContainer)
                        }
                        else -> {

                        }
                    }
                }
            } , {
            })
    }
    private fun populateNativeBanner(nativeAd: com.google.android.gms.ads.nativead.NativeAd, adBinding: NativeExitScreenAdsBinding, nativeContainer: FrameLayout) {

        nativeContainer.removeAllViews()
        nativeContainer.addView(adBinding.root)
        val rootView = adBinding.root

        val mediaView: com.google.android.gms.ads.nativead.MediaView = adBinding.adMedia//findViewById(R.id.bannerMedia)

        mediaView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
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
        rootView.mediaView = mediaView

        // Set other ad assets.
        rootView.headlineView = adBinding.adHeadline//findViewById(R.id.tvAdmobTitle)
        rootView.bodyView = adBinding.adBody//findViewById(R.id.ad_body)
        rootView.callToActionView = adBinding.adCallToAction//findViewById(R.id.ad_call_to_action)
        rootView.iconView = adBinding.adAppIcon//findViewById(R.id.ad_app_icon)
        rootView.advertiserView = adBinding.adBody//findViewById(R.id.ad_body)

        // The headline is guaranteed to be in every UnifiedNativeAd.
        (rootView.headlineView as TextView).text = nativeAd.headline

        // check before trying to display them.
        if (nativeAd.body == null) {
            rootView.bodyView.visibility = View.INVISIBLE
        } else {
            rootView.bodyView.visibility = View.VISIBLE
            (rootView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            rootView.callToActionView.visibility = View.INVISIBLE
        } else {
            rootView.callToActionView.visibility = View.VISIBLE
            (rootView.callToActionView as TextView).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            rootView.iconView.visibility = View.INVISIBLE
        } else {
            (rootView.iconView as ImageView).setImageDrawable(nativeAd.icon.drawable)
            rootView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            rootView.advertiserView.visibility = View.INVISIBLE
        } else {
            (rootView.advertiserView as TextView).text = nativeAd.advertiser
            rootView.advertiserView.visibility = View.VISIBLE
        }
        rootView.setNativeAd(nativeAd)
    }
    private fun populateFbNativeBanner(nativeAd: NativeAd?, fbItem: FbExistScreenAdLayoutBinding, nativeContainer: FrameLayout) {

        nativeContainer.removeAllViews()
        nativeContainer.addView(fbItem.root)

        nativeAd?.let {
            val nativeAdLayout = fbItem.fbNativeAdView
            val llAdChoiceContainer = fbItem.adChoicesContainer
            val tvAdTitle = fbItem.fbAdHeadline
            val fbMediaView = fbItem.fbAdMedia
            val tvAdBody = fbItem.fbAdBody
            val btnAdCallToAction = fbItem.fbAdCallToAction
            val ivAdIcon = fbItem.fbAdAppIcon
            nativeAd.unregisterView()
            // Add the AdOptionsView
            val adOptionsView = AdOptionsView(fbItem.fbAdHeadline.context, nativeAd, nativeAdLayout)
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

}