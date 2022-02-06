package com.example.videodownload.twiter

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TwitterResponseDataClass(

    @SerializedName("source")
    public var source: String,

    @SerializedName("text")
    public val text: String,

    @SerializedName("thumb")
    public val thumb: String,

    @SerializedName("type")
    public val type: String,

    @SerializedName("duration")
    public val duration :Int,

    @SerializedName("bitrate")
    public val bitrate : Int,

    @SerializedName("url")
    public val url: String,

    @SerializedName("size")
    public val size :Int
): Serializable
