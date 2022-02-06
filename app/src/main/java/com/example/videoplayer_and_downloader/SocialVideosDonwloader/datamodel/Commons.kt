package com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.SharedHelper
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass
import com.example.videoplayer_and_downloader.Utils.MyApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Commons {

    companion object{
        val VIDEO_TITLE: String = "VIDEO_TITLE"
        val VIDEO_FILE_PATH: String = "VIDEO_FILE_PATH"
        val TRENDING_VIDEO_URL = "TRENDING_VIDEO_URL"
        val TRENDING_VIDEO_TITLE = "TRENDING_VIDEO_TITLE"
        val FOUND_VIDEO = "FOUND_VIDEO"

        val channelId= "fbvideo_downloader_channel_id"
        val channelId2 = "fbvideo_downloader_channel_id2"
        var isAllSelected = false


        fun dailymotion144pFormat(): String {
            return "_mp4_h264_aac_l2.ts"
        }

        fun dailymotion240pFormat(): String {
            return "_mp4_h264_aac_ld.ts"
        }

        fun dailymotion360pFormat(): String {
            return "_mp4_h264_aac.ts"
        }

        fun dailymotion480pFormat(): String {
            return "_mp4_h264_aac_hq.ts"
        }

        fun dailymotion720pFormat(): String {
            return "_mp4_h264_aac_hd.ts"
        }

        fun dailymotion480OtherFormate(): String {
            return "_mp4_h264_aac_hq_17.ts"
        }

        fun dailymotion360pOtherFormate(): String {
            return "_mp4_h264_aac_17.ts"
        }

        fun dailymotion240pOtherFormate(): String {
            return "_mp4_h264_aac_ld_17.ts"
        }

        fun dailymotion144pOtherFormate(): String {
            return "_mp4_h264_aac_ld_17.ts"
        }


        fun setFoundVideoDetails(context: Context, geoNameList: VideoListModel?) {
            //val mainPrefs = context.getSharedPreferences("DefaultBrowser", Context.MODE_PRIVATE)
            //val editor = mainPrefs.edit()

            Log.e("TAG", "setFoundVideoDetails: " )
            val gson = Gson()
            val element = gson.toJsonTree(
                geoNameList,
                object : TypeToken<VideoListModel?>() {}.type
            )
            if (element.isJsonObject) {
                val jsonArray = element.asJsonObject
                //editor.putString("foundDetails", jsonArray.toString()).apply()
                SharedHelper.putKey(context,FOUND_VIDEO,jsonArray.toString())

                Log.e("TAG", "setFoundVideoDetails: data " + SharedHelper.getKey(context,FOUND_VIDEO))
            }
        }

        fun getFoundDetails(context: Context?): VideoListModel? {
            Log.e("TAG", "getFoundDetails: "  )
            var playersList: VideoListModel? = null
            val list = SharedHelper.getKey(context, FOUND_VIDEO)
            Log.e("TAG", "getFoundDetails: list $list" )
            if (list != "") {
                playersList = fromJson(
                    list,
                    object : TypeToken<VideoListModel?>() {}.type
                ) as VideoListModel
            }
            return playersList
        }

        fun fromJson(jsonString: String?, type: Type?): Any? {
            return Gson().fromJson(jsonString, type)
        }

        fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (manager != null) {
                for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                    if (serviceClass.name == service.service.className) {
                        return true
                    }
                }
            }
            return false
        }

        fun hasWriteStoragePermission() =
               ActivityCompat.checkSelfPermission(MyApplication.getMyApplicationContext()?.applicationContext!!,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        fun hasReadStoragePermission() =
            ActivityCompat.checkSelfPermission(MyApplication.getMyApplicationContext()?.applicationContext!!,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        fun requestPersmissions(){
            var permissionToRequest = mutableListOf<String>()
            if (!hasWriteStoragePermission()){
                 permissionToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (!hasReadStoragePermission()){
                permissionToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (permissionToRequest.isNotEmpty()){
                ActivityCompat.requestPermissions(MyApplication.getMyApplicationContext()?.applicationContext as Activity,permissionToRequest.toTypedArray(),0)
            }
        }


        fun getStringFromResource(resId: Int): String {
            return MyApplication.getMyApplicationContext()?.applicationContext?.resources?.getString(resId)!!
        }
        fun isDailymotionDownloadUrl(video: VideoListClass.VideoInfo): Boolean {
            val dailymotionHomeVideo = MyApplication.getMyApplicationContext()?.applicationContext?.resources?.getString(
                R.string.dailymotion_url) + "/"
            return video.page?.contains(dailymotionHomeVideo, true)!!
        }

        fun getDailyMotionVideoThumbnail(video: VideoListClass.VideoInfo): String {
            val dailymotionHomeVideo = MyApplication.getMyApplicationContext()?.applicationContext?.resources?.getString(R.string.dailymotion_url) + "/"
            return video.page?.substring(
                IntRange(
                    0,
                    dailymotionHomeVideo.length - 1
                )
            ) + "thumbnail" + video.page?.substring(
                IntRange(
                    dailymotionHomeVideo.length - 1,
                    video.page!!.length - 1
                )
            )
        }

    }
}