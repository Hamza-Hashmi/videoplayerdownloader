package com.example.videodownload.download_feature.lists

import android.os.Handler
import android.os.HandlerThread
import androidx.appcompat.app.AppCompatActivity
import com.example.videodownload.fragments.NestedScrollWebFragment
import java.util.*

class VideoDetection(var videoContentDetector : NestedScrollWebFragment.VideoContentDetector, var context : AppCompatActivity?) {

    private val reservedSearchModels: Queue<VideoSearchModel> = ArrayDeque()
    private var handler: Handler? = null

    init {
        val thread = HandlerThread("Video Detect Thread")
        thread.start()
        handler = Handler(thread.looper)
    }

    fun reserve(url: String?, page: String?, title: String?, quality: String?) {
        val videoSearch = VideoSearchModel()
        videoSearch.videoUrl = url
        videoSearch.videoPage = page
        videoSearch.videoTitle = title
        videoSearch.videoQuality = quality
        reservedSearchModels.add(videoSearch)
    }

    fun initiate() {
        try {
            while (reservedSearchModels.size != 0) {
                val search = reservedSearchModels.remove()
                videoContentDetector?.RunNewSearch(
                    search.videoUrl,
                    search.videoPage,
                    search.videoTitle,
                    search.videoQuality,
                    context
                )
                handler?.post(videoContentDetector)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun clear() {
        handler?.looper?.quit()
        val thread = HandlerThread("Video Detect Thread")
        thread.start()
        handler = Handler(thread.looper)
        reservedSearchModels.clear()
    }

    companion object{

        class VideoSearchModel {
            var videoUrl: String? = null
            var videoPage: String? = null
            var videoTitle: String? = null
            var videoQuality: String? = null
        }
    }

}