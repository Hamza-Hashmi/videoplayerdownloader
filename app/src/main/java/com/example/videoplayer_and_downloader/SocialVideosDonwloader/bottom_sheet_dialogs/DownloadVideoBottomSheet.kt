package com.example.videodownload.bottom_sheet_dialogs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.VideoPlayerForWhatsapp
import com.example.videodownload.allViewModel.MainViewModel
import com.example.videodownload.appUtils.*
import com.example.videodownload.appUtils.Utils.Companion.dailymotion144pFormat
import com.example.videodownload.appUtils.Utils.Companion.dailymotion144pOtherFormate
import com.example.videodownload.appUtils.Utils.Companion.dailymotion240pFormat
import com.example.videodownload.appUtils.Utils.Companion.dailymotion240pOtherFormate
import com.example.videodownload.appUtils.Utils.Companion.dailymotion360pFormat
import com.example.videodownload.appUtils.Utils.Companion.dailymotion360pOtherFormate
import com.example.videodownload.appUtils.Utils.Companion.dailymotion480OtherFormate
import com.example.videodownload.appUtils.Utils.Companion.dailymotion480pFormat
import com.example.videodownload.appUtils.Utils.Companion.dailymotion720pFormat
import com.example.videodownload.download_feature.lists.DownloadQueues
import com.example.videodownload.download_feature.lists.MyCompletedVideosClass
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel.VideoListModel
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils.MyDownloadManagerClass
import com.example.videoplayer_and_downloader.Utils.MyApplication
import com.example.videoplayer_and_downloader.databinding.DownloadingVideoBottomSheetDialogLayoutBinding


