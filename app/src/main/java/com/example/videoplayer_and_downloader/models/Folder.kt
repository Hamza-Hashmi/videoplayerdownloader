package com.example.videoplayer_and_downloader.models

data class Folder(
    val name: String,
    val path: String,
    var videoFiles: ArrayList<MediaModel>,
    var audioFiles: ArrayList<MediaModel>
) {
    fun updateVideoFiles(
        videoFiles: java.util.ArrayList<MediaModel>
    ) {
        this.videoFiles = videoFiles
    }

    fun updateAudioFiles(
        audioFiles: java.util.ArrayList<MediaModel>
    ) {
        this.audioFiles = audioFiles
    }
}