package com.example.videoplayer_and_downloader.services

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat

class MediaSessionCallback( val audioPlayerService: AudioPlayerService) :
    MediaSessionCompat.Callback() {
    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        audioPlayerService.seekTo(pos.toInt())
    }
    }