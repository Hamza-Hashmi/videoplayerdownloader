package com.example.videodownload.allViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videodownload.datamodel.SavedDownloadVideos
import com.example.videodownload.repository.DataRepo
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass

class MainViewModel(val dataRepo: DataRepo) : ViewModel() {

    var downloadedData:MutableLiveData<ArrayList<SavedDownloadVideos>> = MutableLiveData<ArrayList<SavedDownloadVideos>>()
    var goToBroswerLiveData = MutableLiveData<Pair<String, String>>()
    val openDownloadFragmentLiveData = MutableLiveData<Boolean>()
    val isServiceRunning = MutableLiveData<Boolean>()
    val pauseWebView = MutableLiveData<Boolean>()

    val downloadVideoEvent = MutableLiveData<VideoListClass.VideoInfo>()

    fun goToNestedScrollWebFragment(url:String, urlType: String) {
        goToBroswerLiveData.postValue(Pair(url, urlType))
    }
    fun initDownloadedData(){
        dataRepo.loadDonloadedVideos()
    }
    fun loadDownloaderVideo() {
        downloadedData.postValue(dataRepo.savedDownloadVideosList)
    }


}