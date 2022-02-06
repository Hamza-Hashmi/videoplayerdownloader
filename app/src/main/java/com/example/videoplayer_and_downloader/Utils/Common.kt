package com.example.videoplayer_and_downloader.Utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.UI.activites.AnotherActivity
import com.example.videoplayer_and_downloader.UI.activites.AudioPlayerActivity
import com.example.videoplayer_and_downloader.UI.activites.VideoPlayer
import com.example.videoplayer_and_downloader.models.CustomFiles
import com.example.videoplayer_and_downloader.models.Folder
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo
import com.example.videoplayer_and_downloader.viewModels.VideosDataViewModel
import com.google.android.exoplayer2.MediaItem
import com.example.videoplayer_and_downloader.database.AudioHistory
import com.example.videoplayer_and_downloader.database.History
import com.example.videoplayer_and_downloader.database.MainPlaylist
import com.example.videoplayer_and_downloader.database.PlaylistItem
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Albums
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Artist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


fun Context.startNewActivity(activityName: Class<*>) {
    startActivity(Intent(this, activityName))
}

fun openAddPlaylistActivity(context: Context, media: MediaModel) {
/*    val intent = Intent(context, AddPlaylistItemActivity::class.java)
    intent.putExtra(MediaModel::class.java.simpleName, MediaModel)
    (context as Base).resultLauncher.launch(intent)*/
}


fun openAnotherrOtherActivity(context: Context, info: SelectedListInfo) {
   context.startActivity(Intent(context, AnotherActivity::class.java).apply {
        putExtra(otherActivity, info)
    })
}
val REQUIRED_PERMISSIONS_Gallery = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE)

/*
fun openSubscriptionActivity(context: Context) {
    context.startNewActivity(SubscriptionActivity::class.java)
}
 */
fun getSingleMediaAdItem(): MediaModel {
    return MediaModel(adItem, adItem, true, uri = adItem)
}

 var lastPosition = -1
 var on_attach = true

 fun setAnimation(view: View, position: Int,animation_type:Int) {
    if (position > lastPosition) {
        AnimationHelper.animate(view, if (on_attach) position else -1, animation_type)
        lastPosition = position
    }
}


fun getDummyMedia(): MediaModel {
    return MediaModel(uniqueMedia, uniqueMedia, true, uri = uniqueMedia)
}

fun getHistoryAdItem(): History {
    return History(name = adItem, realPath = adItem, isVideo = true, uri = adItem)
}
fun getAudioHistoryAdItem(): AudioHistory {
    return AudioHistory(name = adItem, realPath = adItem, isVideo = true, uri = adItem)
}


fun getMainPlaylistAdItem(): MainPlaylist {
    return MainPlaylist(name = adItem, videoPlaylist = true)
}

fun getPlaylistItemAdItem(): PlaylistItem {
    return PlaylistItem(
        name = adItem,
        realPath = adItem,
        isVideo = true,
        uri = adItem,
        isFavorite = false,
        playlistName = none,
        playlistId = 0
    )
}

fun isMiniPlayerSupported(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Settings.canDrawOverlays(context))
            true
        else {
            showToast(context, R.string.allow_overlay_permission_first)
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )
            (context as Base).resultLauncher2.launch(intent)

            true
        }
    } else {
        showToast(context, R.string.mini_player_not_supported)
        false
    }

}

 fun getRenameInterface(audioSongsDataViewModel: VideosDataViewModel): RenameDialog.IRenamed {
    return object : RenameDialog.IRenamed {
        override fun onRenamed(existingFile: CustomFiles, newFile: CustomFiles) {
            audioSongsDataViewModel.updateMedia(
                existingFile = existingFile,
                newFile = newFile
            ){

            }
        }

    }
}

 fun Context.allPermissionsGranted() = REQUIRED_PERMISSIONS_Gallery.all {

    ContextCompat.checkSelfPermission(this, it)  == PackageManager.PERMISSION_GRANTED
}


fun openPlayerActivity(
    context: Context,
    position: Int,
    info: SelectedListInfo,
    realPath: String
) {
    if (info.isVideo)
    {

        Log.e("TAG", "openPlayerActivity: video play" )
        context.startActivity(Intent(context, VideoPlayer::class.java).apply {

            my_listType=info.listType

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(selectedVideo, position)
            putExtra(playerActivity, info)
            putExtra(selectedVideoPath, realPath)
        })

    }else{

        context.startActivity(Intent(context, AudioPlayerActivity::class.java).apply {

            my_listType=info.listType


            Log.e("TAG", "openPlayerActivity: $my_listType  inflist type = ${info.listType}" )

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(selectedVideo, position)
            putExtra(selectedVideoPath, realPath)
            putExtra(callForPlayerFrom, callPlayerApp)
        })
    }

}


@ColorInt
fun resolveColorAttr(@AttrRes colorAttr: Int, context: Context): Int {
    val typedValue = TypedValue()
    val theme: Resources.Theme = context.theme
    theme.resolveAttribute(colorAttr, typedValue, true)
    return typedValue.data
}

