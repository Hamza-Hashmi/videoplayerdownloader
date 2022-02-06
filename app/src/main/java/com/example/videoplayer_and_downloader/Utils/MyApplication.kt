package com.example.videoplayer_and_downloader.Utils

import android.app.Application
import android.content.Intent
import com.example.videoplayer_and_downloader.DI.AppModule
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils.MyDownloadManagerClass
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication: Application() {
    companion object{
        val CHANNEL_ID_1 = "channed1"
        val CHANNEL_ID_2 = "channe2"
        val ACTION_PLALY = "actionplay"
        val ACTION_NEXT = "actionnext"
        val ACTION_PREV = "actionprevious"
        val ACTION_STOP = "actionstop"
        @JvmField
        var instance: MyApplication? = null

        @JvmStatic
        fun getMyApplicationContext(): MyApplication? {
            return instance
        }
    }


    fun getDownloadService(): Intent? {
        return downloadService
    }
    private var downloadService: Intent? = null

    override fun onCreate() {
        super.onCreate()

        instance = this
        downloadService = Intent(applicationContext, MyDownloadManagerClass::class.java)

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            modules(AppModule.getModule)

        }
    }


}
