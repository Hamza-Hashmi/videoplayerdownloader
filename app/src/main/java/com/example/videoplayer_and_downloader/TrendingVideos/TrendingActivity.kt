package com.example.videoplayer_and_downloader.TrendingVideos

import android.content.Intent
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
import com.example.videoplayer_and_downloader.UI.activites.TrendingFavouritesActivity
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adapters.TrendingsAdapter
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.InterstitialAdUpdated
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.getNativeAdObject
import com.example.videoplayer_and_downloader.databinding.ActivityTrendingBinding
import com.example.videoplayer_and_downloader.databinding.LayoutFbNativeBannerBinding
import com.example.videoplayer_and_downloader.databinding.LayoutNativeBannerBinding
import com.example.videoplayer_and_downloader.listeners.onTrendingMediaClick
import com.example.videoplayer_and_downloader.viewModels.DatabaseViewModel
import com.example.videoplayer_and_downloader.viewModels.TrendingsViewModel
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrendingActivity : AppCompatActivity(),onTrendingMediaClick {
    lateinit var binding : ActivityTrendingBinding
    val mainViewModel: TrendingsViewModel by viewModel()
    val dbViewModel:DatabaseViewModel by viewModel()
    lateinit var adapter: TrendingsAdapter
    var itemCountClick = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }



            if (isNetworkConnected()){
                loadBottomNative()
                binding.noDataIv.visibility  = View.GONE
                CoroutineScope(Dispatchers.IO).launch {

                    mainViewModel.getTrendingVideos()
                }.invokeOnCompletion {

                }



                mainViewModel.trendingResponse.observe(this) {
                    if (it != null) {
                        binding.progressBar.visibility = View.GONE
                        binding.noDataIv.visibility = View.GONE
                        binding.trendingRv.visibility = View.VISIBLE
                        // binding.adCard.visibility = View.VISIBLE

                        adapter = TrendingsAdapter(
                            it.list_data as ArrayList<TrendingVideoData>,
                            this
                        )


                        /*  activity?.let {
                              it.setNativeAd(
                                  binding.shimmerViewContainer,
                                  binding.refadslayout,
                                  binding.refadslayoutFb,
                                  binding.adCard,
                                  R.layout.banner_navtive_ad_layout,
                                  R.layout.banner_fb_native_ad_layout,
                                  it.isAlreadyPurchased()!!,
                                  TAG,
                                  mAdPriority,
                                  preLoadedNativeAd,
                                  getString(R.string.native_id)
                              )

                          }
  */



                        adapter.also {
                            binding.trendingRv.adapter = it
                        }

                    } else {
                        binding.apply {
                            this.trendingRv.visibility = View.GONE
                            this.noDataIv.visibility = View.VISIBLE
                            this.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
            else{
                binding.progressBar.visibility = View.GONE
                binding.noDataIv.visibility = View.VISIBLE
            }


        binding.btnFav.setOnClickListener {
            startActivity(Intent(this@TrendingActivity,TrendingFavouritesActivity::class.java))
        }

    }

    override fun onPlayClick(video: TrendingVideoData) {



            if (itemCountClick >= 2) {
                itemCountClick = 1

                InterstitialAdUpdated.getInstance().showInterstitialAdNew(this@TrendingActivity,onAction={

                    val intent = Intent(this@TrendingActivity, TrendingVideoPlayer::class.java)
                    intent.putExtra(TRENDING_VIDEO_URL, video.url)
                    intent.putExtra(TRENDING_VIDEO_TITLE, video.title)
                    Log.e("TAG", "onPlayClick: ${video.url}" )
                    startActivity(intent)

                })

            } else {
                itemCountClick += 1
                val intent = Intent(this@TrendingActivity, TrendingVideoPlayer::class.java)
                intent.putExtra(TRENDING_VIDEO_URL, video.url)
                intent.putExtra(TRENDING_VIDEO_TITLE, video.title)
                startActivity(intent)

            }

    }

    override fun onAddToFavourite(video: TrendingVideoData) {
        super.onAddToFavourite(video)

        Log.e("TAG", "onAddToFavourite: $video" )
        CoroutineScope(Dispatchers.IO).launch {

            dbViewModel.addToFav(video)
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch{

            }
        }

    }

    override fun onShareVideo(video: TrendingVideoData) {
        super.onShareVideo(video)

         shareVideoLink(video.url)
    }

    private fun loadBottomNative() {
       getNativeAdObject("trendingBottomNative", NativeAdOptions.ADCHOICES_TOP_LEFT, getString(
            R.string.native_id), getString(R.string.fb_native_ad),
            ad_periority, { nativeAdO->
                    nativeAdO?.let {
                        when (it) {
                            is NativeAd -> {
                                val inflate = LayoutFbNativeBannerBinding.inflate(LayoutInflater.from(this@TrendingActivity), binding.trendingBottomAdContainter, false)
                                populateFbNativeBanner(it, inflate, binding.trendingBottomAdContainter)
                            }
                            is com.google.android.gms.ads.nativead.NativeAd -> {
                                val inflate = LayoutNativeBannerBinding.inflate(LayoutInflater.from(this@TrendingActivity), binding.trendingBottomAdContainter, false)
                                populateNativeBanner(it, inflate, binding.trendingBottomAdContainter)
                            }
                            else -> {

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