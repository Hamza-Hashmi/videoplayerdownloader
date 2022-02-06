package com.example.videoplayer_and_downloader.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer_and_downloader.DataSource.TrendingDataRepo
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import videodownloader.fbvideodownloader.fbdownloader.network.RetrofitInstance

class TrendingsViewModel(val repo: TrendingDataRepo):ViewModel() {
    val trendingResponse = MutableLiveData<TrendingResponse>()
    val parameters = arrayListOf("allow_embed", "thumbnail_720_url", "title","url","embed_url")

    fun getTrendingVideos(){
        viewModelScope.launch {

            val trendingVideos: Call<TrendingResponse> = RetrofitInstance.getAPI().getTrendingVideos(parameters,"trending","100")

            trendingVideos.enqueue(object : Callback<TrendingResponse> {
                override fun onResponse(
                    call: Call<TrendingResponse>,
                    response: Response<TrendingResponse>
                ) {
                    trendingResponse.value = response.body()
                }

                override fun onFailure(call: Call<TrendingResponse>, t: Throwable) {
                    Log.e("TAG", "onFailure: trending failure -> "+ t.message)
                }

            })

        }
    }
}