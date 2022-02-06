package com.example.videodownload.appUtils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.TinyDB
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel.VideoListModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class Utils {


    companion object {

        fun dailymotion144pFormat(): String {
            return "_mp4_h264_aac_l2.ts"
        }

        fun dailymotion240pFormat(): String {
            return "_mp4_h264_aac_ld.ts"
        }

        fun dailymotion360pFormat(): String {
            return "_mp4_h264_aac.ts"
        }

        fun dailymotion480pFormat(): String {
            return "_mp4_h264_aac_hq.ts"
        }

        fun dailymotion720pFormat(): String {
            return "_mp4_h264_aac_hd.ts"
        }

        fun dailymotion480OtherFormate(): String {
            return "_mp4_h264_aac_hq_17.ts"
        }

        fun dailymotion360pOtherFormate(): String {
            return "_mp4_h264_aac_17.ts"
        }

        fun dailymotion240pOtherFormate(): String {
            return "_mp4_h264_aac_ld_17.ts"
        }

        fun dailymotion144pOtherFormate(): String {
            return "_mp4_h264_aac_ld_17.ts"
        }


        /*fun sendFirebaseEvent(firebaseAnalytics: FirebaseAnalytics, eventName: String?, eventDescription: String?) {
            if (eventName!=null && eventDescription!=null) {
                val bundle = Bundle()
                bundle.putString(eventName, eventDescription)
                firebaseAnalytics.logEvent(eventName, bundle)
            }
        }*/

        public fun setFoundDetails(context: Context, geoNameList: VideoListModel?) {
            //val mainPrefs = context.getSharedPreferences("DefaultBrowser", Context.MODE_PRIVATE)
            //val editor = mainPrefs.edit()
            val gson = Gson()
            val element = gson.toJsonTree(
                geoNameList,
                object : TypeToken<VideoListModel?>() {}.type
            )
            if (element.isJsonObject) {
                val jsonArray = element.asJsonObject
                //editor.putString("foundDetails", jsonArray.toString()).apply()
                TinyDB.getInstance(context).putString(Constants.videoFOUND, jsonArray.toString())
            }
        }

        fun getFoundDetails(context: Context?): VideoListModel? {
            var playersList: VideoListModel? = null
            //val mainPrefs = context.getSharedPreferences("DefaultBrowser", Context.MODE_PRIVATE)
            //val list = mainPrefs.getString("foundDetails", "")
            val list = TinyDB.getInstance(context).getString(Constants.videoFOUND)
            if (list != "") {
                playersList = fromJson(
                    list,
                    object : TypeToken<VideoListModel?>() {}.type
                ) as VideoListModel
            }
            return playersList
        }


        fun fromJson(jsonString: String?, type: Type?): Any? {
            return Gson().fromJson(jsonString, type)
        }

        fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (manager != null) {
                for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                    if (serviceClass.name == service.service.className) {
                        return true
                    }
                }
            }
            return false
        }

        fun tiktokUrl(context: Activity?): String? {
            return context?.getString(R.string.tiktokurl)
        }


        fun vimeoUrl(context: Activity?): CharSequence? {
            return context?.getString(R.string.vimeourlparent)
        }

        fun facebookUrl(context: Activity?): String? {
            return context?.getString(R.string.facebook_url)
        }

        fun instagramUrl(context: Activity?): String? {
            return context?.getString(R.string.instagram_url)
        }

        fun likeeUrl(context: Activity?): String? {
            return context?.getString(R.string.likeeUrl2)
        }


        fun disableSSLCertificateChecking() {

            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return null!!
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String) {
                    // Not implemented
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String) {
                    // Not implemented
                }
            })
            try {
                val sc = SSLContext.getInstance("TLS")
                sc.init(null, trustAllCerts, SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

                // Create all-trusting host name verifier
                val allHostsValid =
                    HostnameVerifier { hostname, session -> true }

                // Install the all-trusting host verifier
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }
    }

}