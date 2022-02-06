package com.example.videodownload.callbacklisteners

import com.example.videodownload.datamodel.videoDetail


interface downloadWithNewLink {
    fun onDownloadLink(download: videoDetail)
}