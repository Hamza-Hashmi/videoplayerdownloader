package com.example.videoplayer_and_downloader.Utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.videoplayer_and_downloader.R

class MySharedPreferences(private var context: Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(
        getStringHere(R.string.mainPreferences),
        AppCompatActivity.MODE_PRIVATE
    )
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getStringHere(resId: Int): String {
        return context.resources.getString(resId)
    }

    fun setAdShow(adShow: Boolean) {
        editor.putBoolean(getStringHere(R.string.adShow), adShow).apply()
    }

    fun getAdShow(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.adShow), false)
    }

    fun setVideosGridLayout(gridLayout: Boolean) {
        editor.putBoolean(getStringHere(R.string.gridLayout), gridLayout).apply()
    }

    fun getVideosGridLayout(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.gridLayout), true)
    }

    fun setAudioServiceRunning(serviceRunning: Boolean) {
        editor.putBoolean(getStringHere(R.string.audioServiceRunning), serviceRunning).apply()
    }

    fun getAudioServiceRunning(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.audioServiceRunning), false)
    }

    fun setVideoServiceRunning(serviceRunning: Boolean) {
        editor.putBoolean(getStringHere(R.string.videoServiceRunning), serviceRunning).apply()
    }

    fun getVideoServiceRunning(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.videoServiceRunning), false)
    }

    fun setTheme(theme: String) {
        editor.putString(getStringHere(R.string.selected_theme), theme).apply()
    }

   /* fun getTheme(): String {
        return sharedPreferences.getString(
            getStringHere(R.string.selected_theme),
            getStringHere(R.string.theme_2)
        ) ?: getStringHere(R.string.theme_2)
    }
*/
    fun setDefaultScreenOrientation(orientation: String) {
        editor.putString(getStringHere(R.string.default_screen_orientation), orientation).apply()
    }

    fun getDefaultScreenOrientation(): String {
        return sharedPreferences.getString(
            getStringHere(R.string.default_screen_orientation),
            getStringHere(R.string.screen_orientation_portrait)
        ) ?: getStringHere(R.string.screen_orientation_portrait)
    }

    fun setLastPosition(realPath: String, duration: Long) {
        editor.putLong(realPath, duration).apply()
    }


    fun getLastPosition(realPath: String): Long {
        return if (getResumeStatus())
            sharedPreferences.getLong(realPath, 0)
        else
            0
    }

    fun setDoubleTapDuration(duration: Int) {
        editor.putInt(getStringHere(R.string.double_tap_to_seek), duration).apply()
    }

    fun getDoubleTapDuration(): Int {
        return sharedPreferences.getInt(
            getStringHere(R.string.double_tap_to_seek),
            10
        )
    }

    fun setShowHistoryStatus(status: Boolean) {
        editor.putBoolean(getStringHere(R.string.show_history), status).apply()
    }

    fun getShowHistoryStatus(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.show_history), true)
    }


    fun setShowVideoNameStatus(status: Boolean) {
        editor.putBoolean(getStringHere(R.string.show_video_name), status).apply()
    }

    fun getShowVideoNameStatus(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.show_video_name), true)
    }


    fun setResumeStatus(status: Boolean) {
        editor.putBoolean(getStringHere(R.string.resume), status).apply()
    }

    fun getResumeStatus(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.resume), false)
    }

    fun setGestureOperationStatus(status: Boolean) {
        editor.putBoolean(getStringHere(R.string.gesture_operation), status).apply()
    }

    fun getGestureOperationStatus(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.gesture_operation), true)
    }

    fun setAutoPopupPlayStatus(status: Boolean) {
        editor.putBoolean(getStringHere(R.string.auto_popup_play), status).apply()
    }

    fun getAutoPopupPlayStatus(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.auto_popup_play), false)
    }

    fun setAlreadyRated() {
        editor.putBoolean(getStringHere(R.string.already_rated), true).apply()
    }

    fun getAlreadyRated(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.already_rated), false)
    }


    fun setAlreadyHaveFolders(value: Boolean) {
        editor.putBoolean(getStringHere(R.string.have_data), value).apply()
    }

    fun getAlreadyHaveFolders(): Boolean {
        return sharedPreferences.getBoolean(getStringHere(R.string.have_data), false)
    }

    /*
    fun setSubscriptionCheck(value: Boolean) {
        editor.putBoolean(BillingConstants.KEY_IS_PURCHASED, value)
        editor.apply()
    }

    fun getSubscriptionCheck(default: Boolean = true): Boolean {
        return sharedPreferences.getBoolean(BillingConstants.KEY_IS_PURCHASED, default)
    }
     */

    fun getSubscriptionCheck(): Boolean {
        return true
    }


}