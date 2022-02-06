package com.example.videodownload.download_feature.lists

import android.content.Context
import android.util.Log
import com.example.videodownload.appUtils.Constants.Companion.LOG_TAG
import com.example.videodownload.datamodel.SavedDownloadVideos
import java.io.*
import java.util.ArrayList

class  MyCompletedVideosClass : Serializable {
    val videos: ArrayList<SavedDownloadVideos>
    fun addVideo(context: Context, name: String?, link: String?, page: String?) {
        save(context)
    }

    fun save(context: Context) {
        try {
            val file = File(context.filesDir, "completed.dat")
            val fileOutputStream = FileOutputStream(file)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(this)
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(LOG_TAG, "MyCompletedVideosClass Save: Write Exception ->" + e.message)
        }
    }

    fun updaeVideo(pos: Int, newName: String) {
        videos[pos].name = newName
    }

    companion object {
        @JvmStatic
        fun load(context: Context): MyCompletedVideosClass? {
            var completedVideos = MyCompletedVideosClass()
            val file = File(context.filesDir, "completed.dat")
            if (file.exists()) {
                try {
                    val fileInputStream = FileInputStream(file)
                    val objectInputStream = ObjectInputStream(fileInputStream)
                    completedVideos = objectInputStream.readObject() as MyCompletedVideosClass
                    objectInputStream.close()
                    fileInputStream.close()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                    Log.e(LOG_TAG, "MyCompletedVideosClass load: Read Exception ->$e")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(LOG_TAG, "MyCompletedVideosClass load: Read Exception ->$e")
                }
            }
            return completedVideos
        }
    }

    init {
        videos = ArrayList()
    }
}