fun getBitmapAttr(@AttrRes colorAttr: Int, context: Context): Bitmap {
    val bmp = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    canvas.drawColor(resolveColorAttr(colorAttr, context))
    return bmp
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun showToast(context: Context, messageResId: Int) {
    Toast.makeText(context, context.resources.getString(messageResId), Toast.LENGTH_LONG).show()
}

fun getUriPath(existingUri: String): Uri {
    return Uri.parse(existingUri)
}

var uri: Uri? = null

/*
fun Activity.setMyTheme(selectedTheme: String) {
    when (selectedTheme) {
        resources.getString(R.string.theme_1) -> setTheme(R.style.Theme_LightTheme)
        resources.getString(R.string.theme_2) -> setTheme(R.style.Theme_LightTheme)
    }
}
*/

/*fun shareMedia2(context: Context, media: SingleMedia) {
    try {
        val file = File(media.realPath)
        Log.e("TAG", "shareMedia: ")
        if (file.exists()) {
            val intentShareFile = Intent(Intent.ACTION_SEND)
            val fileUri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName.toString() + ".provider",
                file
            )
            intentShareFile.type = if (media.isVideo)
                "video/*"
            else
                "audio/*"

            intentShareFile.putExtra(
                Intent.EXTRA_SUBJECT,
                "Checkout this media..."
            )
            intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri)
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(intentShareFile, "Share Video"))
        } else {
            showToast(context, R.string.cannot_share_file)
        }
    } catch (e: Exception) {
        showToast(context, R.string.cannot_share_file)
    }


}*/*/*/
fun shareMedia(context: Context, media: MediaModel) {
    try {
        val file = File(media.realPath)
        Log.e("TAG", "shareMedia: ")
        Log.e("TAG", "shareMedia: ${file.absoluteFile} ")
        Log.e("TAG", "shareMedia: ${file.path} ")
        if (file.exists()) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            val screenshotUri = Uri.parse(file.path)
            sharingIntent.type = "*/*"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
            context.startActivity(Intent.createChooser(sharingIntent, "Share using"))
        } else {
            showToast(context, R.string.cannot_share_file)
        }
    } catch (e: Exception) {
        showToast(context, R.string.cannot_share_file)
    }


}

fun Activity.goToFeedback() {
    val intent = Intent(Intent.ACTION_SEND)
    val recipients = arrayOf("qurantutor28@gmail.com")
    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name) + " Feedback")
    intent.putExtra(Intent.EXTRA_TEXT, "Write your feedback here...")
    intent.type = "text/html"
    intent.setPackage("com.google.android.gm")
    startActivity(Intent.createChooser(intent, "Send mail"))
}

fun Activity.launchMarket() {
    val uri = Uri.parse("market://details?id=$packageName")
    val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        startActivity(myAppLinkToMarket)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, resources.getString(R.string.no_activity_found), Toast.LENGTH_SHORT)
            .show()
    }
}


fun getExoItem(item: MediaModel): MediaItem {
    val type = if (item.isVideo)
        "video/*"
    else
        "audio/*"

    return MediaItem.Builder()
        .setUri(getUriPath(item.uri))
        .setMimeType(type).build()
}

@Suppress("DEPRECATION")
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

fun androidVersionIs10OrAbove(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}


fun getFolderAdItem(): Folder {
    return Folder(adItem, adItem, ArrayList(), ArrayList())
}

/*
fun getDummyFolder(): Folder {
    return Folder(
        deviceItemAudioAlbum,
        deviceItemAudioAlbum,
        arrayListOf(getDummyMedia(), getDummyMedia()),
        ArrayList()
    )
}


 */
fun getAlbumAdItem(): Albums {
    return Albums(adItem, ArrayList())
}

fun getArtistAdItem(): Artist {
    return Artist(adItem, ArrayList())
}

fun <T : Any> defaultThenMain(work: suspend (() -> T?), callback: ((T?) -> Unit)) =
    CoroutineScope(Dispatchers.Main).launch {
        val data = CoroutineScope(Dispatchers.Default).async rt@{
            return@rt work()
        }.await()
        callback(data)
    }

var currentMediaItem: MediaModel? = null
const val videos = "Videos"
const val video = "Video"
const val none = "none"
var currentSelectedParent = none
const val deviceItemVideo = "deviceItemVideo"
const val deviceItemAudioFolder = "deviceItemAudioFolder"
const val deviceItemAudioAlbum = "deviceItemAudioAlbum"
const val deviceItemAudioArtist = "deviceItemAudioArtist"
const val mediaItemService = "mediaItemService"
const val IsTypesList = "IsTypesList"
const val audios = "audios"
const val audio = "audio"
const val all = "All"
 var my_listType = "my_listType"
const val history = "History"
const val favorites = "Favorites"
const val list = "list"
const val grid = "GRID"
const val uniqueMedia = "name5$223001"
const val playlist = "Playlist"
const val new = "New"
const val selectedVideo = "selectedVideo"
const val selectedVideoPath = "selectedVideoPath"
const val otherActivity = "otherActivity"
const val playerActivity = "playerActivity"
const val allVideoPlaylistId = 1
const val favoriteVideoPlaylistId = 2
const val allAudioPlaylistId = 3
const val favoriteAudioPlaylistId = 4
const val adItem = "Ad Item 1*&^5%"

const val ACTION_PREVIOUS = "actionPrevious"
const val ACTION_PLAY = "actionPlay"
const val ACTION_NEXT = "actionNext"
const val ACTION_CLOSE = "actionClose"
const val ACTION_CLOSE_ACTIVITY = "actionCloseActivity"
const val ACTION_UPDATE = "actionUpdate"
const val BROADCAST_ACTION = "BROADCAST_ACTION"
const val BROADCAST_RECEIVER = "BROADCAST_RECEIVER"
var isInterstitialAdOnScreen = false


