package com.example.videoplayer_and_downloader.TrendingVideos

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.videodownload.appUtils.Utils.Companion.getFoundDetails
import com.example.videodownload.appUtils.browsing_features.VideoContentSearch
import com.example.videodownload.appUtils.getDailyMotionVideoThumbnail
import com.example.videodownload.appUtils.isDailymotionDownloadUrl
import com.example.videodownload.download_feature.lists.DownloadQueues
import com.example.videodownload.download_feature.lists.MyCompletedVideosClass
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel.Commons
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel.VideoDetectionForTrendingVideos
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel.VideoListModel
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils.MyDownloadManagerClass
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloaderAdapters.AvailableVideoFormatAdapter
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.whatsApp.Common
import com.example.videoplayer_and_downloader.Utils.MyApplication
import com.example.videoplayer_and_downloader.Utils.TRENDING_VIDEO_TITLE
import com.example.videoplayer_and_downloader.Utils.TRENDING_VIDEO_URL
import com.example.videoplayer_and_downloader.Utils.toast
import com.example.videoplayer_and_downloader.databinding.BottomSheetLayoutForTrendingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory
import kotlin.collections.ArrayList

class TrendingVideoPlayer : AppCompatActivity() {
    var videoUrl = ""
    var videoTitle = ""
    private var defaultSSLSF: SSLSocketFactory? = null
    var Webpage: WebView? = null
    private var videoList: VideoListClass? = null
    var downloadTrendingVideoBtn: ImageView? = null
    private var videoDetectionInitiator: VideoDetectionForTrendingVideos? = null
    private var isDetectingOn = true
    var mVideoList = ArrayList<VideoListClass.VideoInfo>()

   private var foundDetails: VideoListModel? = null

    val mimeType = "text/html"
    val encoding = "UTF-8"
    var html: String? = null
    var lastOne = ""
    var checkedItem = 0

    lateinit var progressBarFullVideo: ProgressBar
    private lateinit var alreadyCompletedVideos: MyCompletedVideosClass
    private lateinit var videoDownloadQueues: DownloadQueues
    lateinit var avialableAdapter: AvailableVideoFormatAdapter


    val TAG = "DailyMotionVideoPlayer"
    private fun disableClick(view: View) {
        view!!.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            view.isEnabled = true
        }, 2000)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trending_video_player)

        defaultSSLSF = HttpsURLConnection.getDefaultSSLSocketFactory()
        videoDetectionInitiator = VideoDetectionForTrendingVideos(
            singleVideoContentSearchTrending(),
            this@TrendingVideoPlayer
        )
        videoUrl = intent.extras?.getString(TRENDING_VIDEO_URL).toString()
        videoTitle = intent.extras?.getString(TRENDING_VIDEO_TITLE).toString()



        Log.e("TAG", "onCreate: videoUrl $videoUrl" )


        Log.e("TAG", "onCreate: " + videoUrl )

        progressBarFullVideo = findViewById(R.id.progressBarFullVideo)
        downloadTrendingVideoBtn = findViewById(R.id.downloadTrendingVideoBtn)


        Webpage = findViewById(R.id.webViewForPlaying)


        createBottomSheetForDownloading()
        Log.e(
            "Constants.TAG",
            "onCreateView: video url is -> ${lastOne} ++++ full url -> ${videoUrl}"
        )
        loadWebSetting()
