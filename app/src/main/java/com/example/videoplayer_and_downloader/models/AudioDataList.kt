package com.example.videoplayer_and_downloader.models

import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Albums
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Artist

data class AudioDataList(
    var audioSongs: ArrayList<MediaModel>,
    var allArtists:ArrayList<Artist>,
    var allAlbums :ArrayList<Albums>

)