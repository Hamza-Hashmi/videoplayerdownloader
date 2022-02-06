package com.example.videoplayer_and_downloader.TrendingVideos.trendingModels

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Keep
@Entity(tableName = "TrendingVideoData")
data class TrendingVideoData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @SerializedName("embed_url")
    val embed_url: String,
    @SerializedName("allow_embed")
    val allow_embed: String,
    @SerializedName("thumbnail_720_url")
    val thumbnail_720_url: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)