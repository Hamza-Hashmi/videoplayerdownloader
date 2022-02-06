package com.example.videoplayer_and_downloader.UI.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.UI.activites.FileManagerActivity
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adapters.VideoFolderAdapter
import com.example.videoplayer_and_downloader.adapters.VideoHistoryAdapter
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.getNativeAdObject
import com.example.videoplayer_and_downloader.databinding.FragmentVideoFoldersFragmentsBinding
import com.example.videoplayer_and_downloader.databinding.LayoutFbNativeBannerBinding
import com.example.videoplayer_and_downloader.databinding.LayoutNativeBannerBinding
import com.example.videoplayer_and_downloader.models.Folder
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoFoldersFragments :  MyBaseFragment<FragmentVideoFoldersFragmentsBinding>(FragmentVideoFoldersFragmentsBinding::inflate) {

    val TAG="FolderFragment"

    private lateinit var historyAdaptor: VideoHistoryAdapter

    private lateinit var foldersAdaptor: VideoFolderAdapter

    private var folderDataPresent = true

    override fun initViews() {

        binding.btnFileManager.setOnClickListener {
            if (checkPermission()) {
                //permission allowed
                val intent: Intent = Intent(requireContext(), FileManagerActivity::class.java)
                val path = Environment.getExternalStorageDirectory().path
                intent.putExtra("path", path)
                startActivity(intent)
            } else {
                //permission not allowed
                requestPermission()
            }
        }
        foldersAdaptor= VideoFolderAdapter(getFolderClickInterface(),true,R.layout.folder_row
        ,getString(R.string.native_id), getString(R.string.fb_native_ad), keyPriority =  ad_periority)
        binding.folderRv.layoutManager=listLayoutManager
        binding.folderRv.adapter = foldersAdaptor
        initHistoryRecyclerView()

    }

    override fun setObservers() {
        super.setObservers()

        myVideosDataViewModel.allFolderData.observe(viewLifecycleOwner) { folder ->
            Log.e("TAG", "onViewCreated: folder_list ${folder.size}")
            if (folder.isEmpty()) {
                binding.folderRv.visibility = View.GONE
                binding.noDataIv.visibility = View.VISIBLE
                binding.noDataTv.visibility = View.VISIBLE
                folderDataPresent = false
            } else {
                binding.folderRv.visibility = View.VISIBLE
                binding.noDataIv.visibility = View.GONE
                binding.noDataTv.visibility = View.GONE
                folderDataPresent = true

                context?.let {
                    foldersAdaptor.setData(
                        folder,it.isInternetConnected()
                    )
                    foldersAdaptor.notifyDataSetChanged()
                }

            }

            binding.btnFileManager.visibility = View.VISIBLE
        }

        //        binding.recentPlayedRv.adapter = historyAdaptor
        databaseViewModel.allHistory.observe(viewLifecycleOwner) { history ->
            Log.e(TAG, "initHistoryRecyclerView_call:  ${history.size}")
            if (history.isEmpty()) {
                binding.recentPlaylistTv.visibility = View.GONE
                binding.recentPlayedRv.visibility = View.GONE
                if (!isHideAd){
                    context?.isInternetConnected()?.let {
                        if(it){
                            loadTopNative()
                        }else{
                            binding.folderTopAdContainter.visibility = View.GONE
                        }
                    }
                }
               binding.noDataLayout.visibility = View.VISIBLE
            } else {
                binding.recentPlaylistTv.visibility = View.VISIBLE
                binding.recentPlayedRv.visibility = View.VISIBLE
               binding.noDataLayout.visibility = View.GONE
                binding.folderTopAdContainter.visibility = View.GONE
                context?.let { conxt ->
                    historyAdaptor.setData(
                        history,
                        isNetworkAvilable = conxt.isNetworkConnected()
                    )
                    historyAdaptor.notifyDataSetChanged()
                }
            }
        }
    }


    private fun initHistoryRecyclerView() {
        historyAdaptor = VideoHistoryAdapter(
            getVideoClickInterface(),
            getString(R.string.native_id), getString(R.string.fb_native_ad), keyPriority =  ad_periority
        )
        context?.let {

            val mLayoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false)

            binding.recentPlayedRv.apply {

                layoutManager = mLayoutManager
//                isNestedScrollingEnabled=false

                adapter = historyAdaptor
            }

        }

    }

    override fun loadData() {
        super.loadData()
        CoroutineScope(Dispatchers.IO).launch {
            databaseViewModel.getHistory()
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                updateDataFromRepo()

            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun updateDataFromRepo() {
        CoroutineScope(Dispatchers.Default).launch {

            myVideosDataViewModel.getUpdatedData()

        }.invokeOnCompletion {

        }


    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else false
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                requireContext(),
                "Storage permission is requires,please allow from settings",
                Toast.LENGTH_SHORT
            ).show()
        } else ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            111
        )
    }

    private fun loadTopNative() {
        context?.getNativeAdObject("VideoFolderTopNatvie", NativeAdOptions.ADCHOICES_TOP_LEFT, getString(
            R.string.native_id), getString(R.string.fb_native_ad),
            ad_periority, { nativeAdO->
                context?.let {cont->
                    nativeAdO?.let {
                        when (it) {
                            is NativeAd -> {
                                val inflate = LayoutFbNativeBannerBinding.inflate(LayoutInflater.from(cont), binding.folderTopAdContainter, false)
                                populateFbNativeBanner(it, inflate, binding.folderTopAdContainter)
                            }
                            is com.google.android.gms.ads.nativead.NativeAd -> {
                                val inflate = LayoutNativeBannerBinding.inflate(LayoutInflater.from(cont), binding.folderTopAdContainter, false)
                                populateNativeBanner(it, inflate, binding.folderTopAdContainter)
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