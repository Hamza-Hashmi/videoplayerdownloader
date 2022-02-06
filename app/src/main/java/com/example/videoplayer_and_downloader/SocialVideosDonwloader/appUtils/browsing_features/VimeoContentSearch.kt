package com.example.videodownload.appUtils.browsing_features

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class VimeoContentSearch(context1: Context, mVimeoresult: mVimeoResult) {

    val context = context1
    private val iVimeoResult = mVimeoresult
    fun searchVimeoVideo(vimeoId: String, mainUrl: String) {
        val url = "https://player.vimeo.com/video/$vimeoId/config"

        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    Log.e("Download_vimeoidflow5", response)


                    val mainResponse = JSONObject(response)
                    val request = getJsonObjectIfExists(mainResponse, "request")
                    request?.let { it2 ->
                        val files = getJsonObjectIfExists(it2, "files")
                        files?.let { it1 ->
                            val progressiveArray = getJsonArrayIfExists(it1, "progressive")
                            progressiveArray?.let { it4 ->
                                for (m in 0 until progressiveArray.length()) {
                                    val videoObject = getJsonObjectIfExists(it4, m)
                                    val downloadUrl = getStringIfExists(videoObject!!, "url")
                                    val quality = getStringIfExists(videoObject, "quality")
                                    var title = "Vimeo Watch"
                                    val videoDetailObject = getJsonObjectIfExists(mainResponse, "video")
                                    videoDetailObject?.let {
                                        val videoTitle = getStringIfExists(it, "title")
                                        if (videoTitle.isNotEmpty()) {
                                            title = videoTitle
                                        }
                                    } ?: stopHere("5")
                                    Log.d("itemadded541", quality)
                                    iVimeoResult.addVimeoItem(downloadUrl, mainUrl, title, quality)
                                }

                            } ?: stopHere("3")
                        } ?: stopHere("2")
                    } ?: stopHere("1")
                },
                {
                    Log.e("Download_vimeoidflow6", it.toString())

                })
        queue.add(stringRequest)
    }

    private fun stopHere(i: String) {
        Log.e("Download_vimeoidflow8", "stop$i")
    }

    private fun getStringIfExists(jsonObject: JSONObject, key: String): String {
        return if (jsonObject.has(key))
            jsonObject.getString(key)
        else
            ""
    }

    private fun getJsonObjectIfExists(jsonObject: JSONObject, key: String): JSONObject? {
        return try {
            jsonObject.getJSONObject(key)
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("SameParameterValue")
    private fun getJsonArrayIfExists(jsonObject: JSONObject, key: String): JSONArray? {
        return try {
            jsonObject.getJSONArray(key)
        } catch (e: Exception) {
            null
        }
    }

    private fun getJsonObjectIfExists(jsonArray: JSONArray, index: Int): JSONObject? {
        return try {
            jsonArray.getJSONObject(index)
        } catch (e: Exception) {
            null
        }
    }

    interface mVimeoResult {
        fun addVimeoItem(url: String, page: String, title: String, quality: String)
    }


}