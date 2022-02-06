package com.example.videoplayer_and_downloader.Utils
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass
import com.example.videoplayer_and_downloader.Utils.Constants.Companion.KEY_IS_PURCHASED
import com.example.videoplayer_and_downloader.Utils.Constants.Companion.isNotificationCome
import com.example.videoplayer_and_downloader.adapters.TracksAdapter
import com.example.videoplayer_and_downloader.models.MusicModel
import com.google.android.material.snackbar.Snackbar
import smartobject.videoplayer.supervideoplayer.hdvideoplayer.videoeditor.adaptters.DetailsAudioAdapter
import java.io.File
import java.io.IOException


val DURATION_IN_LEFT_RIGHT: Long = 150
val DURATION_IN_RIGHT_LEFT: Long = 1000
val DELLAY_ANIM: Long = 50
var shuffle = false
var repeat = false
var audioListType = ""

var isHideAd = false

var isNotificationIsComming=false
const val callForPlayerFrom = "callPlayerFrom"
const val callPlayerApp = "callPlayerApp"
const val songSelectFromList = "songSelectFromList"
const val selectedFolderName = "selectedFolderName"
var mySelectedSongPositionn: Int = 0
const val callForPlayerService = "callForPlayerService"
const val callForPlayerMusicHome = "callPlayerMusicHome"
const val playingSongPosition = "playingSongPosition"
val NOTIFICATION_CHANNEL_ID = "VideoPlayerAndDownloader123"
val NOTIFICATION_CHANNEL_NAME = "VideoPlayerAndDownloader"
var itemClickCount = 1
var listToPlay:ArrayList<MediaModel> = ArrayList()

fun Activity.isDailymotionDownloadUrl(video: VideoListClass.VideoInfo): Boolean {
    val dailymotionHomeVideo = getString(R.string.dailymotion_url) + "/"
    return video.page?.contains(dailymotionHomeVideo, true)!!
}

fun Context.navigateToNextScreen(myclass: Class<*>?) {
    Intent(applicationContext, myclass).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun Activity.isMyServiceRunning(serviceClass: Class<*>): Boolean {
    val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.getClassName()) {
            return true
        }
    }
    return false
}


fun Context.isComeNewNotification(): Boolean {
    val tinyDB =
        SharedPreferenceData(
            this
        )
    return tinyDB.getBoolean(isNotificationCome,false)

}

fun Activity.navigateToFragmentsController(navController: NavController, @IdRes resId: Int) {

    if (resId!= navController.currentDestination?.id) {
        navController.navigate(resId)
    }

}

fun getFcmToken() {


}

fun Context.setIsNewNotification(boolean: Boolean){
    val tinyDB =
        SharedPreferenceData(
            this
        )
    tinyDB.putBoolean(isNotificationCome,boolean)
}


fun Context.deleteFileFromStorage(uri: Uri, onComplete: ((String) -> Unit)) {

    val path: String = MyFilePaths.getPath(this, uri)
    Log.d("deletedUri3", path)
    val deleteDialog = DeleteDialog(
        context = this,
        newUri = uri
    ) {
        Log.d("deletedUri4", path)
        onComplete?.invoke(path)
    }
    deleteDialog.show()

}

fun Context.deleteFileFromStorage(singleMedia: MediaModel, onComplete: ((MediaModel) -> Unit)) {

    val deleteDialog = DeleteDialog(
        context = this,
        existingFile = singleMedia
    ) { uri ->
        uri?.let { uMedia ->
            onComplete?.invoke(uMedia)
        }
    }
    deleteDialog.show()

}


fun Context.getStringFile(idres: Int, quality: String, fileSize: String): String {
    return resources.getString(idres, quality, fileSize)
}

fun Activity.switchActivity(java: Class<*>?) {
    val intent = Intent(this, java)
    startActivity(intent)
}
fun Activity.switchActivityWithFinish(java: Class<*>?) {

    val intent = Intent(this, java)
    startActivity(intent)
    finish()
}

