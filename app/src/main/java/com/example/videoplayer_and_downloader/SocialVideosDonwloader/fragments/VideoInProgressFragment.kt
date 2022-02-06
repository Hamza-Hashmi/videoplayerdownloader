package com.example.videodownload.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videodownload.adapters.VideoInProgressAdapter
import com.example.videodownload.allViewModel.MainViewModel
import com.example.videodownload.appUtils.Constants
import com.example.videodownload.appUtils.Utils
import com.example.videodownload.appUtils.showShortMessage
import com.example.videodownload.callbacklisteners.downloadProgressClickListener
import com.example.videodownload.callbacklisteners.downloadWithNewLink
import com.example.videodownload.datamodel.CheckInternetConnection
import com.example.videodownload.datamodel.videoDetail
import com.example.videodownload.download_feature.lists.DownloadQueues
import com.example.videodownload.download_feature.lists.MyCompletedVideosClass
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils.DownloadUpdateUI
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils.MyDownloadManagerClass
import com.example.videoplayer_and_downloader.Utils.MyApplication
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class VideoInProgressFragment : BaseFargment(),
    MyDownloadManagerClass.OnDownloadFinishedListener,
    MyDownloadManagerClass.OnLinkNotFoundListener,
    downloadWithNewLink,
    MyDownloadManagerClass.onDownloadFailExceptionListener, LifecycleObserver {
    val dashbordViewModel: MainViewModel by sharedViewModel()
    private var downloadsList: ArrayList<videoDetail> = ArrayList()
    private   var queues: DownloadQueues? = null
    private var noProgressVideosLayout: LinearLayout? = null
    private var downloadsAdaptor: VideoInProgressAdapter? = null
    private var isPaused = true
    lateinit var checkInternetConnection : CheckInternetConnection
    private var mVideoUiUpdate: DownloadUpdateUI? = null
    private var completedVideos: MyCompletedVideosClass? = null
    private lateinit var recyclerView: RecyclerView
    override fun onResume() {
        super.onResume()
        Log.e("LOG_TAG", "onResume: Progress on Rsume is called..")

        if (Utils.isServiceRunning(
                MyDownloadManagerClass::class.java,
                MyApplication.getMyApplicationContext()?.applicationContext!!
            )
        ) {
            //callback.invoke(false)
            isPaused = false
            startVideoTracking()
            dashbordViewModel.isServiceRunning.postValue(true)
        } else {
            // callback.invoke(true)
            dashbordViewModel.isServiceRunning.postValue(false)
            isPaused = true
            stopVideoTracking()
        }

        updateVideosList(isResumed = true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video_in_progress, container, false)
        downloadsList = ArrayList()
        Log.e("LOG_TAG", "onCreateView : Progress on Create View  is called..")

        noProgressVideosLayout = view.findViewById(R.id.no_progress_video_layout)
        recyclerView = view.findViewById(R.id.videoInProgressRecyclerView)
        downloadsAdaptor = VideoInProgressAdapter(
            progressDownloadCLick(),
            downloadsList,isPaused)
        val llm = LinearLayoutManager(requireActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        recyclerView.adapter = downloadsAdaptor
        MyDownloadManagerClass.setOnDownloadFinishedListener(this)
        MyDownloadManagerClass.setOnLinkNotFoundListener(this)
        MyDownloadManagerClass.setOnDownloadFailExceptionListener(this)
        checkInternetConnection = CheckInternetConnection(requireContext())
        checkInternetConnection.observe(viewLifecycleOwner) {
            if (it) {
                startDownloadingVideo()
            }
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("LOG_TAG", "onCreateView : Progress on View Created   is called..")

    }


    fun startDownloadingVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                dashbordViewModel.isServiceRunning.postValue(true)
                val downloadService = baseAppClass()?.getDownloadService()
                if (downloadsList.isNotEmpty()) {
                    val topVideo = downloadsList[0]
                    Log.i("video",""+topVideo)
                    downloadService?.putExtra("link", topVideo.link)
                    downloadService?.putExtra("name", topVideo.name)
                    downloadService?.putExtra("type", topVideo.type)
                    downloadService?.putExtra("size", topVideo.size)
                    downloadService?.putExtra("page", topVideo.page)
                    downloadService?.putExtra("chunked", topVideo.chunked)
                    downloadService?.putExtra("website", topVideo.website)
                    baseAppClass()?.startService(downloadService)
                    getMainActivity()?.runOnUiThread {
                        isPaused = false
                        //callback.invoke(false)
                        //     mainActivity.showToastFromResource(R.string.download_started)
                    }
                    startVideoTracking()
                }
            } else {
                ProcessLifecycleOwner.get().lifecycle.addObserver(this)
            }
        } else {
            dashbordViewModel.isServiceRunning.postValue(true)
            val downloadService = baseAppClass()?.getDownloadService()
            if (downloadsList.isNotEmpty()) {
                val topVideo = downloadsList[0]
                downloadService?.putExtra("link", topVideo.link)
                downloadService?.putExtra("name", topVideo.name)
                downloadService?.putExtra("type", topVideo.type)
                downloadService?.putExtra("size", topVideo.size)
                downloadService?.putExtra("page", topVideo.page)
                downloadService?.putExtra("chunked", topVideo.chunked)
                downloadService?.putExtra("website", topVideo.website)
                baseAppClass()?.startService(downloadService)
                getMainActivity()?.runOnUiThread {
                    isPaused = false
                    //callback.invoke(false)
                    //     mainActivity.showToastFromResource(R.string.download_started)
                }
                startVideoTracking()
            }
        }

    }


    fun pauseVideoDownload() {
        if (downloadsList.isEmpty())
        //getMainActivity()?.showToast("Video not found")
        else {
            MyDownloadManagerClass.stop()
            getMainActivity()?.runOnUiThread {
                stopVideoTracking()
                //callback.invoke(true)
                isPaused = true
                getMainActivity()?.showShortMessage("Downloading Pause")
                updateVideosList()
            }
        }
    }

    private fun startVideoTracking() {
        mVideoUiUpdate?.let {
            it.startUpdates()
        } ?: startVideoTrackingInitilizing()
    }


    private fun startVideoTrackingInitilizing() {
        mVideoUiUpdate = DownloadUpdateUI {
            updateVideosList()
        }
        mVideoUiUpdate?.startUpdates()
    }

    private fun stopVideoTracking() {
        mVideoUiUpdate?.let {
            it.stopUpdates()
        }
    }

    private fun updateVideosList(isResumed: Boolean = false) {
        getMainActivity()?.let {
            queues = DownloadQueues.load(it)!!
            downloadsList = queues?.list as ArrayList

            if (downloadsList.size > 0) {
                noProgressVideosLayout?.visibility = View.GONE
            } else {
                noProgressVideosLayout?.visibility = View.VISIBLE
            }
            downloadsAdaptor?.updateVideoList(downloadsList, isPaused)
        }
    }

    private fun saveQueues() {
        queues?.save(getMainActivity())
    }

    private fun progressDownloadCLick(): downloadProgressClickListener {

        val clickObject = object : downloadProgressClickListener {

            override fun onDownloadPause() {
                pauseVideoDownload()
            }

            override fun onDownloadStart() {
                startDownloadingVideo()
            }

            override fun onDownloadCancel(position: Int) {
                if (position < downloadsList.size)
                    downloadsList.removeAt(position)
                saveQueues()
                updateVideosList()
                //MyDownloadManagerClass.stop()
                if (position == 0)
                    startDownloadingVideo()
            }

            override fun onOpenLinkClicked(position: Int) {
                try {
                    if (position < downloadsList.size)
                        copyPageUrl(downloadsList[position].page)
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
        }
        return clickObject
    }

    override fun onDownloadFinished() {

        Log.e("LOG_TAG", "onDownloadFinished: Progress frament download finsih called")
        getMainActivity()?.runOnUiThread {
            dashbordViewModel.isServiceRunning.postValue(false)
            updateVideosList()
            stopVideoTracking()
            if (!downloadsList.isNullOrEmpty()) {
                val name = downloadsList[0].name
                val type = downloadsList[0].type
                val link = downloadsList[0].link
                val page = downloadsList[0].page

                downloadsList.removeAt(0)
                saveQueues()
                downloadsAdaptor?.notifyItemRemoved(0)

                updateVideosList()
            }
            startDownloadingVideo()
        }
    }

    private fun addCompletedVideoList(name: String?, type: String?, link: String?, page: String?) {

        completedVideos?.addVideo(requireContext(), "$name.$type", link, page)
            ?: initializeCompletedList(name, type, link, page)
    }

    private fun initializeCompletedList(name: String?, type: String?, link: String?, page: String?) {
        completedVideos = MyCompletedVideosClass.load(requireContext())
        addCompletedVideoList(name, type, link, page)
    }

    override fun onLinkNotFound() {
        getMainActivity()?.runOnUiThread {

            stopVideoTracking()
            if (downloadsList.isNotEmpty()) {
                val video = downloadsList[0]
                val inactiveDownload = videoDetail()
                inactiveDownload.name = video.name
                inactiveDownload.link = video.link
                inactiveDownload.page = video.page
                inactiveDownload.size = video.size
                inactiveDownload.type = video.type
                downloadsList.removeAt(0)
                updateVideosList()
            }
            startDownloadingVideo()
        }
    }

    override fun onDownloadLink(download: videoDetail) {

        if (Utils.isServiceRunning(
                MyDownloadManagerClass::class.java,
                baseAppClass()?.applicationContext!!
            )
        ) {
            dashbordViewModel.isServiceRunning.postValue(true)
            pauseVideoDownload()
        }else{
            dashbordViewModel.isServiceRunning.postValue(false)

        }
        getMainActivity()?.runOnUiThread {
            downloadsList.add(0, download)
            saveQueues()
            updateVideosList()
            startDownloadingVideo()
        }
    }

    private fun copyPageUrl(url: String?) {
        copy_text(url)
    }

    fun copy_text(outputText: String?) {

        if (!outputText?.isEmpty()!!) {
            val clipboard =
                getMainActivity()?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied url", outputText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(getMainActivity(), "Url copied", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(getMainActivity(), "No Url Found", Toast.LENGTH_LONG).show()
        }

    }
    override fun onDestroyView() {
        stopVideoTracking()
        MyDownloadManagerClass.setOnDownloadFinishedListener(null)
        MyDownloadManagerClass.setOnLinkNotFoundListener(null)
        super.onDestroyView()
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDownloadFailException(message: String?) {
        Log.e(Constants.LOG_TAG, "onDownloadFailException: Catch here in Progress fragment -> " + message.toString())
        getMainActivity()?.showShortMessage("Downloading Stop!")
        pauseVideoDownload()
    }

}
