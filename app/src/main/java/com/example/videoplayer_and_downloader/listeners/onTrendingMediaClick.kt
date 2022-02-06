package com.example.videoplayer_and_downloader.listeners

import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData

interface onTrendingMediaClick {

    fun onPlayClick(video:TrendingVideoData)
    fun onAddToFavourite(video:TrendingVideoData){}
    fun onRemoveFromFav(video: TrendingVideoData){}
    fun onShareVideo(video:TrendingVideoData){}
}