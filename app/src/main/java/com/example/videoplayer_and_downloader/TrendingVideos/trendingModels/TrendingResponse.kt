package com.example.videoplayer_and_downloader.TrendingVideos.trendingModels

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TrendingResponse(
    val explicit: Boolean,
    val has_more: Boolean,
    val limit: Int,
    @SerializedName("list")
val list_data: List<TrendingVideoData>,
    val page: Int,
    val total: Int

)