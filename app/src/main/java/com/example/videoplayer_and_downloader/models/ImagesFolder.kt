package com.example.videoplayer_and_downloader.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImagesFolder(
    var folderName: String?="",
    var folderPath: String,
    val imageslist: ArrayList<Uri>
) : Parcelable