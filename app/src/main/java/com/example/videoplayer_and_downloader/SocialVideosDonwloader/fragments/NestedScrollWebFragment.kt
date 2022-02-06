package com.example.videodownload.fragments

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.videodownload.allViewModel.MainViewModel
import com.example.videodownload.apiCalling.GetDataService
import com.example.videodownload.apiCalling.RetrofitClientInstance
import com.example.videodownload.appUtils.*
import com.example.videodownload.appUtils.browsing_features.NestedScrollWebView
import com.example.videodownload.appUtils.browsing_features.VideoContentSearch
import com.example.videodownload.appUtils.browsing_features.VimeoContentSearch
import com.example.videodownload.bottom_sheet_dialogs.DownloadVideoBottomSheet
import com.example.videodownload.download_feature.lists.VideoDetection
import com.example.videodownload.twiter.TwitterResponse
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.AdBlockers.AdBlockManager
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.AdBlockers.AdBlocker
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.NetworkHelper
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.TinyDB
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel.VideoListModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.*
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

class NestedScrollWebFragment : BaseFargment(), View.OnTouchListener,
    View.OnClickListener, View.OnLongClickListener {

    companion object {

        @JvmStatic
        fun newInstance(sitesUrl: String?, siteName:String?) =
            NestedScrollWebFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.KEY_URL, sitesUrl)
                    putString(Constants.KEY_SITE_NAME, siteName)
                }
            }
    }

    private var mainView: View? = null
    var getSitesUrl = ""
    var getUrlType = ""
    private var loadingPageProgressBar: ProgressBar? = null
    private var sslSocketFactory: SSLSocketFactory? = null
    var nestedScrollWebView: NestedScrollWebView? = null
    val mainViewModel: MainViewModel by sharedViewModel()
    private var loadedVideoFirsTime = false
    private var orientation = 0
    private var gestureDetector: GestureDetector? = null
    var videoListClass: VideoListClass? = null
    private var downloadFileView: View? = null
    private var downloadVideosButton: ImageView? = null
    private var moved = false
    private var prevX = 0f
    private var prevY = 0f
    private var downloadWithMobileData = false
    private var videoDetectionInitiator: VideoDetection? = null
    private var isDetectingOn = false
    var clipboardManager: ClipboardManager? = null
    var bHasClipChangedListener = false
    private var adBlocker: AdBlocker? = null
    private val adblock: AdBlockManager = AdBlockManager()
    var isLoading: Boolean = false
    lateinit var urlBox : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSitesUrl = arguments?.getString(Constants.KEY_URL).toString()
        getUrlType = arguments?.getString(Constants.KEY_SITE_NAME).toString()
        sslSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()
        retainInstance = true
        downloadWithMobileData = TinyDB.getInstance(getMainActivity()).getBoolean(Constants.isMobileDataEnabled)
        videoDetectionInitiator = VideoDetection(VideoContentDetector(), getMainActivity())
        isDetectingOn = TinyDB.getInstance(getMainActivity()).getBooleanDownlaod(Constants.isDetectionOn)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_nested_scroll_web, container, false)

        if (mainView == null || resources.configuration.orientation != orientation) {
            var visibility = View.VISIBLE
            if (view != null) {
                visibility = mainView?.visibility!!
            }
            mainView?.visibility = visibility
            if (nestedScrollWebView == null) {
                nestedScrollWebView = mainView!!.findViewById(R.id.nestedScrollWebView)
            } else {
                val page1 = mainView!!.findViewById<View>(R.id.nestedScrollWebView)
                (mainView as ViewGroup).removeView(page1)
                (nestedScrollWebView!!.parent as ViewGroup).removeView(nestedScrollWebView)
                (mainView as ViewGroup).addView(nestedScrollWebView)
            }
            initReceiver()
            loadingPageProgressBar = mainView!!.findViewById(R.id.loadingPageProgress)
            loadingPageProgressBar?.visibility = View.INVISIBLE

            if (resources.configuration.orientation != orientation) {
                orientation = resources.configuration.orientation
            }
        }
        urlBox = mainView!!.findViewById(R.id.urlBox)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdBlock()
        loadWebSetting()
        initVideoDetector()
        createBottomSheetForDownloading()
        registerObserver()
    }

    private fun registerObserver() {
        mainViewModel.pauseWebView.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                Timber.tag("CHECK_STATES_FRAG").e("registerObserver: Pause The webview ")
                nestedScrollWebView?.onPause()
            } else {
                Timber.tag("CHECK_STATES_FRAG").e("registerObserver: Play The webview ")
                nestedScrollWebView?.onResume()
            }
        })
    }

    private fun setupAdBlock() {
        val prefs = Objects.requireNonNull(requireActivity())
            ?.getSharedPreferences("settings", 0)
        val lastUpdated = prefs?.getString(getString(R.string.adFiltersLastUpdated), "")

        if (lastUpdated != null) {
            adblock.update(lastUpdated, object : AdBlockManager.UpdateListener {
                override fun onAdBlockUpdateBegins() {


                }

                override fun onAdBlockUpdateEnds() {


                }

                override fun onUpdateFiltersLastUpdated(today: String?) {
                    try {
                        activity?.let {
                            val prefs = it.getSharedPreferences("settings", 0)
                            prefs.edit().putString(getString(R.string.adFiltersLastUpdated), today)
                                .apply()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }


                }

                override fun onSaveFilters() {
                    try {
                        adblock.saveFilters(getMainActivity())
                    } catch (E: java.lang.Exception) {
                        E.printStackTrace()
                    }

                }

                override fun onLoadFilters() {
                    try {
                        adblock.loadFilters(getMainActivity())
                    } catch (E: java.lang.Exception) {
                        E.printStackTrace()
                    }

                }
            })
        }
    }

    private fun createBottomSheetForDownloading() {
        if (videoListClass != null) {
            videoListClass?.recreateVideoList()
        } else {
            videoListClass = object : VideoListClass(getMainActivity()) {
            }
        }
    }

    private fun setupAdBlocker() {
        try {
            val file = File(Objects.requireNonNull(getMainActivity())?.filesDir, "ad_filters.dat")
            if (file.exists()) {
                val fileInputStream = FileInputStream(file)
                val objectInputStream = ObjectInputStream(fileInputStream)
                adBlocker = objectInputStream.readObject() as AdBlocker
                objectInputStream.close()
                fileInputStream.close()
            } else {
                adBlocker = AdBlocker()
                val fileOutputStream = FileOutputStream(file)
                val objectOutputStream = ObjectOutputStream(fileOutputStream)
                objectOutputStream.writeObject(adBlocker)
                objectOutputStream.close()
                fileOutputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    @Deprecated("")
    fun updateAdFilters() {
        if (adBlocker != null) {
            adBlocker?.update(Objects.requireNonNull(getMainActivity()))
        } else {
            setupAdBlocker()
            if (adBlocker != null) {
                adBlocker?.update(Objects.requireNonNull(getMainActivity()))
            } else {
                val file = File(
                    Objects.requireNonNull(getMainActivity())?.filesDir, "ad_filters.dat"
                )
                if (file.exists()) {
                    if (file.delete()) {
                        setupAdBlocker()
                        if (adBlocker != null) {
                            adBlocker?.update(activity)
                        }
                    }
                }
            }
        }
    }

    @Deprecated("")
    fun checkUrlIfAds(url: String?): Boolean {
        return adBlocker!!.checkThroughFilters(url)
    }

    fun isUrlAd(url: String?): Boolean {
        if (url == null){
            return false
        }else{
            return adblock.checkThroughFilters(url)
        }
    }

    private fun loadWebSetting() {

        if (!loadedVideoFirsTime) {
            val thread = HandlerThread("Video Extraction Thread")
            thread.start()
            val extractVideoHandler = Handler(thread.looper)

            val webSettings = nestedScrollWebView!!.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.allowUniversalAccessFromFileURLs = true
            webSettings.javaScriptCanOpenWindowsAutomatically = true

            if (getSitesUrl.contains(resources.getString(R.string.facebook_url)))
                nestedScrollWebView?.settings?.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36"

            if (getSitesUrl.contains(getString(R.string.vimeourl))) {

                val vimeoSearch = VimeoContentSearch(
                    baseAppClass()?.applicationContext!!, getVimeoResultInterface()
                )

                nestedScrollWebView?.webViewClient = object : WebViewClient() {

                    override fun doUpdateVisitedHistory(
                        view: WebView?,
                        url: String?,
                        isReload: Boolean
                    ) {
                        super.doUpdateVisitedHistory(view, url, isReload)

                        Handler(Looper.getMainLooper()).post {
                            urlBox?.text = url
                            getSitesUrl = url!!
                        }
                        Log.e("vimeoidflow2", url!!)

                        videoListClass?.clearAll()
                        var vimeoId = url
                        if (vimeoId.contains("com/")) {
                            val comString = vimeoId.substring(vimeoId.indexOf("com/") + 4)
                            if (vimeoId.contains("?")) {
                                vimeoId = comString
                                vimeoId = vimeoId.substring(0, vimeoId.indexOf("?"))
                                Log.e("vimeoidflow3", vimeoId)
                                vimeoSearch.searchVimeoVideo(vimeoId, url)
                            } else {
                                if (Character.isDigit(url[url.length - 1])) {
                                    vimeoId = comString
                                    Log.e("vimeoidflow4", vimeoId)
                                    vimeoSearch.searchVimeoVideo(vimeoId, url)
                                }
                            }
                        }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        return if (!request!!.url.toString().startsWith("intent")) {
                            super.shouldOverrideUrlLoading(view, request)
                        } else true
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        return if (!url!!.startsWith("intent")) {
                            super.shouldOverrideUrlLoading(view, url)
                        } else true
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        Handler(Looper.getMainLooper()).post {
                            urlBox?.text = url
                            getSitesUrl = url!!
                        }
                        loadingPageProgressBar?.visibility = View.VISIBLE

                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        loadingPageProgressBar?.visibility = View.INVISIBLE
                        super.onPageFinished(view, url)
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getMainActivity() != null) {
                            if ((request!!.url.toString().contains("ad") ||
                                        request.url.toString().contains("banner") ||
                                        request.url.toString().contains("pop"))
                                && isUrlAd(
                                    request.url
                                        .toString()
                                )
                            ) {
                                return WebResourceResponse(null, null, null)

                            } else {
                                return null
                            }

                        } else {
                            return shouldInterceptRequest(view, request)
                        }
                    }


                    override fun shouldInterceptRequest(
                        view: WebView?,
                        url: String?
                    ): WebResourceResponse? {
                        if ((url!!.contains("ad") || url.contains("banner") || url.contains("pop")) && isUrlAd(url)
                        ) {
                            return WebResourceResponse("text/javascript", "UTF-8", null)
                        }
                        return super.shouldInterceptRequest(view, url)
                    }
                }

            } else if (!getSitesUrl.contains(getString(R.string.twitterUrll))) {

                nestedScrollWebView?.webViewClient = object : WebViewClient() {

                    private val videoExtract = VideoExtractionRunnable()

                    private val VIDEO_DETECTOR: NestedScrollWebFragment.VideoContentDetector =
                        VideoContentDetector()
                    private var currentPage: String = nestedScrollWebView?.url.toString()

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        Handler(Looper.getMainLooper()).post {
                            urlBox?.text = url
                            getSitesUrl = url!!
                        }
                        loadingPageProgressBar?.visibility = View.VISIBLE
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        loadingPageProgressBar?.visibility = View.INVISIBLE
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
                        if (getMainActivity() != null) {
                            if ((url!!.contains("ad") || url.contains("banner") || url.contains("pop"))
                                && isUrlAd(url)
                            ) {
                                return WebResourceResponse("text/javascript", "UTF-8", null)
                            }
                        }
                        return super.shouldInterceptRequest(view, url)
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getMainActivity() !=
                            null
                        ) {
                            if ((request!!.url.toString().contains("ad") ||
                                        request.url.toString().contains("banner") ||
                                        request.url.toString().contains("pop"))
                                && isUrlAd(
                                    request.url
                                        .toString()
                                )
                            ) {
                                WebResourceResponse(null, null, null)


                            } else null
                        } else {
                            shouldInterceptRequest(view, request!!.url.toString())
                        }
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
                                    //here we can hide the button and clear all list

                                    /*videoList?.clearAll()
                                    browserActivity()?.runOnUiThread {
                                        btnBeforeDownload?.visibility = View.GONE
                                    }*/

                                    VIDEO_DETECTOR.RunNewSearch(
                                        url,
                                        page,
                                        title,
                                        "Unknown Quality",
                                       getMainActivity()
                                    )
                                    if (isDetectingOn) {
                                        VIDEO_DETECTOR.run()
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
            } else {
                Handler(Looper.getMainLooper()).post {
                    downloadVideosButton?.setImageResource(R.drawable.ready_for_download)
                    downloadVideosButton?.visibility = View.GONE

                    downloadFileView?.startAnimation(
                        AnimationUtils.loadAnimation(
                            downloadVideosButton?.context,
                            R.anim.shake
                        )
                    )
                }
            }

            //Setting chrome here below
            nestedScrollWebView?.webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    loadingPageProgressBar?.progress = newProgress
                }

            }
            nestedScrollWebView?.setOnLongClickListener(this)
            nestedScrollWebView?.loadUrl(getSitesUrl)
            loadedVideoFirsTime = true
        } else {
            urlBox?.text = getSitesUrl
        }
    }


    private fun updateDownloadButton() {

        if (downloadVideosButton?.visibility == View.GONE) {
            downloadVideosButton?.visibility = View.VISIBLE
            downloadVideosButton?.setImageResource(R.drawable.ready_for_download)

        }
        downloadVideosButton?.setImageResource(R.drawable.ready_for_download)

        downloadFileView?.startAnimation(
            AnimationUtils.loadAnimation(
                downloadVideosButton?.context,
                R.anim.shake
            )
        )
    }

    private fun getVimeoResultInterface(): VimeoContentSearch.mVimeoResult {

        return object : VimeoContentSearch.mVimeoResult {

            override fun addVimeoItem(url: String, page: String, title: String, quality: String) {

                val thread = HandlerThread("Video Extraction Thread")
                thread.start()
                val extractVideoHandler = Handler(thread.looper)

                val videoDetector: NestedScrollWebFragment.VideoContentDetector =
                    VideoContentDetector()

                class VideoExtractionRunnable : Runnable {
                    private var url = "https://"
                    private var title = ""
                    private var page = ""
                    private var quality = ""
                    fun setUrl(url: String) {
                        this.url = url
                    }

                    fun setTitle(title: String) {
                        this.title = title
                    }

                    fun setPage(page: String) {
                        this.page = page
                    }

                    fun setQuality(quality1: String) {
                        this.quality = quality1
                    }

                    override fun run() {
                        try {
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
                                videoDetector.RunNewSearch(
                                    url,
                                    page,
                                    title,
                                    quality,
                                    getMainActivity()
                                )
                                if (isDetectingOn) {
                                    videoDetector.run()
                                } else {
                                    videoDetectionInitiator!!.reserve(url, page, title, quality)
                                }
                            }
                        } catch (e: java.lang.IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
                val videoExtract = VideoExtractionRunnable()
                videoExtract.setUrl(url)
                videoExtract.setTitle(title)
                videoExtract.setPage(page)
                videoExtract.setQuality(quality)
                extractVideoHandler.post(videoExtract)

            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v == downloadFileView) {
            gestureDetector!!.onTouchEvent(event)

            when (event!!.action) {

                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                    }
                    moved = false

                }
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.rawX
                    prevY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    moved = true
                    val moveX: Float = event.rawX - prevX
                    downloadFileView!!.x = downloadFileView!!.x + moveX
                    prevX = event.rawX
                    val moveY: Float = event.rawY - prevY
                    downloadFileView!!.y = downloadFileView!!.y + moveY
                    prevY = event.rawY
                    val width = resources.displayMetrics.widthPixels.toFloat()
                    val height = resources.displayMetrics.heightPixels.toFloat()
                    if (downloadFileView!!.x + downloadFileView!!.width >= width
                        || downloadFileView!!.x <= 0
                    ) {
                        downloadFileView!!.x = downloadFileView!!.x - moveX
                    }
                    if ((downloadFileView!!.y + downloadFileView!!.height + Objects.requireNonNull(
                            resources.getDimensionPixelSize(
                                R.dimen._160sdp
                            )
                        )) >= height
                        || downloadFileView!!.y <= 0
                    ) {
                        downloadFileView!!.y = downloadFileView!!.y - moveY
                    }
                }
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layoutDownloadFile -> {
                if (videoListClass!!.size > 0) {
                    if (downloadWithMobileData) {
                        if (NetworkHelper.isConnectedMobile(getMainActivity())) {
                            getMainActivity()?.showShortMessage("Can\'t download ! You are using cellular network")
                        } else {
                            if (isDetectingOn) {
                                Utils.setFoundDetails(
                                    Objects.requireNonNull(baseAppClass()!!.applicationContext),
                                    VideoListModel(
                                        videoListClass?.videoInfos,
                                        videoListClass?.currentName
                                    )
                                )
                                activity?.supportFragmentManager.let {
                                    DownloadVideoBottomSheet.newInstance(baseAppClass()?.applicationContext)
                                        .apply {
                                            setStyle(
                                                DialogFragment.STYLE_NO_FRAME,
                                                R.style.CustomBottomSheetDialogTheme
                                            )
                                            show(it!!, tag)
                                        }
                                }
                            } else {
                                isDetectingOn = true
                                videoDetectionInitiator?.initiate()
                            }

                        }
                    } else {
                        Utils.setFoundDetails(Objects.requireNonNull(baseAppClass()!!.applicationContext), VideoListModel(videoListClass?.videoInfos, videoListClass?.currentName))
                        activity?.supportFragmentManager.let {
                            DownloadVideoBottomSheet.newInstance(baseAppClass()?.applicationContext)
                                .apply {
                                    setStyle(
                                        DialogFragment.STYLE_NO_FRAME,
                                        R.style.CustomBottomSheetDialogTheme
                                    )
                                    show(it!!, tag)
                                }
                        }
                    }
                } else {
                    getMainActivity()?.showShortMessage("No Video selected")
                }
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        return true
    }

    override fun onDestroy() {
        nestedScrollWebView?.stopLoading()
        nestedScrollWebView?.destroy()

        super.onDestroy()
    }

    fun initVideoDetector() {
        downloadFileView = mainView?.findViewById(R.id.layoutDownloadFile)
        downloadFileView?.setOnTouchListener(this)
        downloadFileView?.setOnClickListener(this)

        gestureDetector = GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                downloadFileView?.performClick()
                return true
            }
        })
        downloadVideosButton = downloadFileView?.findViewById(R.id.btnVideoDownload)
        //showhideDownloadButton()

        isDetectingOn =
            TinyDB.getInstance(getMainActivity()).getBooleanDownlaod(Constants.isDetectionOn)

    }

    inner class VideoContentDetector : VideoContentSearch() {


        override fun onStartInspectingURL() {

        }

        override fun onFinishedInspectingURL(finishedAll: Boolean) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory)
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
            videoListClass?.addItem(size, type, link, name, page, chunked, website, url, quality)

            getMainActivity()?.runOnUiThread {
                updateDownloadButton()
            }
        }
    }

    private fun initReceiver() {
        Log.e("LOG_TAG", "initReceiver: called --> ")
        clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        //manager?.removePrimaryClipChangedListener(this)
        if (!bHasClipChangedListener) {
            clipboardManager?.addPrimaryClipChangedListener(this)
            bHasClipChangedListener = true
        }
    }

    override fun onPrimaryClipChanged() {

        if (isLoading) {
            isLoading = false
        } else {
            Log.e("LOG_TAG", "onPrimaryClipChanged: clipBoadMangaer ---> ")

            clipboardManager?.let { manager->
                if(manager.primaryClip!= null){
                    if (manager.primaryClip?.itemCount!! > 0) {
                        val clip = manager.primaryClip!!.getItemAt(0).text.toString()
                        if (clip.contains("twitter.com")) {
                            Constants.showProgressDialog(getMainActivity())
                            loadTwitterData(clip)
                        }
                        isLoading = true
                    }
                }

            }

        }

    }

    override fun onPause() {
        nestedScrollWebView?.onPause()
        Log.e("CHECK_STATES_FRAG", "onPause: Browser fragment onPause called")
        super.onPause()
    }

    override fun onResume() {
            nestedScrollWebView?.onResume()
            Log.e("CHECK_STATES_FRAG", "onResume: Browser fragment onResume called")
            super.onResume()
        }

    override fun onDestroyView() {

        nestedScrollWebView?.onPause()
        if (bHasClipChangedListener) {
            clipboardManager?.removePrimaryClipChangedListener(this)
            bHasClipChangedListener = false
        }
        super.onDestroyView()
    }

    private fun loadTwitterData(copiedText: String) {
        try {
            //createFileFolder()
            val url = URL(copiedText)
            val host = url.host
            if (host.contains("twitter.com")) {
                val id: Long = getTweetId(copiedText)!!
                if (id != null) {
                    callGetTwitterData(id.toString())
                }
            } else {
                requireActivity()?.showShortMessage("Invalid twitter Url")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTweetId(s: String?): Long? {
        val split = s?.split("\\/".toRegex())?.toTypedArray()
        val id = split!![5].split("\\?".toRegex()).toTypedArray()[0]
        return id.toLong()
    }

    private fun showBottomSheetForVideoDownload() {
        if (videoListClass!!.size > 0) {
            if (downloadWithMobileData) {
                if (NetworkHelper.isConnectedMobile(getMainActivity())) {
                    getMainActivity()?.showShortMessage("Can\'t download ! You are using cellular network")
                } else {
                    if (isDetectingOn) {
                        Utils.setFoundDetails(
                            Objects.requireNonNull(baseAppClass()!!.applicationContext),
                            VideoListModel(
                                videoListClass?.videoInfos,
                                videoListClass?.currentName
                            )
                        )
                        activity?.supportFragmentManager.let {
                            DownloadVideoBottomSheet.newInstance(baseAppClass()?.applicationContext)
                                .apply {
                                    setStyle(
                                        DialogFragment.STYLE_NO_FRAME,
                                        R.style.CustomBottomSheetDialogTheme
                                    )
                                    show(it!!, tag)
                                }
                        }
                    } else {
                        isDetectingOn = true
                        videoDetectionInitiator?.initiate()
                    }

                }
            } else {
                Utils.setFoundDetails(
                    Objects.requireNonNull(baseAppClass()!!.applicationContext), VideoListModel(
                        videoListClass?.videoInfos,
                        videoListClass?.currentName
                    )
                )
                activity?.supportFragmentManager.let {

                    if (!it?.isStateSaved!!) {
                        DownloadVideoBottomSheet.newInstance(baseAppClass()?.applicationContext)
                            .apply {
                                setStyle(
                                    DialogFragment.STYLE_NO_FRAME,
                                    R.style.CustomBottomSheetDialogTheme
                                )
                                show(it, tag)
                            }
                    }
                }
            }
        } else {
            getMainActivity()?.showShortMessage("No Video selected")
        }
    }

    private fun callGetTwitterData(id: String) {
        val URL = "https://twittervideodownloaderpro.com/twittervideodownloadv2/index.php"
        try {
            //val utils = Utils(activity)
            if (getMainActivity()?.isNetworkAvailable()!!) {
                val service: GetDataService =
                    RetrofitClientInstance().getRetrofitInstance()!!.create<GetDataService>(
                        GetDataService::class.java
                    )

                service.let {
                    GlobalScope.launch {
                        val api: Call<TwitterResponse> = service.callTwitterSimple(URL, id)

                        api.enqueue(object : Callback<TwitterResponse> {
                            override fun onResponse(
                                call: Call<TwitterResponse>,
                                response: Response<TwitterResponse>
                            ) {
                                Constants.hideProgressDialog(getMainActivity())
                                val videoResposne = response.body()?.videos

                                var print = ""
                                for ((index, element) in response.body()?.videos?.withIndex()!!) {
                                    val split = element.url.split("\\/".toRegex()).toTypedArray()
                                    val quality = split[7].split("x".toRegex()).toTypedArray()[0]
                                    print =
                                        "Index is $index and url is ${element.url} + quality -> ${quality}"
                                    Log.e(
                                        "LOG_TAG",
                                        "onResponse: Twitter Successfull Response - >" + print
                                    )
                                }

                                for ((index, item) in videoResposne?.withIndex()!!) {

                                    val split = item.url.split("\\/".toRegex()).toTypedArray()
                                    val quality = split[7].split("x".toRegex()).toTypedArray()[0]

                                    videoListClass?.addItem(
                                        item.size.toString(),
                                        item.type.toString(),
                                        item.url,
                                        item.text,
                                        item.source,
                                        false,
                                        "twitter.com",
                                        item.url,
                                        quality + "P"
                                    )
                                }

                                showBottomSheetForVideoDownload()

                            }

                            override fun onFailure(call: Call<TwitterResponse>, t: Throwable) {
                                Log.e("LOG_TAG", "onResponse: Twitter Failuer Response - >" + t.message)
                                Constants.hideProgressDialog(getMainActivity())
                            }

                        })

                    }
                }
            } else {
                getMainActivity()?.showShortMessage("No internet connection")
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}