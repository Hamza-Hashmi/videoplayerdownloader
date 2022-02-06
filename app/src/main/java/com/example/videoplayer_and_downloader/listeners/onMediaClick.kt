package com.example.videoplayer_and_downloader.listeners

import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo

interface onMediaClick {
    fun onMediaPlayed(position: Int, media: MediaModel, info: SelectedListInfo)
    fun onMediaAddedToPlaylist(position: Int, media: MediaModel){}
    fun onMediaRemovedFromPlaylist(position: Int, media: MediaModel, selectedListInfo: SelectedListInfo){}
    fun onMediaRemovedFromHistory(position: Int, media: MediaModel, selectedListInfo: SelectedListInfo){}
    fun onMediaAddedToFavorites(position: Int, media: MediaModel){}
    fun onMediaRemovedFromFavorites(position: Int, media: MediaModel){}
    fun onMediaRename(position: Int, media: MediaModel, selectedListInfo: SelectedListInfo){}
    fun onMediaDelete(position: Int, media: MediaModel, selectedListInfo: SelectedListInfo){}
    fun onMediaShare(position: Int, media: MediaModel){}
    fun onAlbumClick( albumName:String, info: SelectedListInfo){}
    fun onArtistClick(artistName:String, info: SelectedListInfo){}

}