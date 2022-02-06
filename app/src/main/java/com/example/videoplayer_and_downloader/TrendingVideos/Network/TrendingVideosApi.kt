package com.example.videoplayer_and_downloader.TrendingVideos.Network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingResponse

interface TrendingVideosApi {

    @GET("/videos")
    fun getTrendingVideos(
        @Query("fields[]") fields: List<String>,
        @Query("sort") sort: String,
        @Query("limit") limit: String

    ): Call<TrendingResponse>

}