package com.example.videodownload.callbacklisteners

interface downloadProgressClickListener {
    fun onDownloadPause()
    fun onDownloadStart()
    fun onDownloadCancel(position: Int)
    fun onOpenLinkClicked(position: Int)
}