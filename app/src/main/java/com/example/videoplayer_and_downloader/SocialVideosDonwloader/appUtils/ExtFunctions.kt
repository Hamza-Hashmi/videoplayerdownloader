package com.example.videodownload.appUtils

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.FileProvider
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils.DownloadFailException
import com.example.videoplayer_and_downloader.Utils.MyApplication
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


fun Activity.ShowDeleteDialog(callback: (Boolean) -> Unit) {
    val builder = android.app.AlertDialog.Builder(this)
    val inflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.video_cancel_dialog, null)
    builder.setView(dialogView)
    val alertDialog = builder.create()
    alertDialog.setCanceledOnTouchOutside(false)
    if (alertDialog != null) {
        if (!isFinishing) {
            val params: ViewGroup.LayoutParams = alertDialog.window?.attributes!!
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 30)
            alertDialog.window!!.setBackgroundDrawable(inset)
            alertDialog.show()
        }
    }
    val btnYes = dialogView.findViewById<TextView>(R.id.tv_btnYes)
    val btnCancel = dialogView.findViewById<TextView>(R.id.tv_btnCancel)

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
fun Activity.RenameDialog(link : String, callback: (String, String) -> Unit) {

    val builder = android.app.AlertDialog.Builder(this)
    val inflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.rename_video_dialog, null)
    builder.setView(dialogView)
    val alertDialog = builder.create()
    alertDialog.setCanceledOnTouchOutside(false)
    if (alertDialog != null) {
        if (!isFinishing) {
            val params: ViewGroup.LayoutParams = alertDialog.window?.attributes!!
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 30)
            alertDialog.window!!.setBackgroundDrawable(inset)
            alertDialog.show()
        }
    }

    val editText = dialogView.findViewById(R.id.et_reanmeVideo) as EditText
    val btnYes = dialogView.findViewById<TextView>(R.id.tv_btnyes)
    val btnCross = dialogView.findViewById<ImageView>(R.id.iv_name_remove)
    val btnCancel = dialogView.findViewById<TextView>(R.id.tv_btnCancel)
    var oldName = ""
    editText.setText(link.split(".")!![0])
    oldName = link// editText.text.toString()

    editText.setOnTouchListener(object : View.OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (v is EditText) {
                v.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        btnYes.setTextColor(resources.getColor(R.color.primary_color))
                    }
                })
            }
            return false
        }
    })

    btnCross.setOnClickListener {
        editText.setText("")
    }

    btnCancel?.setOnClickListener {
        alertDialog.dismiss()
    }

    btnYes.setOnClickListener {
        if (!editText.text.isNullOrEmpty() && !oldName.isNullOrEmpty()) {
            callback(editText.text.trim().toString(), oldName)
            alertDialog.dismiss()
        } else {
            Toast.makeText(this, "invalid name", Toast.LENGTH_SHORT).show()
        }
    }
}
fun Activity.DeleteSavedVideo(callback: (Boolean) -> Unit) {

    val builder = android.app.AlertDialog.Builder(this)
    val inflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.file_delete_dialog, null)
    builder.setView(dialogView)
    val alertDialog = builder.create()
    alertDialog.setCanceledOnTouchOutside(false)
    if (alertDialog != null) {
        if (!isFinishing) {
            val params: ViewGroup.LayoutParams = alertDialog.window?.attributes!!
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 30)
            alertDialog.window!!.setBackgroundDrawable(inset)
            alertDialog.show()
        }
    }

    val btnCancel = dialogView.findViewById<TextView>(R.id.tv_btnCancel)
    val btnDelete = dialogView.findViewById<TextView>(R.id.tv_btnDelete)


    btnDelete.setOnClickListener {
        callback(true)
        if (!isFinishing) {
            alertDialog.dismiss()
        }
    }

    btnCancel.setOnClickListener {
        callback(false)
        if (!isFinishing) {
            alertDialog.dismiss()
        }
    }
}



fun Context.getTimeStamp():String{
    val timeStamp: String = SimpleDateFormat("yyyyMMdd HH.mm.ss").format(Date())
  return timeStamp
}

//Create method appInstalledOrNot
fun Activity.appInstalledOrNot(url: String): Boolean {
    val packageManager: PackageManager = packageManager
    val app_installed: Boolean
    app_installed = try {
        packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return app_installed
}

fun Activity.hideKeybaord(v: View) {
    val inputMethodManager: InputMethodManager? =
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.hideSoftInputFromWindow(v.applicationWindowToken, 0)
}

fun Context.getStringFromResource(resId: Int): String {
    return resources.getString(resId)
}

fun Context.isDailymotionDownloadUrl(video: VideoListClass.VideoInfo): Boolean {
    val dailymotionHomeVideo = getString(R.string.dailymotion_url) + "/"
    return video.page?.contains(dailymotionHomeVideo, true)!!
}

fun Context.getDailyMotionVideoThumbnail(video: VideoListClass.VideoInfo): String {
    val dailymotionHomeVideo = getString(R.string.dailymotion_url) + "/"
    return video.page?.substring(
        IntRange(
            0,
            dailymotionHomeVideo.length - 1
        )
    ) + "thumbnail" + video.page?.substring(
        IntRange(
            dailymotionHomeVideo.length - 1,
            video.page!!.length - 1
        )
    )
}

fun Activity.shareVideoFile(fileToShare: File) {
    try {
        val intentShareFile = Intent(Intent.ACTION_SEND)

        val fileUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            fileToShare
        )
        intentShareFile.type = "video/*"
        intentShareFile.putExtra(
            Intent.EXTRA_SUBJECT,
            "HD Video Downloader"
        )
        intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri)
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intentShareFile, "Share Video"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> {
                false
            }
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}
fun Activity.showShortMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
@Throws(DownloadFailException::class)
fun Context.PrepareDirectory(): File? {
    return MyApplication.getMyApplicationContext()?.applicationContext?.filesDir
}

fun Context.getMainStoragePath(): String {
    return MyApplication.getMyApplicationContext()?.applicationContext?.filesDir?.absolutePath!!
}


fun Activity.showyoutubeNotSupported() {
    val builder = android.app.AlertDialog.Builder(this)
    val inflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.youtube_supported_dailog, null)
    builder.setView(dialogView)
    val alertDialog = builder.create()
    alertDialog.setCanceledOnTouchOutside(false)
    if (alertDialog != null) {
        if (!isFinishing) {
            val params: ViewGroup.LayoutParams = alertDialog.window?.attributes!!
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 30)
            alertDialog.window!!.setBackgroundDrawable(inset)
            alertDialog.show()
        }
    }
    val btnCancel = dialogView.findViewById(R.id.tv_btnCancel) as TextView
    val btnOk = dialogView.findViewById(R.id.tv_btnOK) as TextView
    btnOk.setOnClickListener {
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
fun Context.getFileSize(size: Long):String {
    val format = DecimalFormat("#.##")
    val MiB = 1024*1024
    val KiB = 1024
    if (size > MiB) {
        return format.format(size / MiB) + " MB"
    }
    if (size > KiB) {
        return format.format(size / KiB) + " KB"
    }
    return format.format(size) + " B"
}
