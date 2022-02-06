package com.example.videodownload.fragments

import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.NestedScrollWebDashboardActivity
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.DownloadVideosDashboardActivity
import com.example.videoplayer_and_downloader.Utils.MyApplication

open class BaseFargment : Fragment()  , ClipboardManager.OnPrimaryClipChangedListener {


    fun getMainActivity(): AppCompatActivity? {
        return if(activity != null){
            if (activity is DownloadVideosDashboardActivity){
                activity as DownloadVideosDashboardActivity?
            }else{
                  activity as NestedScrollWebDashboardActivity?
            }

        }else{
            null
        }
    }

    fun baseAppClass(): MyApplication? {
        return if(activity != null){
            activity?.application as MyApplication
        }else{
            null
        }
    }
    override fun onPrimaryClipChanged() {
    }

}