package com.example.videoplayer_and_downloader.models

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Parcelable
import com.example.videoplayer_and_downloader.Utils.Constants.Companion.uniqueMedia
import com.example.videoplayer_and_downloader.Utils.getUriPath
import kotlinx.parcelize.Parcelize
import java.io.File
@Parcelize
data class MediaModel (
    var name: String = "",
    var realPath: String ="",
    val isVideo: Boolean = false,
    val isFvt: Boolean = false,
    val isHistory: Boolean = false,
    val isPlaylist: Boolean = false,
    var artist: String = "<unknown>",
    var album: String = "<unknown>",
    val playlistName: String = "",
    var uri: String = "",
    var duration :String = ""
    ) : Parcelable
    {
        fun updateNameAndPath(name: String, path: String) {
            this.uri = Uri.fromFile(File(path)).toString()
            this.name = name
            this.realPath = path
        }

        fun isUniqueMedia(): Boolean {
            return name == uniqueMedia && realPath == uniqueMedia
        }

        fun isPortraitVideo(context: Context): Boolean {
            return try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, getUriPath(uri))
                val bmp: Bitmap? = retriever.frameAtTime
                return bmp?.let {
                    val videoWidth = bmp.width
                    val videoHeight = bmp.height
                    videoWidth <= videoHeight
                } ?: true
            } catch (e: Exception) {
                true
            }
        }
    }