package com.example.videoplayer_and_downloader.DataSource

import android.content.Context
import android.util.Log
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingResponse
import com.example.videoplayer_and_downloader.Utils.printLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import videodownloader.fbvideodownloader.fbdownloader.network.RetrofitInstance

class TrendingDataRepo(val context:Context) {

    val paramenters = arrayListOf("allow_embed", "thumbnail_720_url", "title","url","embed_url")

    val trendingResponse = ArrayList<TrendingResponse>()



    suspend fun getTrendingVideos(){


        val trendingVideos: Call<TrendingResponse> = RetrofitInstance.getAPI().getTrendingVideos(paramenters,"trending","100")

        GlobalScope.async(Dispatchers.IO){


            trendingVideos.enqueue(object : Callback<TrendingResponse> {
                override fun onResponse(
                    call: Call<TrendingResponse>,
                    response: Response<TrendingResponse>
                ) {
                    printLog("response_api  before ", response.body()?.list_data.toString())

                    response.body()?.let {
                        trendingResponse.add(it)
                        printLog("response_api_call ", it.list_data.toString())

                    }
                }

                override fun onFailure(call: Call<TrendingResponse>, t: Throwable) {
                    Log.e("TAG", "onFailure: trending failure -> "+ t.message)
                }

            })

        }.await()

    }

}