package com.example.videoplayer_and_downloader.Utils

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider

import java.io.File

class Constants(private val context: Context) {


private val myTag="DataApp"

    companion object {

        val PLAY_ACTION = "actionplay"
        val NEXT_ACTION = "actionnext"
        val PREVIOUS_ACTION = "actionprevious"
        val STOP_ACTION = "actionstop"

        const val allSongsList = "allSongsList"
        const val albumSongsList = "albumSongsList"
        const val artistSongsList = "artistSongsList"

        const val handlePlayerSatate ="handlePlayerState"
        const val handlePlayPauseAction ="handlePlayPauseAction"
        const val handleMessageState ="handleMessageState"
        const val songCommonPosition="SongPosition"
        const val handlePlayPauseSong ="handleplayPauseSong"
        const val playNextSong ="PlaynextSong"
        const val playPerviousSong ="PlayPrevousSong"


        const val shuffleKey = "shuffle_key"
        const val rapeatKey = "rapeat_key"

        var showAdSplash = false
        const val billingKey =
            ""
        const val KEY_IS_PURCHASED = "KEY_IS_PURCHASED"
        const val testPurchaseId = "android.test.purchased"

        const val INTERSTITIAL = "interstitialAd"
        const val NATIVE = "nativeAd"
        const val israting = "israting"

        const val isPremiumUserKey = "isPremiumUserKey"
        const val appOpenCounterKey = "appOpenCounterKey"
        const val MY_NOTIFICATIONS = "MyNotifications"
        const val isPremiumScreenShow = "isPremiumScreenShow"
        const val isShowSplash = "isShowSplash"

        const val showAdTTS = 15
        const val preferenceMode = 0
        const val preferenceName = "MyVideoPlayer"
        const val uniqueMedia = "name5$223001"

        const val notificationTitle = "notificationTitle"
        const val notificationImage = "notificationImage"
        const val notification_Id = "notification_Id"
        const val notificationDetails = "notificationDetails"
        const val showNotification = "showNotification"
        const val isNotificationCome = "isNotificationCome"

        const val Recents = "Recents"
        const val Folders = "Folders"
        const val Playlist = "Playlist"
        const val Phrases = "Phrases"

        const val SUBSCRIBE_MONTHLY_PACKAGE: String = ""
        const val SUBSCRIBE_ANNUAL_PACKAGE: String = ""
        val SUBS_SKUS = listOf(
            SUBSCRIBE_MONTHLY_PACKAGE,
            SUBSCRIBE_ANNUAL_PACKAGE
        )

        val NON_CONSUMABLE_SKUS = SUBS_SKUS












       /* fun shareText(text: String?, context: Context, type: SendingType?) {
            try {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, text)
                sendIntent.type = "text/plain"
                when (type) {
                    SendingType.FACEBOOK ->
                        sendIntent.setPackage("com.facebook.orca")
                    SendingType.INSTAGRAM ->
                        sendIntent.setPackage("com.instagram.android")
                    SendingType.WHATSAPP ->
                        sendIntent.setPackage("com.whatsapp")
                    else -> {
                    }
                }

                context.startActivity(Intent.createChooser(sendIntent, "Share text to.."))
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }

        fun copyText(text: String?, context: Activity) {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
//            toast("Text copied ")
        }


    }




    fun Activity.shareAudioFile(audioFile: String) {
        val file = File(audioFile)
        val uri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".fileprovider",
            file
        )
        val shareIntent: Intent = ShareCompat.IntentBuilder.from(this)
            .setType("audio/mp3")
            .setStream(uri)
            .intent
        startActivity(Intent.createChooser(shareIntent, "Share Sound File"))
    }



*/

}
}