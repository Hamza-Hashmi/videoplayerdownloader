package com.example.videoplayer_and_downloader.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.videoplayer_and_downloader.Utils.Constants.Companion.NEXT_ACTION
import com.example.videoplayer_and_downloader.Utils.Constants.Companion.PLAY_ACTION
import com.example.videoplayer_and_downloader.Utils.Constants.Companion.PREVIOUS_ACTION
import com.example.videoplayer_and_downloader.Utils.Constants.Companion.STOP_ACTION

class NotificationReciver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val actionName = intent.action
        val serviceIntent = Intent(context, AudioPlayerService::class.java)
        if (actionName != null) {
            when (actionName) {
                PLAY_ACTION -> {
                    serviceIntent.putExtra("ActionName", "playPause")
                    context.startService(serviceIntent)
                }
                NEXT_ACTION -> {
                    serviceIntent.putExtra("ActionName", "next")
                    context.startService(serviceIntent)
                }
                PREVIOUS_ACTION -> {
                    serviceIntent.putExtra("ActionName", "actionprevious")
                    context.startService(serviceIntent)
                }
                STOP_ACTION -> {
                    serviceIntent.putExtra("ActionName", "actionstop")
                    context.startService(serviceIntent)
                }
            }
        }
    }
}
