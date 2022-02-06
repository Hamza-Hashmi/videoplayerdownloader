package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

interface NativeAdsCallBack {
    fun onNewAdLoaded(nativeAd: Any, position: Int)
    fun onAdClicked(position: Int)
}