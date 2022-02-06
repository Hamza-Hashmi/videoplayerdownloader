package com.example.videodownload.download_feature.lists

import android.content.Context
import com.example.videodownload.appUtils.PrepareDirectory
import com.example.videodownload.datamodel.videoDetail
import com.example.videoplayer_and_downloader.Utils.MyApplication
import java.io.*
import java.util.*

class DownloadQueues : Serializable {

    private val downloads: MutableList<videoDetail>

    fun save(context: Context?) {
        try {
            val file = File(context?.filesDir, "downloads.dat")
            val fileOutputStream = FileOutputStream(file)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(this)
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun insertToTop(
        size: String?,
        type: String?,
        link: String?,
        name: String?,
        page: String?,
        chunked: Boolean?,
        website: String?
    ) {
        var name = name
        name = getValidName(name, type)
        val video = videoDetail()
        video.link = link
        video.name = name
        video.page = page
        video.size = size
        video.type = type
        video.chunked = chunked!!
        video.website = website
        downloads.add(0, video)
    }

    fun add(
        size: String,
        type: String,
        link: String,
        name: String,
        page: String,
        chunked: Boolean,
        website: String
    ) {
        var name = name
        name = getValidName(name, type)
        val video = videoDetail()
        video.link = link
        video.name = name
        video.page = page
        video.size = size
        video.type = type
        video.chunked = chunked
        video.website = website
        downloads.add(video)
    }

    private fun getValidName(name: String?, type: String?): String {
        var name = name
        name = name?.replace("[^\\w ()'!\\[\\]\\-]".toRegex(), "")
        name = name?.trim { it <= ' ' }
        if (name?.length!! > 26) { //allowed filename length is 127
            name = name.substring(0, 26)
        } else if (name == "") {
            name = "video"
        }
        var i = 0
        var file = File(MyApplication.getMyApplicationContext()?.applicationContext?.PrepareDirectory(), "$name.$type")
        var nameBuilder = StringBuilder(name)
        while (file.exists()) {
            i++
            nameBuilder = StringBuilder(name)
            nameBuilder.append(" ").append(i)
            file = File(MyApplication.getMyApplicationContext()?.applicationContext?.PrepareDirectory(), "$nameBuilder.$type"
            )
        }
        while (nameAlreadyExists(nameBuilder.toString())) {
            i++
            nameBuilder = StringBuilder(name)
            nameBuilder.append(" ").append(i)
        }
        return nameBuilder.toString()
    }

    val list: List<videoDetail>
        get() = downloads

    val topVideo: videoDetail?
        get() = if (downloads.size > 0) {
            downloads[0]
        } else {
            null
        }

    fun deleteTopVideo(context: Context) {
        if (downloads.size > 0) {
            downloads.removeAt(0)
            save(context)
        }
    }

    private fun nameAlreadyExists(name: String): Boolean {
        for (video in downloads) {
            if (video.name.equals(name)) return true
        }
        return false
    }

    fun renameItem(index: Int, newName: String) {
        if (!downloads[index].name.equals(newName)) {
            downloads[index].name = getValidName(newName, downloads[index].type!!)
        }
    }

    companion object {
        fun load(context: Context?): DownloadQueues? {

            val file = File(context?.filesDir, "downloads.dat")
            var queues = DownloadQueues()
            if (file.exists()) {
                try {
                    val fileInputStream = FileInputStream(file)
                    val objectInputStream = ObjectInputStream(fileInputStream)
                    queues = objectInputStream.readObject() as DownloadQueues
                    objectInputStream.close()
                    fileInputStream.close()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return queues
        }
    }

    init {
        downloads = ArrayList<videoDetail>()
    }
}