fun Context.rateUs() {

    Log.e("TAG", "rateUs: package ${this.packageName}")
    val intent = Intent(
        Intent.ACTION_VIEW, Uri.parse(
            "https://play.google.com/store/apps/details?id=" +
                    applicationContext.packageName
        )
    )
    startActivity(intent)
}

fun ImageView.loadImageThroughBitmap(bitmap: Bitmap? = null) {
    val circularProgressDrawable = CircularProgressDrawable(this.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    if (bitmap == null)
        Glide.with(this).load("").placeholder(circularProgressDrawable).into(this)
    else {
        try {
            Glide.with(this).load(bitmap).placeholder(circularProgressDrawable).into(this)
        } catch (e: Exception) {
        }
    }
}

fun Context.shareApp(appName:String) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT,appName )
    var shareMessage = "\nLet me recommend you this application\n\n"
    shareMessage = """
            ${shareMessage}https://play.google.com/store/apps/details?id=${applicationContext.packageName}
            """.trimIndent()
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
    startActivity(Intent.createChooser(shareIntent, "choose one"))
}

fun Context.gotoPrivacy(link: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    startActivity(browserIntent)
}


fun Context.sentEmail() {
    val addresses = arrayOf("ontubeapp@gmail.com")
    val subject =getString(R.string.app_name)
    val body =
        "Kindly share your feedback here, Because your feedback is valueable for us to improve our Application for you."
    try {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        emailIntent.type = "plain/text"
        //            emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        emailIntent.setPackage("com.google.android.gm")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(emailIntent)
    } catch (e: Exception) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:" + addresses[0])
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }
}

fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}


fun Activity.hideStatusBar() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
    window.statusBarColor = Color.TRANSPARENT
}


fun Activity.setWindowFlag(bits: Int, on: Boolean) {
    val win = window
    val winParams = win.attributes
    if (on) {
        winParams.flags = winParams.flags or bits
    } else {
        winParams.flags = winParams.flags and bits.inv()
    }
    win.attributes = winParams
}


fun Activity.onCopyData(copytext: String) {
    Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    val clipboard: ClipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied text", copytext)
    clipboard.setPrimaryClip(clip)
}

fun Activity.onShareData(data: String) {

    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, data)
    startActivity(Intent.createChooser(shareIntent, "Share via"))
}


/*
fun openSubscriptionActivity(context: Context) {
    context.startNewActivity(SubscriptionActivity::class.java)
}
 */


val ad_periority = 2
val count_after = 4


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.isAlreadyPurchased(): Boolean {
    val tinyDB = SharedPreferenceData(this)
    return tinyDB.getBoolean(KEY_IS_PURCHASED)

}

fun View.slideVisibility(visibility: Boolean, durationTime: Long = 300) {
    val transition = Slide(Gravity.LEFT)
    transition.apply {
        duration = durationTime
        addTarget(this@slideVisibility)
    }
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.isVisible = visibility
}

fun View.slideVisibilityRIGHT(visibility: Boolean, durationTime: Long = 300) {
    val transition = Slide(Gravity.RIGHT)
    transition.apply {
        duration = durationTime
        addTarget(this@slideVisibilityRIGHT)
    }
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.isVisible = visibility
}

fun Context.isInternetConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true

    return true
}

fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}


fun animateRightLeft(view: View) {
    view.translationX = view.x + 300
    view.alpha = 0f
    val animatorSet = AnimatorSet()
    val animatorTranslateY = ObjectAnimator.ofFloat(view, "translationX", view.x + 300, 0f)
    val animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f)
    ObjectAnimator.ofFloat(view, "alpha", 0f).start()
    animatorTranslateY.setStartDelay(DELLAY_ANIM)
    animatorTranslateY.duration = DURATION_IN_RIGHT_LEFT
    animatorSet.playTogether(animatorTranslateY, animatorAlpha)
    animatorSet.start()
}


