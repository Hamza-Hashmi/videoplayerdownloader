package videodownloader.fbvideodownloader.fbdownloader.network

import com.example.videoplayer_and_downloader.TrendingVideos.Network.TrendingVideosApi
import com.google.gson.GsonBuilder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val BASE_URL = "https://api.dailymotion.com"

    fun getAPI(): TrendingVideosApi {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        val gson = gsonBuilder.create()
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val retrofit =  Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(TrendingVideosApi::class.java)
    }

}