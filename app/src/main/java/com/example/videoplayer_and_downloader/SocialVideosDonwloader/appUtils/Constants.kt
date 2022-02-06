package com.example.videodownload.appUtils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import com.example.videoplayer_and_downloader.R

class Constants {

    companion object{

        val LOG_TAG = "AjDownloaderApp_TAG"
        val KEY_VIDEO_DATA_BUNDLE = "video_data_bundle"
        val KEY_URL = "KEY_URL"
        val KEY_SITE_NAME = "KEY_SITE_NAME"
        val channelId= "video_downloader_channel_id"
        val channelId2 = "video_downloader_channel_id2"
        val videoFOUND ="videoFOUND"
        val videoFilePath ="videoFilePath"
        val videoTitle = "videoTitle"
        val KEY_SORT_POS = "KEY_SORT_POS"
        val WA_VIDEO_FILEPATH ="WA_VIDEO_FILEPATH"
        val WA_VIDEO_TITLE ="WA_VIDEO_TITLE"

        val isMobileDataEnabled ="isMobileDataEnabled"
        val defaultSearchEngine ="defaultSearchEngine"
        var defSearchEngineValue = 0
        var isDetectionOn = "isDetectionOn"

        var customDialog: Dialog? = null

        fun showProgressDialog(activity: Activity?) {

            println("Show")
            if (customDialog != null) {
                customDialog?.dismiss()
                customDialog = null
            }
            activity?.let {
                customDialog = Dialog(it)
                val inflater = LayoutInflater.from(it)
                val mView: View = inflater.inflate(R.layout.twitter_progress_dialog, null)
                customDialog?.setCancelable(false)
                customDialog?.setContentView(mView)
                if (!customDialog?.isShowing!! && !activity.isFinishing) {
                    val back = ColorDrawable(Color.TRANSPARENT)
                    val inset = InsetDrawable(back, 0)
                    customDialog?.window!!.setBackgroundDrawable(inset)
                    customDialog?.show()
                }
            }
        }

        fun hideProgressDialog(activity: Activity?) {
            println("Hide")
            if (customDialog != null && customDialog?.isShowing!!) {
                customDialog?.dismiss()
            }
        }
    }


}