fun animateLeftRight(view: View, position: Int) {
    var position = position
    val not_first_item = position == -1
    position = position + 1
    view.translationX = -400f
    view.alpha = 0f
    val animatorSet = AnimatorSet()
    val animatorTranslateY = ObjectAnimator.ofFloat(view, "translationX", -400f, 0f)
    val animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f)
    ObjectAnimator.ofFloat(view, "alpha", 0f).start()
    animatorTranslateY.setStartDelay(DELLAY_ANIM)
    animatorTranslateY.duration = 2 * DURATION_IN_LEFT_RIGHT
    animatorSet.playTogether(animatorTranslateY, animatorAlpha)
    animatorSet.start()
}


fun Context.isNativeAdsEnable(): Boolean {

    return true
}


fun printLog(TAG: String, message: String) {

    Log.e(TAG, message)

}

fun shareFile(filePath: String, context: Context?) {
    val f: File = File(filePath)
    val uri =
        FileProvider.getUriForFile(context!!, context.getPackageName() + ".provider", f)
    val share = Intent(Intent.ACTION_SEND)
    share.putExtra(Intent.EXTRA_STREAM, uri)
    share.type = "audio/*"
    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(Intent.createChooser(share, "Share audio File"))
}

fun Context.deleteAudioFromDeviceDetails(
    dirPath: String?,
    position: Int,
    songList: ArrayList<MusicModel>,
    adaptor: DetailsAudioAdapter
) {
    //String root = Environment.getExternalStorageDirectory().toString();
    val file = File(dirPath)
    if (file.delete()) {
        songList.removeAt(position)
        adaptor.notifyItemRemoved(position)
        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, "Deleted Failed", Toast.LENGTH_SHORT).show()
    }
    if (file.exists()) {
        try {
            file.canonicalFile.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (file.exists()) {
            this.getApplicationContext().deleteFile(file.name)
        }
    }
    adaptor.notifyDataSetChanged()
}
fun Context.deleteAudioFromDevice(
    dirPath: String?,
    position: Int,
    songList: ArrayList<MusicModel>,
    adaptor: TracksAdapter
) {
    //String root = Environment.getExternalStorageDirectory().toString();
    val file = File(dirPath)
    if (file.delete()) {
        songList.removeAt(position)
        adaptor.notifyItemRemoved(position)
        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, "Deleted Failed", Toast.LENGTH_SHORT).show()
    }
    if (file.exists()) {
        try {
            file.canonicalFile.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (file.exists()) {
            this.getApplicationContext().deleteFile(file.name)
        }
    }
    adaptor.notifyDataSetChanged()
}

fun Context.shareVideoLink(link:String){
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    //shareIntent.putExtra(Intent.EXTRA_SUBJECT,appName )
    var shareMessage = link
    shareMessage = """
            ${shareMessage}
            """.trimIndent()
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
    startActivity(Intent.createChooser(shareIntent, "choose one"))
}
/*
fun Activity.showExitDialog(callback: (Boolean) -> Unit) {
    val builder = android.app.AlertDialog.Builder(this)
    val inflater = layoutInflater
    val dialogView = inflater?.inflate(R.layout.simple_exit_dialouge, null)
    builder.setView(dialogView)
    val alertDialog = builder.create()
    alertDialog.setCanceledOnTouchOutside(false)
    if (alertDialog != null) {
        if (!isFinishing) {
            // val params: ViewGroup.LayoutParams = alertDialog?.getWindow()?.getAttributes()!!
            //val back = ColorDrawable(Color.TRANSPARENT)
            // val inset = InsetDrawable(back, 30)
            // alertDialog.window!!.setBackgroundDrawable(inset)
            alertDialog.show()
        }
    }

    val btnYes = dialogView.findViewById<android.widget.TextView>(R.id.btnYes)
    val btnCancel = dialogView.findViewById<android.widget.TextView>(R.id.btnNo)

    btnYes.setOnClickListener {
        callback(true)
        if (!isFinishing) {
            alertDialog.dismiss()
        }
    }

    btnCancel.setOnClickListener {
        if (!isFinishing) {
            alertDialog.dismiss()
        }
    }

}
*/

var TRENDING_VIDEO_URL = "TRENDING_VIDEO_URL"
var TRENDING_VIDEO_TITLE = "TRENDING_VIDEO_TITLE"
var bottomSheetNative: Any? = null
