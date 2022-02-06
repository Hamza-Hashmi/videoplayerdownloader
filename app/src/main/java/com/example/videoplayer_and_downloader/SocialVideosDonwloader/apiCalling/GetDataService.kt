package com.example.videodownload.apiCalling


import com.example.videodownload.twiter.TwitterResponse
import retrofit2.Call
import retrofit2.http.*

interface GetDataService {

    @FormUrlEncoded
    @POST
    fun callTwitterSimple(@Url Url: String?, @Field("id") id: String?): Call<TwitterResponse>


}