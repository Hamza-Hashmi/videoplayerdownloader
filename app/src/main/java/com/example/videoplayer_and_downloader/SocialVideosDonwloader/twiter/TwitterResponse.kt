package com.example.videodownload.twiter

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class TwitterResponse(
    @SerializedName("videos")
    val videos: ArrayList<TwitterResponseDataClass>
):Serializable