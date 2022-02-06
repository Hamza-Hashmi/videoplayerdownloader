package com.example.videoplayer_and_downloader.UI.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.ad_periority
import com.example.videoplayer_and_downloader.Utils.isNetworkConnected
import com.example.videoplayer_and_downloader.adapters.MainVideoListAdapter
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.getNativeAdObject
import com.example.videoplayer_and_downloader.databinding.FragmentAllVideosBinding
import com.example.videoplayer_and_downloader.databinding.LayoutFbNativeBannerBinding
import com.example.videoplayer_and_downloader.databinding.LayoutNativeBannerBinding
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class AllVideosFragment :MyBaseFragment<FragmentAllVideosBinding>(FragmentAllVideosBinding::inflate) {

    private  val TAG = "NewTestDataFragment"
    private lateinit var mainVideoListAdapter: MainVideoListAdapter
    override fun initViews() {
        mainVideoListAdapter= MainVideoListAdapter(getVideoClickInterface(),getString(R.string.native_id), getString(R.string.fb_native_ad), keyPriority =  ad_periority)
        binding.folderRv.adapter=mainVideoListAdapter
        loadTopNative()
    }


    override fun setObservers() {
        super.setObservers()
        myVideosDataViewModel.allVideosSongs.observe(viewLifecycleOwner) { songlist ->
            if (songlist.size >0){
                Log.e(TAG, "onViewCreated:  allVideosSongs_size ${songlist.size}",)
                context?.let {
                    mainVideoListAdapter.setData(songlist, it.isNetworkConnected())
                    mainVideoListAdapter.notifyDataSetChanged()
                }
            }
            else{
                binding.noDataLayout.visibility = View.VISIBLE
                binding.folderRv.visibility = View.GONE
            }
        }
    }

    private fun loadTopNative() {
        context?.getNativeAdObject("VideoFolderTopNatvie", NativeAdOptions.ADCHOICES_TOP_LEFT, getString(
            R.string.native_id), getString(R.string.fb_native_ad),
            ad_periority, { nativeAdO->
                context?.let {cont->
                    nativeAdO?.let {
                        when (it) {
                            is NativeAd -> {
                                val inflate = LayoutFbNativeBannerBinding.inflate(LayoutInflater.from(cont), binding.videosTopAdContainter, false)
                                populateFbNativeBanner(it, inflate, binding.videosTopAdContainter)
                            }
                            is com.google.android.gms.ads.nativead.NativeAd -> {
                                val inflate = LayoutNativeBannerBinding.inflate(LayoutInflater.from(cont), binding.videosTopAdContainter, false)
                                populateNativeBanner(it, inflate, binding.videosTopAdContainter)
                            }
                            else -> {

                            }
                        }
                    }
                }
            } , {
            })
    }
    private fun populateNativeBanner(nativeAd: com.google.android.gms.ads.nativead.NativeAd, adBinding: LayoutNativeBannerBinding, nativeContainer: FrameLayout) {

        nativeContainer.removeAllViews()
        nativeContainer.addView(adBinding.root)
        val rootView = adBinding.root

        val mediaView: com.google.android.gms.ads.nativead.MediaView = adBinding.homeBannerMedia1//findViewById(R.id.bannerMedia)

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
        rootView.headlineView = adBinding.homeTvAdmobTitle//findViewById(R.id.tvAdmobTitle)
        rootView.bodyView = adBinding.homeAdBody//findViewById(R.id.ad_body)
        rootView.callToActionView = adBinding.homeAdCallToAction//findViewById(R.id.ad_call_to_action)
        rootView.iconView = adBinding.homeAdAppIcon//findViewById(R.id.ad_app_icon)
        rootView.advertiserView = adBinding.homeAdBody//findViewById(R.id.ad_body)

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
    private fun populateFbNativeBanner(nativeAd: NativeAd?, fbItem: LayoutFbNativeBannerBinding, nativeContainer: FrameLayout) {

        nativeContainer.removeAllViews()
        nativeContainer.addView(fbItem.root)

        nativeAd?.let {
            val nativeAdLayout = fbItem.homeBanNativeAdLayoutFb
            val llAdChoiceContainer = fbItem.homeBanLlAdChoiceContainer
            val tvAdTitle = fbItem.homeBanTvFbAdTitle
            val fbMediaView = fbItem.homeBanFbMediaView
            val tvAdBody = fbItem.homeBanTvFbAdBody
            val btnAdCallToAction = fbItem.homeBanBtnFbAdCallToAction
            val ivAdIcon = fbItem.homeBanIvFbAdIcon
            nativeAd.unregisterView()
            // Add the AdOptionsView
            val adOptionsView = AdOptionsView(fbItem.homeBanTvFbAdTitle.context, nativeAd, nativeAdLayout)
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