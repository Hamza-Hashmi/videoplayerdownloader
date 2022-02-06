package com.example.videoplayer_and_downloader.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SelectedListInfo(
    val listType: String,
    val selectedParent: String,
    val isVideo: Boolean
) : Parcelable {
    override fun toString(): String {
        return "\nList Type = $listType \n Parent= $selectedParent \n isVideo = $isVideo"
    }

}