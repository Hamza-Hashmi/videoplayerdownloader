package com.example.videoplayer_and_downloader.DataSource

import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData
import com.example.videoplayer_and_downloader.Utils.allAudioPlaylistId
import com.example.videoplayer_and_downloader.Utils.allVideoPlaylistId
import com.example.videoplayer_and_downloader.Utils.favorites
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.database.MyAppDataBase
import com.example.videoplayer_and_downloader.database.AudioHistory
import com.example.videoplayer_and_downloader.database.History
import com.example.videoplayer_and_downloader.database.MainPlaylist
import com.example.videoplayer_and_downloader.database.PlaylistItem

class DatabaseRepo(private val myAppDataBase: MyAppDataBase) {



    fun addMainPlaylist(mainPlaylist: MainPlaylist){
        myAppDataBase.getAppData().insertMainPlaylist(mainPlaylist)
    }


    fun addHistory(history: History) {
        myAppDataBase.getAppData().insertHistoryItem(history)
    }

    fun addAudioHistory(history: AudioHistory) {
        myAppDataBase.getAppData().insertAudioHistoryItem(history)
    }
    fun addTrendingFavourite(data:TrendingVideoData){
        myAppDataBase.getAppData().insertIntoFavourite(data)
    }




    fun deleteFromFav(videoUrl:String){
        myAppDataBase.getAppData().deleteFromFav(videoUrl)
    }
    fun deleteHistoryItem(singleMedia: MediaModel): Int {
        return myAppDataBase.getAppData().deleteHistoryByPath(singleMedia.realPath)
    }


    fun deleteAudioHistoryItem(singleMedia: MediaModel): Int {
        return myAppDataBase.getAppData().deleteAudioHistoryByPath(singleMedia.realPath)
    }

    fun addPlaylistItem(item: PlaylistItem) {
        myAppDataBase.getAppData().insertPlaylistItem(item)
//        val namesList = listOf(item.playlistId, getAllId(item.isVideo))
        myAppDataBase.getAppData().updatePlaylistItem(item.playlistId, 1)
    }

    private fun getAllId(isVideo: Boolean): Int {
        return if (isVideo)
            allVideoPlaylistId
        else
            allAudioPlaylistId
    }


    fun deletePlaylistItem(singleMedia: MediaModel): Boolean {
        return if (myAppDataBase.getAppData().deletePlaylistItemByPath(
                singleMedia.realPath,
                myAppDataBase.getAppData().getPlaylistByName(singleMedia.playlistName)!!.id
            ) > 0
        ) {
            val namesList = listOf(
                getAllId(singleMedia.isVideo),
                myAppDataBase.getAppData().getPlaylistByName(singleMedia.playlistName)!!.id
            )
            myAppDataBase.getAppData().updatePlaylistItems(namesList, -1)
            true
        } else
            false
    }

    suspend fun updateVideoHistory(history: History)
    {
        myAppDataBase.getAppData().updateVideoHistory(history)
    }

    suspend fun updateVideoHistoryById(old_path:String,name: String,real_path:String,uri:String):Int
    {
        return  myAppDataBase.getAppData().updateVideoHistoryById(old_path,name,real_path,uri)
    }
    suspend fun updateVideoPlaylisItemById(old_path:String,name: String,real_path:String,uri:String):Int
    {
        return  myAppDataBase.getAppData().updateVideoPlaylisItemById(old_path,name,real_path,uri)
    }
    suspend fun updateAudioHistoryById(old_path:String,name: String,real_path:String,uri:String):Int
    {
        return  myAppDataBase.getAppData().updateAudioHistoryById(old_path,name,real_path,uri)
    }

    fun deleteFavorite(singleMedia: MediaModel): Boolean {
        val id = getFavoritePlaylist()!!.id
        return if (myAppDataBase.getAppData().deletePlaylistItemByPath(singleMedia.realPath, id) > 0) {
            val namesList = listOf(
                getAllId(singleMedia.isVideo),
                id
            )
            myAppDataBase.getAppData().updatePlaylistItems(namesList, -1)
            true
        } else
            false
    }


    fun getAllFavourites():List<TrendingVideoData>{
        return myAppDataBase.getAppData().getFavList()
    }
    fun getCompletePlaylist(): List<MainPlaylist> {
        return myAppDataBase.getAppData().getAllMainPlaylists()
    }

    fun getCompleteHistory(): ArrayList<History> {
        val originalList = myAppDataBase.getAppData().getAllHistoryItems()
        val myList = ArrayList<History>()
        if (originalList.isNotEmpty()) {
            for (i in (originalList.size - 1) downTo 0) {
                myList.add(originalList[i])
            }
        }
        return myList
    }
    fun getMyVideoHistory() = myAppDataBase.getAppData().getAllHistoryItems()
    fun getMyAudioHistory() = myAppDataBase.getAppData().getAllAudioHistoryItems()
    fun getCompleteAudioHistory(): ArrayList<AudioHistory> {
        val originalList = myAppDataBase.getAppData().getAllAudioHistoryItems()
        val myList = ArrayList<AudioHistory>()
        if (originalList.isNotEmpty()) {
            for (i in (originalList.size - 1) downTo 0) {
                myList.add(originalList[i])
            }
        }
        return myList
    }

    fun getCompletePlaylistItems()= myAppDataBase.getAppData().getAllPlaylistItems()


    fun getCompletePlaylistItemsByName(name: String): List<PlaylistItem> {
        return myAppDataBase.getAppData().getAllPlaylistItemsByName(name)
    }

    fun getAllFavouriteItem(name: String): List<PlaylistItem> {
        return myAppDataBase.getAppData().getAllPlaylistItemsByName(name)
    }

    fun getFavoritePlaylist(): MainPlaylist? {
        return myAppDataBase.getAppData().getPlaylistByName(favorites)
    }


}