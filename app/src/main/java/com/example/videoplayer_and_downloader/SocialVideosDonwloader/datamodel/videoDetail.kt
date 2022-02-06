package com.example.videodownload.datamodel

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class videoDetail(
    open var size: String? = "",
    var type: String? = "",
    var link: String? = "",
    var name: String? = "",
    var page: String? = "",
    var chunked: Boolean = false,
    var checked: Boolean = false,
    var website: String? = "",
    var downloadUrl: String? = null,
    var quality: String? = "Unknown Quality"
) : Serializable
