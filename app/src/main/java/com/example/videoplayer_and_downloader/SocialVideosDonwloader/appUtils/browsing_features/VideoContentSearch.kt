package com.example.videodownload.appUtils.browsing_features

import android.content.Context
import android.util.Log
import com.example.videodownload.appUtils.getTimeStamp
import com.example.videoplayer_and_downloader.R
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset

abstract class VideoContentSearch/*(

) */ : Runnable {


    private var numLinksInspected = 0
    var context: Context? = null
    var url: String? = null
    var page: String? = null
    var title: String? = null
    var quality: String = "Unknown Quality"


    abstract fun onStartInspectingURL()
    abstract fun onFinishedInspectingURL(finishedAll: Boolean)
    abstract fun onVideoFound(
        size: String?, type: String?, link: String?, name: String?,
        page: String?, chunked: Boolean, website: String?, downloadableUrl: String?, quality: String
    )

    open fun RunNewSearch(url: String?, page: String?, title: String?, quality: String?, context: Context? ){
        this.context = context
        this.url = url
        this.page = page
        this.title = title
        this.quality = quality!!
        numLinksInspected = 0
    }

    override fun run() {
        val urlLowerCase = url!!.toLowerCase()

        val filters = context!!.resources.getStringArray(R.array.videourls)
        var urlMightBeVideo = false
        for (filter in filters) {
            if (urlLowerCase.contains(filter!!)) {
                urlMightBeVideo = true
                break
            }
        }

        if (urlMightBeVideo) {
            numLinksInspected++
            onStartInspectingURL()

            var uCon: URLConnection? = null
            try {
                uCon = URL(url).openConnection()
                uCon.connect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (uCon != null) {
                var contentType = uCon.getHeaderField("content-type")
                Log.e("LOG_TAG", "run: Content type -> " + contentType)
                if (contentType != null) {
                    contentType = contentType.toLowerCase()
                    if (contentType.contains("video") || contentType.contains("audio")) {
                        addVideoToList(uCon, page, title, contentType)
                    } else if (contentType == "application/x-mpegurl" || contentType == "application/vnd.apple.mpegurl") {
                        addVideosToListFromM3U8(uCon, page, title, contentType)
                    } else Log.e(
                        "LOG_TAG", "Not a video. Content type = " +
                                contentType
                    )
                } else {
                    Log.e("LOG_TAG", "no content type")
                }
            } else Log.e("LOG_TAG", "no connection")
            numLinksInspected--
            var finishedAll = false
            if (numLinksInspected <= 0) {
                finishedAll = true
            }
            onFinishedInspectingURL(finishedAll)
        }
    }


    fun addVideoToList(
        uCon: URLConnection,
        page: String?,
        title: String?,
        contentType: String
    ) {
        try {
            var size = uCon.getHeaderField("content-length")
            var link = uCon.getHeaderField("Location")
            if (link == null) {
                link = uCon.url.toString()
            }
            val host = URL(page).host
            var website: String? = null
            var chunked = false

            /*Log.e(TAG, "addVideoToList: Host value is -> " + host)
            Log.e(TAG, "addVideoToList: Content type -> " + contentType)
            Log.e(TAG, "addVideoToList: Title -> " + title)
            Log.e(TAG, "addVideoToList: Page -> " + page)*/


            // Skip twitter video chunks.
            if (host.contains("twitter.com") && contentType == "video/mp2t") {
                return
            }
            var name = "video"
            if (title != null) {
                if (contentType.contains("audio")) {
                    name = "[AUDIO ONLY]$title"
                } else {
                    name = title
                }
            } else if (contentType.contains("audio")) {
                name = "audio"
            }
            if (host.contains("youtube.com") || URL(link).host.contains("googlevideo.com")) {
                //link  = link.replaceAll("(range=)+(.*)+(&)",
                // "");
                val r = link.lastIndexOf("&range")
                if (r > 0) {
                    link = link.substring(0, r)
                }
                val ytCon: URLConnection
                ytCon = URL(link).openConnection()
                ytCon.connect()
                size = ytCon.getHeaderField("content-length")
                if (host.contains("youtube.com")) {
                    val embededURL = URL(
                        "http://www.youtube.com/oembed?url=" + page +
                                "&format=json"
                    )
                    try {
                        //name = new JSONObject(IOUtils.toString(embededURL)).getString("title");
                        val jSonString: String
                        val inputputStream = embededURL.openStream()
                        val inReader = InputStreamReader(
                            inputputStream, Charset
                                .defaultCharset()
                        )
                        val sb = StringBuilder()
                        val buffer = CharArray(1024)
                        var read: Int
                        while ((inReader.read(buffer).also { read = it }) != -1) {
                            sb.append(buffer, 0, read)
                        }
                        jSonString = sb.toString()
                        name = JSONObject(jSonString).getString("title")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("LOG_TAG", "addVideoToList: Error in parsing youtube json -> " + e.message)
                    }
                    if (contentType.contains("video")) {
                        name = "[VIDEO ONLY]$name"
                    } else if (contentType.contains("audio")) {
                        name = "[AUDIO ONLY]$name"
                    }
                }
            } else if (host.contains("dailymotion.com")) {
                chunked = true
                website = "dailymotion.com"
                link = link.replace("(frag\\()+(\\d+)+(\\))".toRegex(), "FRAGMENT")
                Log.e("LOG_TAG", "addVideoToList: Dailymotion link ->" + link)
            } else if (host.contains("vimeo.com") && link.endsWith("m4s")) {
                chunked = true
                website = "vimeo.com"
                link = link.replace("(segment-)+(\\d+)".toRegex(), "SEGMENT")
                size = null
                Log.e("LOG_TAG", "addVideoToList: Vimeo link ->" + link)

            } else if (host.contains("facebook.com") && link.contains("bytestart")) {
                name = link.substring(50, 94)
                val b = link.lastIndexOf("&bytestart")
                val f = link.indexOf("fbcdn")
                if (b > 0) {
                    link = "https://video.xx." + link.substring(f, b)
                }
                val fbCon: URLConnection
                fbCon = URL(link).openConnection()
                fbCon.connect()
                size = fbCon.getHeaderField("content-length")
                website = "facebook.com"

                Log.e("LOG_TAG", "addVideoToList: Facebook link ->" + link)
                Log.e("LOG_TAG", "addVideoToList: Facebook size  ->" + size)
            } else if (host.contains("facebook.com")) {
                name = ""+ context?.getTimeStamp()
                //if (link.length >= 94) "Facebook" + link.substring(50, 94) else "Facebook$link"
            } else if (host.contains("instagram.com")) {
                if (name.equals("Instagram", ignoreCase = true)) {
                    name = "Instagram"+  context?.getTimeStamp()
                    /*if (link.length >= 94) "Instagram" + link.substring(
                    50,
                    94
                ) else "Instagram$link"*/
                }
            }
            val type: String
            when (contentType) {
                "video/mp4" -> type = "mp4"
                "video/webm" -> type = "webm"
                "video/mp2t" -> type = "ts"
                "audio/webm" -> type = "webm"
                else -> type = "mp4"
            }
            onVideoFound(size, type, link, name, page, chunked, website, url, quality)
            val videoFound = ("name:" + name + "\n" +
                    "link:" + link + "\n" +
                    "type:" + contentType + "\n" +
                    "size:" + size)
            Log.e("LOG_TAG", "Found Video is -> " + videoFound)
        } catch (e: IOException) {
            Log.e(
                "loremarTest", "Exception in adding video to " +
                        "list"
            )
        }
    }

    fun addVideosToListFromM3U8(
        uCon: URLConnection,
        page: String?,
        title: String?,
        contentType: String
    ) {

        try {
            var host: String
            host = URL(page).host
            if (host.contains("twitter.com") ||
                host.contains("metacafe.com") ||
                host.contains("myspace.com")) {
                val inputputStream = uCon.getInputStream()
                val inReader = InputStreamReader(inputputStream)
                var buffReader = BufferedReader(inReader)

                var line = ""
                var prefix: String? = null
                var type: String? = null
                var name = "video"
                var website: String? = null
                if (title != null) {
                    name = title
                }
                if (host.contains("twitter.com")) {
                    prefix = "https://video.twimg.com"
                    type = "ts"
                    website = "twitter.com"
                } else if (host.contains("metacafe.com")) {
                    val link = uCon.url.toString()
                    prefix = link.substring(0, link.lastIndexOf("/") + 1)
                    website = "metacafe.com"
                    type = "mp4"
                } else if (host.contains("myspace.com")) {
                    val link = uCon.url.toString()
                    website = "myspace.com"
                    type = "ts"
                    onVideoFound(null, type, link, name, page, true, website, url, quality)
                    val videoFound = """
                    name:$name
                    link:$link
                    type:$contentType
                    size: null
                    """.trimIndent()
                    Log.e("LOG_TAG", videoFound)
                    return
                }

                while (true) {
                    line = buffReader.readLine() ?: break
                    Log.e("LOG_TAG", "addVideosToListFromM3U8: buffer line -> " + line)
                    if (line.endsWith(".m3u8")) {

                        val link = prefix + line
                        onVideoFound(null, type, link, name, page, true, website, url, quality)
                        val videoFound = """
                    name:$name
                    link:$link
                    type:$contentType
                    page:$page
                    website:$website
                    size: null
                    """.trimIndent()
                        Log.e("LOG_TAG", "twitter video found ->" + videoFound)
                    }
                }

                /*while (buffReader.readLine() != null && !buffReader.readLine().isEmpty()) {
                    line = buffReader.readLine()

                    if (line.endsWith(".m3u8")) {

                        val link = prefix + line
                        onVideoFound(null, type, link, name, page, true, website)
                        val videoFound = """
                        name:$name
                        link:$link
                        type:$contentType
                        page:$page
                        website:$website
                        size: null
                        """.trimIndent()
                        Log.e(TAG, "twitter video found ->" + videoFound)
                    }
                }*/


            } else {
                Log.e(
                    "LOG_TAG", "Content type is " + contentType + " but site is not " +
                            "supported"
                )
            }
        } catch (e: IOException)
        {
            e.printStackTrace()
        }
    }

}