import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DownloadVideoBottomSheet(context: Context?) : BottomSheetDialogFragment() {

    private var mContext = context
    private var checkedItem = 0
    private var foundDetails: VideoListModel? = null
    private var videoList = ArrayList<VideoListClass.VideoInfo>()
    private lateinit var alreadyCompletedVideos: MyCompletedVideosClass
    private lateinit var videoDownloadQueues: DownloadQueues
    val mainViewModel: MainViewModel by sharedViewModel()
    lateinit var binding : DownloadingVideoBottomSheetDialogLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DownloadingVideoBottomSheetDialogLayoutBinding.inflate(layoutInflater)
        getVideosList()
        binding.apply {
            tvPlay.setOnClickListener {
                val name = videoList[checkedItem].name
                val link = videoList[checkedItem].link
                playVideo(name,link)

            }
            tvDownlaod.setOnClickListener {
                when {
                    videoList[checkedItem].link.contains(
                        "youtube",
                        true
                    ) -> requireActivity().showShortMessage("Youtube videos not supported")
                    videoDownloading(videoList[checkedItem].link) -> requireActivity().showShortMessage("Video is already downloading")
                    videoDownloaded(videoList[checkedItem].link) -> requireActivity().showShortMessage("Video already downloaded")
                    else -> downloadingStart(checkedItem)
                }
                dismiss()
            }
        }
        return binding.root
    }

    private fun playVideo(name : String,link : String) {
        val downloadFolder = requireActivity()?.getMainStoragePath()
        downloadFolder?.let {
            val bundle = Bundle()
            bundle.putString(Constants.videoFilePath, link)
            bundle.putString(Constants.videoTitle, name)

            val intent = Intent(context, VideoPlayerForWhatsapp::class.java)
            intent.putExtra(Constants.KEY_VIDEO_DATA_BUNDLE, bundle)
            startActivity(intent)
        }
    }

    fun downloadingStart(position: Int) {
        val video: VideoListClass.VideoInfo = videoList[position]
        val queues = DownloadQueues.load(mContext)
        queues?.insertToTop(
            video.size,
            video.type,
            video.link,
            video.name,
            video.page,
            video.chunked,
            video.website
        )
        queues?.save(mContext)
        val topVideo = queues?.topVideo
        val downloadService = MyApplication.getMyApplicationContext()?.getDownloadService()
        MyDownloadManagerClass.stop()
        downloadService?.putExtra("link", topVideo?.link)
        downloadService?.putExtra("name", topVideo?.name)
        downloadService?.putExtra("type", topVideo?.type)
        downloadService?.putExtra("size", topVideo?.size)
        downloadService?.putExtra("page", topVideo?.page)
        downloadService?.putExtra("chunked", topVideo?.chunked)
        downloadService?.putExtra("website", topVideo?.website)
        MyApplication.getMyApplicationContext()?.startService(downloadService)

        Toast.makeText(
            context, "Downloading Start...", Toast.LENGTH_LONG
        ).show()
        mainViewModel.downloadVideoEvent.value = video
        mainViewModel.isServiceRunning.postValue(true)

    }


    private fun getVideosList() {
        foundDetails = Utils.getFoundDetails(mContext)
        Log.e("LOG_TAG", "getVideosList: found video list size ${foundDetails?.videos?.size}")
        foundDetails?.let {
            getExistingVideo()
            addVideoList()
            setupUi()
        }
    }

    fun videoDownloading(videoUrl: String?): Boolean {
        var found = false
        for (video in videoDownloadQueues.list) {
            if (video.link == videoUrl) {
                found = true
                break
            }
        }
        return found
    }

    fun videoDownloaded(videoUrl: String?): Boolean {
        var found = false
        for (video in alreadyCompletedVideos.videos) {
            if (video.link == videoUrl) {
                found = true
                break
            }
        }
        return found
    }

    private fun setupUi() {
        binding.tvVideoPlatform.text = foundDetails!!.videos[0].name
        val video = foundDetails!!.videos[0]

        val thumbnailUrl = when {
            mContext?.isDailymotionDownloadUrl(video)!! -> mContext?.getDailyMotionVideoThumbnail(
                video
            )
            else -> video.link
        }
        Glide.with(mContext!!).load(thumbnailUrl).into(binding.ivVideoLogo)
        val sizeFormatted = Formatter.formatShortFileSize(context, video.size!!.toLong())
        binding.tvVideoSize.text = sizeFormatted

    }

    fun getExistingVideo() {
        alreadyCompletedVideos = mContext?.let {
            MyCompletedVideosClass.load(it)
        }!!
        videoDownloadQueues = DownloadQueues.load(mContext)!!
    }

    private fun addVideoList() {

        for ((index, video) in foundDetails!!.videos.withIndex()) {

            if (mContext?.isDailymotionDownloadUrl(video)!!) {

                val indexGeneralUrl = video.link.indexOf("_mp4_h264_aac")

                if (indexGeneralUrl > 0) {
                    val formatUrl = video.link.subSequence(indexGeneralUrl, video.link.length)
                    Log.e("LOG_TAG", "addVideoList: dailymotion formate url -> " + formatUrl)
                    video.quality = when (formatUrl) {
                        dailymotion144pFormat() -> mContext?.getStringFromResource(R.string.p144)
                        dailymotion240pFormat() -> mContext?.getStringFromResource(R.string.p240)
                        dailymotion360pFormat() -> mContext?.getStringFromResource(R.string.p360)
                        dailymotion480pFormat() -> mContext?.getStringFromResource(R.string.p480)
                        dailymotion720pFormat() -> mContext?.getStringFromResource(R.string.p720)
                        dailymotion480OtherFormate() -> mContext?.getStringFromResource(R.string.p480)
                        dailymotion360pOtherFormate() -> mContext?.getStringFromResource(R.string.p360)
                        dailymotion240pOtherFormate() -> mContext?.getStringFromResource(R.string.p240)
                        dailymotion144pOtherFormate() -> mContext?.getStringFromResource(R.string.p144)
                        else -> mContext?.getStringFromResource(R.string.empty)
                    }
                    val mySize = getVideoSize(video.quality, video.size)
                    video.size = mySize
                }
            }
            video.checked = false
            videoList.add(video)
        }

        videoList[0].checked = true
        checkedItem = 0

    }
    private fun getVideoSize(quality: String?, initialSize: String): String {
        return when (quality) {
            mContext?.getStringFromResource(R.string.p144) -> {
                try {
                    (initialSize.toLong() * 75).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            mContext?.getStringFromResource(R.string.p240) -> {
                try {
                    (initialSize.toLong() * 100).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            mContext?.getStringFromResource(R.string.p360) -> {
                try {
                    (initialSize.toLong() * 120).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            mContext?.getStringFromResource(R.string.p480) -> {
                try {
                    (initialSize.toLong() * 200).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            mContext?.getStringFromResource(R.string.p720) -> {
                try {
                    (initialSize.toLong() * 240).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            else -> initialSize
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(context: Context?): DownloadVideoBottomSheet {
            val fragment = DownloadVideoBottomSheet(context)
            return fragment
        }
    }


}