package com.example.videoplayer_and_downloader.UI.fragments

import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.videodownload.adapters.DownloadedVideosAdapter
import com.example.videodownload.allViewModel.MainViewModel
import com.example.videodownload.appUtils.*
import com.example.videodownload.datamodel.SavedDownloadVideos
import com.example.videodownload.download_feature.lists.DownloadQueues
import com.example.videodownload.fragments.BaseFargment
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.VideoPlayerForWhatsapp
import com.example.videoplayer_and_downloader.Utils.ad_periority
import com.example.videoplayer_and_downloader.Utils.isInternetConnected
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.getNativeAdObject
import com.example.videoplayer_and_downloader.databinding.*
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File

class DownloadsFragment : BaseFargment() {

    var _binding : FragmentDownloadsBinding? = null
    val binding get() = _binding!!
    var name : String = ""
    var link : String = ""
    private   var queues: DownloadQueues? = null
    lateinit var bottomSheetDialog: BottomSheetDialog
    var videoList: ArrayList<SavedDownloadVideos> = ArrayList()
    lateinit var downloadedVideosAdapter: DownloadedVideosAdapter
    val viewModel: MainViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDownloadsBinding.inflate(layoutInflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.isInternetConnected().let {
            if (it == true){
                binding.videosTopAdContainter.visibility = View.VISIBLE
                loadTopNative()
            }
        }


        viewModel.downloadedData.observe(viewLifecycleOwner) {
            if (it.size > 0) {
                binding.noDownloadVideoLayout.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.downloadingCompleteRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext())
                downloadedVideosAdapter = DownloadedVideosAdapter(requireActivity(),
                    it,
                    callBack = { pos ->
                        videoMenu(pos)

                    },
                    callBackSelect = { name, link ->
                        playVideo(name, link)
                    })
                downloadedVideosAdapter.notifyDataSetChanged()
                downloadedVideosAdapter.also {
                    binding.downloadingCompleteRecyclerView.adapter = it
                }
            } else {
                binding.noDownloadVideoLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE


            }

        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.initDownloadedData()
        viewModel.loadDownloaderVideo()
    }

    fun videoDownloaded(videoName: String?): Boolean {
        var found = false
        for (video in videoList) {
            if (video.name == videoName) {
                found = true
                break
            }
        }
        return found
    }

    private fun renameVideo(link: String, pos: Int) {

        bottomSheetDialog.dismiss()
        getMainActivity()?.RenameDialog(link) { newName, oldName ->
            val baseName = oldName.split(".")[0]
            val type = oldName.split(".")[1]

            val downloadsFolder = getMainActivity()?.getMainStoragePath()
            val renamedFile = File(downloadsFolder, "$newName.$type")
            val file = File(downloadsFolder, "$baseName.$type")

            val newVideoName = "$newName.$type"
            if (videoDownloaded(newVideoName)) {
                getMainActivity()?.showShortMessage("Video with this name already Exist..")
                return@RenameDialog
            }

            if (file.renameTo(renamedFile)) {
                viewModel.downloadedData.observe(viewLifecycleOwner) {
                    it.get(pos).link = newVideoName
                    binding.downloadingCompleteRecyclerView?.adapter?.notifyItemChanged(pos)
                    downloadedVideosAdapter.notifyDataSetChanged()
                    bottomSheetDialog.dismiss()
                    MediaScannerConnection.scanFile(
                        getMainActivity(),
                        arrayOf(renamedFile.toString()),
                        null
                    ) { path, uri -> }
                }

            } else {
                Toast.makeText(activity, "invalid filename", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun deleteVideo(link : String,position: Int) {
        bottomSheetDialog.dismiss()
        getMainActivity()?.DeleteSavedVideo {
            if (it) {
                val downloadFolder = getMainActivity()?.getMainStoragePath()
                downloadFolder?.let {
                    val file = File(downloadFolder, link)
                    when {
                        file.exists() -> {
                            val delete = file.delete()
                            when {
                                delete -> {
                                    getMainActivity()?.showShortMessage("Deleted Successfully")
                                    bottomSheetDialog.dismiss()
                                    updateDownloadVideosList(position)


                                }
                                else -> getMainActivity()?.showShortMessage("Failed to delete this video")
                            }
                        }
                        else -> getMainActivity()?.showShortMessage("video not found")
                    }
                } ?: getMainActivity()?.showShortMessage("video not found")

            }
        }
    }
    private fun shareVideo(link: String) {
        val downloadFolder = getMainActivity()?.getMainStoragePath()
        downloadFolder?.let {
            val file = File(downloadFolder, link)
            getMainActivity()?.shareVideoFile(file)
        }
    }
    private fun playVideo(name : String,link : String) {
        val downloadFolder = getMainActivity()?.getMainStoragePath()
        downloadFolder?.let {
            val bundle = Bundle()
            bundle.putString(Constants.videoFilePath, name)
            bundle.putString(Constants.videoTitle, link)

            val intent = Intent(context, VideoPlayerForWhatsapp::class.java)
            intent.putExtra(Constants.KEY_VIDEO_DATA_BUNDLE, bundle)
            startActivity(intent)
        }
    }
    fun videoMenu(position: Int){
        bottomSheetDialog = BottomSheetDialog(requireContext())
        var binding : SelectedVideoBottomSheetBinding
        binding = SelectedVideoBottomSheetBinding.inflate(layoutInflater)
        viewModel.downloadedData.observe(viewLifecycleOwner) {
            val downloadVideo = it[position]
            name = downloadVideo.name
            link = downloadVideo.link
            Glide.with(requireActivity()).load(downloadVideo.name).into(
                binding.imgVideo
            )
            binding.tvVideoName.text = downloadVideo.link
            binding.tvVideoSize.text = downloadVideo.page
        }
        binding.apply {
            imgVideoShare.setOnClickListener {
                shareVideo(link)
            }
            imgVideoRename.setOnClickListener {
                renameVideo(link,position)
            }
            imgVideoDelete.setOnClickListener {
                deleteVideo(link,position)
            }
            btnCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }

    private fun updateDownloadVideosList(position: Int){
        downloadedVideosAdapter.downloadedArrayList.removeAt(position)
        downloadedVideosAdapter.notifyDataSetChanged()
    }

    companion object{
        fun newInstance( ) = DownloadsFragment()
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