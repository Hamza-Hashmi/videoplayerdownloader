package com.example.videodownload.allViewModel

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videodownload.whatsApp.FileModel
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class WhatsappStatusViewModel: ViewModel() {

    val selectedVideos = MutableLiveData<ArrayList<FileModel>>()

    fun getLiveStatusVideos(whatsAppLocation: String){
        selectedVideos.value = getVidList(File(Environment.getExternalStorageDirectory().toString() + whatsAppLocation))
    }

    fun getLiveStatusVideos(whatsFile: File){
        selectedVideos.value = getVidList(whatsFile)
    }

    fun getVidList(parentDir: File): ArrayList<FileModel>{
       return getVideosStatusList(parentDir)
    }

    fun getVideosStatusList(parentDir: File): ArrayList<FileModel> {
        val inFiles = ArrayList<FileModel>()
        val files: Array<File>? = parentDir.listFiles()

        Log.e("LOG_TAG", "getVideosStatusList: file size is -> ${files?.size}" )

        if (files != null) {
            for (file in files) {
                if (file.name.endsWith(".mp4") || file.name.endsWith(".gif")) {
                    file.mkdir()
                    inFiles.add(FileModel(file, FileModel.Video))
                }
            }
        }
        inFiles.sortWith(Comparator { file1, file2 ->
            Date(file1.name?.lastModified()).compareTo(
                Date(file2.name?.lastModified())
            )
        })
        inFiles.reverse()
        return inFiles
    }



}