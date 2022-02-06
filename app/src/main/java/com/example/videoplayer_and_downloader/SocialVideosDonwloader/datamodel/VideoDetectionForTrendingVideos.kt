package com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel

import android.app.Activity
import android.os.Handler
import android.os.HandlerThread
import com.example.videoplayer_and_downloader.TrendingVideos.TrendingVideoPlayer
import java.util.*

class VideoDetectionForTrendingVideos(var videoContentSearch : TrendingVideoPlayer.singleVideoContentSearchTrending, var context : Activity?) {

    private val reservedSearches: Queue<VideoSearch> = ArrayDeque()
    private var handler: Handler? = null

    init {
        val thread = HandlerThread("Video Detect Thread")
        thread.start()
        handler = Handler(thread.looper)
    }



    fun reserve(url: String?, page: String?, title: String?, quality: String?) {
        val videoSearch = VideoSearch()
        videoSearch.url = url
        videoSearch.page = page
        videoSearch.title = title
        videoSearch.quality = quality
        reservedSearches.add(videoSearch)
    }

    fun clear() {
        handler!!.looper.quit()
        val thread = HandlerThread("Video Detect Thread")
        thread.start()
        handler = Handler(thread.looper)
        reservedSearches.clear()
    }


    fun initiate() {
        try {
            while (reservedSearches.size != 0) {
                val search = reservedSearches.remove()
                videoContentSearch?.RunNewSearch(
                    search.url,
                    search.page,
                    search.title,
                    search.quality,
                    context
                )
                handler!!.post(videoContentSearch)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
    companion object{

        class VideoSearch {
            var url: String? = null
            var page: String? = null
            var title: String? = null
            var quality: String? = null
        }
    }

}