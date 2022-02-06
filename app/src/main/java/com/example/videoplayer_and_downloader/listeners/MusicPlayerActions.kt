package com.example.videoplayer_and_downloader.listeners

import android.media.MediaPlayer

interface MusicPlayerActions {

    fun playPauseBtnClick()
    fun btnNextClicked()
    fun btnPreviousClicked()
    fun updateSeekbar(currentDuration: Int){}
    fun onCompletion(mp: MediaPlayer?){
    }
    fun setSeekBarDuration(duration: Int){
        duration.and(0)
    }
}