package com.example.videodownload.allViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videodownload.datamodel.videoDetail


class DownloadedVideosFragmentViewModel : ViewModel() {

    val startDownloadLiveData = MutableLiveData<videoDetail>()
    val downloadCompleteLiveData = MutableLiveData<Boolean>()

    fun startDownloadstartDownload(videoDetail: videoDetail){
        startDownloadLiveData.postValue(videoDetail)
    }

}