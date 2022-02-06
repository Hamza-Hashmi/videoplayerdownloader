package com.example.videodownload.repository

import android.content.Context
import com.bumptech.glide.Glide
import com.example.videodownload.appUtils.getFileSize
import com.example.videodownload.datamodel.SavedDownloadVideos


class DataRepo(var context: Context) {
    lateinit var savedDownloadVideosList : ArrayList<SavedDownloadVideos>

    fun loadDonloadedVideos() {

        savedDownloadVideosList = ArrayList()
        val files = context.filesDir?.listFiles()

        if (files != null) {
            for (file in files)
            {
                if (file.absolutePath.endsWith(".mp4") || file.absolutePath.endsWith(".ts")){
                    var video = SavedDownloadVideos(file.path,file.name,context.getFileSize(file.length()))
                    savedDownloadVideosList.add(video)
                }
            }

        }

    }
}