//        playVideoInWebView()

        val bits: List<String> = videoUrl.split("/")
        lastOne = bits[bits.size - 1]

        downloadTrendingVideoBtn?.setOnClickListener {

            prepareVideoToDownload()

            toast("comming soon")

        }

    }


    private fun getHTMLDailyMotion(videoId: String): String? {
        return """<iframe class="youtube-player" style="width:100%;height:100%;position:absolute;left:0px;top:0px;overflow:hidden"
    frameborder="0" id="ytplayer" type="text/html" src="http://www.dailymotion.com/embed/video/$videoId?fs=0&autoplay=1&ui-logo=0&sharing-enable=0&ui-highlight=fff">
</iframe>
"""
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun playVideoInWebView() {
        val webSettings = Webpage!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.setPluginState(WebSettings.PluginState.ON)
        webSettings.domStorageEnabled = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        Webpage?.getSettings()
            ?.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36")
        Webpage?.loadUrl(videoUrl)
        html = this.getHTMLDailyMotion(lastOne)
        Log.e(TAG, "loadWebSetting: html string -> " + html)
        this.Webpage?.loadData(html!!, mimeType, encoding)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadWebSetting() {

        //Toast.makeText(this@DailyMotionVideoPlayer, "load web setting", Toast.LENGTH_SHORT).show()
        Log.e("TAG", "loadWebSetting: "  )
        val thread = HandlerThread("Video Extraction Thread")
        thread.start()
        val extractVideoHandler = Handler(thread.looper)

        val webSettings = Webpage!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.setPluginState(WebSettings.PluginState.ON)
        webSettings.domStorageEnabled = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setUserAgentString(null)
        Webpage?.webViewClient = object : WebViewClient() {

           private val videoExtract = VideoExtractionRunnable()
            @SuppressLint("StaticFieldLeak")
            private val videoSearch: singleVideoContentSearchTrending =
                singleVideoContentSearchTrending()
            private var currentPage: String = Webpage?.url.toString()

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Handler(Looper.getMainLooper()).post({

                })
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                val page = view!!.url
                val title = view.title
                assert(page != null)


                if (page != currentPage) {
                    currentPage = page!!
                    videoDetectionInitiator!!.clear()
                }
                videoExtract.setUrl(url!!)
                videoExtract.setTitle(title!!)
                videoExtract.setPage(page)
                extractVideoHandler.post(videoExtract)

            }

            override fun shouldInterceptRequest(
                view: WebView?,
                url: String?
            ): WebResourceResponse? {
                return super.shouldInterceptRequest(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }


          inner class VideoExtractionRunnable : Runnable {

                private var url = "https://"
                private var title = ""
                private var page = ""
                fun setUrl(url: String) {
                    this.url = url
                }

                fun setTitle(title: String) {
                    this.title = title
                }

                fun setPage(page: String?) {
                    this.page = page!!
                }

                override fun run() {
                    try {

                        Log.e(TAG, "run " )
                        val urlLowerCase = url.toLowerCase()
                        val filters = resources.getStringArray(R.array.videourls)
                        var urlMightBeVideo = false
                        for (filter in filters) {
                            if (urlLowerCase.contains(filter!!)) {
                                urlMightBeVideo = true
                                break
                            }
                        }
                        if (urlMightBeVideo) {
                            videoSearch.RunNewSearch(
                                url,
                                page,
                                title,
                                "Unknown Quality",
                                this@TrendingVideoPlayer
                            )
                            if (isDetectingOn) {
                                Log.e(TAG, "run:  is detecing on" )
                                videoSearch.run()
                            } else {
                                videoDetectionInitiator?.reserve(
                                    url,
                                    page,
                                    title,
                                    "Unknown Quality"
                                )
                            }
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        //Setting chrome here below
        try{
            Webpage?.webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView?, newProgress: Int) {

                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                }
            }
            Webpage?.loadUrl(videoUrl)
        }catch (e:Exception){
            Log.e("TAG", "loadWebSetting: ${e.message}" )
        }
    }
//}//ye wali bracket remove krni hai jab baqi code uncomment krna hai tab

    private fun createBottomSheetForDownloading() {
        if (videoList != null) {
            videoList?.recreateVideoList()
        } else {
            videoList = object : VideoListClass(this) {
            }
        }
    }

    inner class singleVideoContentSearchTrending() : VideoContentSearch() {


        override fun onStartInspectingURL() {

        }

        override fun onFinishedInspectingURL(finishedAll: Boolean) {
            HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSF)
        }

        override fun onVideoFound(
            size: String?,
            type: String?,
            link: String?,
            name: String?,
            page: String?,
            chunked: Boolean,
            website: String?,
            downloadableUrl: String?,
            quality: String
        ) {
            videoList?.addItem(size, type, link, name, page, chunked, website, url, quality)

            Log.e(TAG, "onVideoFound: " + videoList)
            Handler(Looper.getMainLooper()).post {
                Log.e(TAG, "onVideoFoundHandler:  ", )
                downloadTrendingVideoBtn?.visibility = View.VISIBLE
                downloadTrendingVideoBtn?.setImageResource(R.drawable.ic_video_download)
                progressBarFullVideo.visibility = View.GONE

            }

        }

    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun prepareVideoToDownload() {
        if (videoList!!.size > 0) {
            Commons.setFoundVideoDetails(
                Objects.requireNonNull(this),
                VideoListModel(videoList?.videoInfos!!, videoList?.currentName!!)
            )
            //showBottomSheetDialog

            showBottomSheet()


        } else {
            Toast.makeText(this, "No Network", Toast.LENGTH_SHORT).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun showBottomSheet() {

        val bottomSheetDialog = BottomSheetDialog(this@TrendingVideoPlayer)

        var bottomSheetLayoutBinding = BottomSheetLayoutForTrendingBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetLayoutBinding.root)
      //   bottomSheetLayoutBinding.btnPlay.visibility = View.GONE
        getVideosList(bottomSheetLayoutBinding)




        val name = foundDetails!!.videos[0].name
        val video = foundDetails!!.videos[0]


        Log.e("TAG", "showBottomSheet: video link " + video.link)
        Log.e("TAG", "showBottomSheet: video url" + video.downloadUrl)


        Log.e("TAG", "showBottomSheet: $foundDetails" )

        val thumbnailUrl = when {
            isDailymotionDownloadUrl(video) -> getDailyMotionVideoThumbnail(
                video
            )
            else -> video.link
        }

        bottomSheetLayoutBinding.apply{

            Glide.with(imageView.context).load(thumbnailUrl).into(imageView)
            //this.imageView.load(thumbnailUrl)
            this.videoNameTv.text = name
            this.btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
            this.btnDownload.setOnClickListener {
                Log.e("TAG", "showBottomSheet: ${videoList?.size}" )
                bottomSheetDialog.dismiss()
                when {
                    mVideoList[checkedItem].link.contains(
                        "youtube",
                        true
                    ) -> Toast.makeText(this@TrendingVideoPlayer, "Youtube Video Not SUppported", Toast.LENGTH_SHORT).show()
                    videoDownloading(mVideoList[checkedItem].link) -> Toast.makeText(this@TrendingVideoPlayer, "Video already downloading", Toast.LENGTH_SHORT).show()
                    videoDownloaded(mVideoList[checkedItem].link) -> Toast.makeText(this@TrendingVideoPlayer, "video already downloaded", Toast.LENGTH_SHORT).show()
                    else -> startDownloading(checkedItem)

                }
            }

            bottomSheetDialog.show()
        }
    }
    private fun getVideosList(bottomSheetLayoutBinding: BottomSheetLayoutForTrendingBinding) {
        Log.e("TAG", "getVideosList:1 " )
        foundDetails = Commons.getFoundDetails(this@TrendingVideoPlayer)
        Log.e("TAG", "getVideosList: found video list size ${foundDetails?.videos?.size}")
        foundDetails?.let {
            getExistingVideo()
            setUpRecyclerView(bottomSheetLayoutBinding)
            addVideoList()

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


    fun getExistingVideo() {
        alreadyCompletedVideos = MyCompletedVideosClass.load(this@TrendingVideoPlayer)!!
        videoDownloadQueues = DownloadQueues.load(this@TrendingVideoPlayer)!!
    }

    private fun setUpRecyclerView(bottomSheetLayoutBinding: BottomSheetLayoutForTrendingBinding) {

        bottomSheetLayoutBinding.rvVideosAvailable.layoutManager = GridLayoutManager(applicationContext,3)

        avialableAdapter = AvailableVideoFormatAdapter(ArrayList(), this@TrendingVideoPlayer ) { video, pos ->

            for (i in 0 until videoList!!.size) {
                mVideoList[i].checked = false
            }
            mVideoList[pos].checked = true
            updateList()
            checkedItem = pos




        }

        bottomSheetLayoutBinding.rvVideosAvailable.adapter = avialableAdapter
    }

    private fun addVideoList() {

        for ((index, video) in foundDetails!!.videos.withIndex()) {

            if (isDailymotionDownloadUrl(video)!!) {

                Log.e("TAG", "addVideoList: "+ "dailymotionLink" )
                val indexGeneralUrl = video.link.indexOf("_mp4_h264_aac")

                if (indexGeneralUrl > 0) {
                    val formatUrl = video.link.subSequence(indexGeneralUrl, video.link.length)
                    Log.e("TAG", "addVideoList: dailymotion formate url -> " + formatUrl)
                    video.quality = when (formatUrl) {
                        Commons.dailymotion144pFormat() -> Commons.getStringFromResource(R.string.p144)
                        Commons.dailymotion240pFormat() -> Commons.getStringFromResource(R.string.p240)
                        Commons.dailymotion360pFormat() -> Commons.getStringFromResource(R.string.p360)
                        Commons.dailymotion480pFormat() -> Commons.getStringFromResource(R.string.p480)
                        Commons.dailymotion720pFormat() -> Commons.getStringFromResource(R.string.p720)
                        Commons.dailymotion480OtherFormate() -> Commons.getStringFromResource(R.string.p480)
                        Commons.dailymotion360pOtherFormate() -> Commons.getStringFromResource(R.string.p360)
                        Commons.dailymotion240pOtherFormate() -> Commons.getStringFromResource(R.string.p240)
                        Commons.dailymotion144pOtherFormate() -> Commons.getStringFromResource(R.string.p144)
                        else -> Commons.getStringFromResource(R.string.empty)
                    }
                    val mySize = getVideoSize(video.quality, video.size)
                    video.size = mySize
                }
            }
            video.checked = false
            mVideoList.add(video)
        }


        mVideoList[checkedItem].checked = true

        updateList()
    }

    private fun updateList() {
        avialableAdapter.updateList(mVideoList)
    }

    fun startDownloading(position: Int) {
        val video: VideoListClass.VideoInfo = mVideoList[position]
        val queues = DownloadQueues.load(this@TrendingVideoPlayer)
        queues?.insertToTop(
            video.size,
            video.type,
            video.link,
            video.name,
            video.page,
            video.chunked,
            video.website
        )
        queues?.save(this@TrendingVideoPlayer)
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
            this@TrendingVideoPlayer, "Downloading Start...", Toast.LENGTH_LONG
        ).show()
    }


    private fun getVideoSize(quality: String?, initialSize: String): String {
        return when (quality) {
            Commons.getStringFromResource(R.string.p144) -> {
                try {
                    (initialSize.toLong() * 75).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            Commons.getStringFromResource(R.string.p240) -> {
                try {
                    (initialSize.toLong() * 100).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            Commons.getStringFromResource(R.string.p360) -> {
                try {
                    (initialSize.toLong() * 120).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            Commons.getStringFromResource(R.string.p480) -> {
                try {
                    (initialSize.toLong() * 200).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            Commons.getStringFromResource(R.string.p720) -> {
                try {
                    (initialSize.toLong() * 240).toString()
                } catch (e: Exception) {
                    initialSize
                }
            }
            else -> initialSize
        }
    }
}

