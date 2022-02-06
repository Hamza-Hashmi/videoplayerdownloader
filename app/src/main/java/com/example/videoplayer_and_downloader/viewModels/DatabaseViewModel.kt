package com.example.videoplayer_and_downloader.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.videoplayer_and_downloader.DataSource.DatabaseRepo
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData
import com.example.videoplayer_and_downloader.Utils.currentSelectedParent
import com.example.videoplayer_and_downloader.Utils.favorites
import com.example.videoplayer_and_downloader.Utils.new
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.database.AudioHistory
import com.example.videoplayer_and_downloader.database.History
import com.example.videoplayer_and_downloader.database.MainPlaylist
import com.example.videoplayer_and_downloader.database.PlaylistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DatabaseViewModel(application: Application, val repo: DatabaseRepo) : AndroidViewModel(application) {

    var allVideoPlaylist: MutableLiveData<List<MainPlaylist>> = MutableLiveData()
    var allAudioPlaylist: MutableLiveData<List<MainPlaylist>> = MutableLiveData()
    var allHistory: MutableLiveData<ArrayList<History>> = MutableLiveData()
    var allAudioHistory: MutableLiveData<ArrayList<AudioHistory>> = MutableLiveData()
    var allPlaylistItemVideos: MutableLiveData<List<PlaylistItem>> = MutableLiveData()
    var allPlaylistItemAudios: MutableLiveData<List<PlaylistItem>> = MutableLiveData()
    var allPlaylistByNameVideos: MutableLiveData<List<PlaylistItem>> = MutableLiveData()
    var allPlaylistByNameAudios: MutableLiveData<List<PlaylistItem>> = MutableLiveData()
    var allFavouritePlaylistByNameAudios: MutableLiveData<List<PlaylistItem>> = MutableLiveData()

    var allFavourites:MutableLiveData<List<TrendingVideoData>> = MutableLiveData()



    fun getPlaylist() {
        val postPlaylist = repo.getCompletePlaylist()
        val videoList = ArrayList<MainPlaylist>()
        val audioList = ArrayList<MainPlaylist>()
        postPlaylist.forEach {
            if (it.videoPlaylist)
                videoList.add(it)
            else
                audioList.add(it)
        }
//        videoList.add(MainPlaylist(name = new, numItems = 0, videoPlaylist = true))
//        audioList.add(MainPlaylist(name = new, numItems = 0, videoPlaylist = false))
        allVideoPlaylist.postValue(videoList)
        allAudioPlaylist.postValue(audioList)
    }

    fun getPlaylistToAdd() {
        val postPlaylist = repo.getCompletePlaylist()
        val videoList = ArrayList<MainPlaylist>()
        val audioList = ArrayList<MainPlaylist>()
        postPlaylist.forEach {
            if (it.videoPlaylist)
                videoList.add(it)
            else
                audioList.add(it)
        }
        audioList.removeAt(0)
        videoList.removeAt(0)
        videoList.add(MainPlaylist(name = new, videoPlaylist = true))
        audioList.add(MainPlaylist(name = new, videoPlaylist = false))
        allVideoPlaylist.postValue(videoList)
        allAudioPlaylist.postValue(audioList)
    }

    fun getHistory() {
        val postHistory = repo.getCompleteHistory()
        allHistory.postValue(postHistory)
    }
    fun getAudioHistory() {
        val postHistory = repo.getCompleteAudioHistory()
        allAudioHistory.postValue(postHistory)
    }

    fun addToFav(data:TrendingVideoData){
        repo.addTrendingFavourite(data)
    }

    fun deleteFavourite(videoUrl:String){
        repo.deleteFromFav(videoUrl)
    }

    fun getAllFav(){
        val data = repo.getAllFavourites()
        allFavourites.postValue(data)
    }

    fun addMainPlaylist(name: String, numItems: Int, isVideoPlaylist: Boolean): Int {
        val mainPlaylist =
            MainPlaylist(name = name, numItems = numItems, videoPlaylist = isVideoPlaylist)
        return repo?.let {
            val list = it.getCompletePlaylist()
            var status = 1
            for (p in list) {
                if (p.name == name) {
                    status = 0
                    break
                }
            }
            if (status == 1)
                it.addMainPlaylist(mainPlaylist)
            status
        } ?: -1
    }
    fun addMainPlaylistFavourite(name: String, numItems: Int, isVideoPlaylist: Boolean): Int {
        val mainPlaylist =
            MainPlaylist(name = name, numItems = numItems, videoPlaylist = isVideoPlaylist)
        return repo?.let {
            val list = it.getCompletePlaylist()
            var status = 1
            if (list.isNullOrEmpty()) {
                Log.e("TAG_Ad", "addMainPlaylistFavourite:   add_Favourite", )
                if (status == 1)
                    it.addMainPlaylist(mainPlaylist)
            }
            status
        } ?: -1
    }


    fun addMainPlaylist(mainPlaylist: MainPlaylist) {
        repo.addMainPlaylist(mainPlaylist)
    }


    /*
    fun addDefaultPlaylist(mainPlaylist: List<MainPlaylist>) {
        repo?.addDefaultPlaylists(mainPlaylist)
        getPlaylist()
    }
     */
    fun getVideosPlaylistItems() {
        val playlistItems = repo.getCompletePlaylistItems()
        val videoList = ArrayList<PlaylistItem>()
        val audioList = ArrayList<PlaylistItem>()
        playlistItems.forEach {
            if (it.isVideo)
                videoList.add(it)


        }
        allPlaylistItemVideos.postValue(videoList)


    }
    private fun getPlaylistItems() {
        val playlistItems = repo?.getCompletePlaylistItems() ?: ArrayList()
        val videoList = ArrayList<PlaylistItem>()
        val audioList = ArrayList<PlaylistItem>()
        playlistItems.forEach {
            if (it.isVideo)
                videoList.add(it)
            else
                audioList.add(it)
        }
        allPlaylistItemVideos.postValue(videoList)

        allPlaylistItemAudios.postValue(audioList)

    }


    fun getPlaylistItemsByName(name: String) {
        val playlistItems = repo?.getCompletePlaylistItemsByName(name) ?: ArrayList()
        val videoList = ArrayList<PlaylistItem>()
        val audioList = ArrayList<PlaylistItem>()
        playlistItems.forEach {
            if (it.isVideo)
                videoList.add(it)
            else
                audioList.add(it)
        }
        allPlaylistByNameVideos.postValue(videoList)
        allPlaylistByNameAudios.postValue(audioList)
    }
    fun getFavouritePlaylistItemsByName(name: String) {
        Log.e("TAG", "getFavouritePlaylistItemsByName:  ${name} ", )
        val playlistItems = repo.getCompletePlaylistItemsByName(name)
//        val videoList = ArrayList<PlaylistItem>()
//        playlistItems.forEach {
//            if (it.isVideo)
//                videoList.add(it)
//        }
        Log.e("TAG", "getFavouritePlaylistItemsByName:  playlistItems_size ${playlistItems.size}", )
        allFavouritePlaylistByNameAudios.postValue(playlistItems)
    }


    fun addHistory(singleMedia: MediaModel) {
        val h = History(
            realPath = singleMedia.realPath,
            name = singleMedia.name,
            isVideo = singleMedia.isVideo,
            uri = singleMedia.uri
        )
        repo?.let {
            if (isHistoryExists(h)) {
                val j = it.deleteHistoryItem(singleMedia)
            }
            it.addHistory(h)
        }
    }
    fun addAudioHistory(singleMedia: MediaModel) {
        val h = AudioHistory(
            realPath = singleMedia.realPath,
            name = singleMedia.name,
            isVideo = singleMedia.isVideo,
            uri = singleMedia.uri
        )
        if (isAudioHistoryExists(h)) {
            val j = repo.deleteAudioHistoryItem(singleMedia)
        }
        repo.addAudioHistory(h)

    }

    fun addPlaylistItem(singleMedia: MediaModel, mainPlaylist: MainPlaylist): Int {
        return repo?.let {
            var exist = false
            val playlistItems = it.getCompletePlaylistItems()
            for (item in playlistItems) {
                if (item.realPath == singleMedia.realPath && item.playlistId == mainPlaylist.id) {
                    exist = true
                    break
                }
            }
            if (exist) {
                0
            } else {

                GlobalScope.launch(Dispatchers.IO) {
                    it.addPlaylistItem(createPlaylistItem(singleMedia, mainPlaylist))

                }.invokeOnCompletion {
                    getPlaylistItems()

                }

                1
            }
        } ?: -1

    }


    fun isFavorite(realPath: String): Boolean {
        return repo?.let {
            var exist = false
            val playlistItems = it.getCompletePlaylistItems()
            for (item in playlistItems) {
                if (item.realPath == realPath && item.playlistName == favorites) {
                    exist = true
                    break
                }
            }
            exist
        } ?: false
    }

    private fun createPlaylistItem(
        singleMedia: MediaModel,
        mainPlaylist: MainPlaylist
    ): PlaylistItem {
        return PlaylistItem(
            name = singleMedia.name,
            realPath = singleMedia.realPath,
            isVideo = singleMedia.isVideo,
            artist = singleMedia.artist,
            album = singleMedia.album,
            playlistId = mainPlaylist.id,
            playlistName = mainPlaylist.name,
            isFavorite = mainPlaylist.name == favorites,
            uri = singleMedia.uri
        )
    }

    fun deleteVideoFromHistory(singleMedia: MediaModel): Boolean {
        return repo?.let {
            if (it.deleteHistoryItem(singleMedia) > 0) {
                getHistory()
                true
            } else
                false
        } ?: false
    }


    fun deleteVideoFromPlaylist(singleMedia: MediaModel): Boolean {
        val status = repo?.deletePlaylistItem(singleMedia)
        return status?.let {
            if (it) {
                getPlaylist()
                getPlaylistItems()
                getPlaylistItemsByName(currentSelectedParent)
                true
            } else
                false
        } ?: false
    }

    suspend fun updateVideoHistory(history: History)
    {
        repo.updateVideoHistory(history)

    }

    suspend fun updateVideoHistoryById(old_path:String,name: String,real_path:String,uri:String):Int
    {
        return  repo.updateVideoHistoryById(old_path,name,real_path,uri)
    }


    suspend fun updateVideoPlaylisItemById(old_path:String,name: String,real_path:String,uri:String):Int
    {
        return  repo.updateVideoPlaylisItemById(old_path,name,real_path,uri)
    }

    suspend fun updateAudioHistoryById(old_path:String,name: String,real_path:String,uri:String):Int
    {
        return  repo.updateAudioHistoryById(old_path,name,real_path,uri)
    }

    fun deleteFavorite(singleMedia: MediaModel): Boolean {
        val status = repo?.deleteFavorite(singleMedia)
        return status?.let {
            if (it) {
                getPlaylist()
                getPlaylistItems()
                getPlaylistItemsByName(favorites)
                true
            } else
                false
        } ?: false
    }


    private  fun isHistoryExists(history: History): Boolean {
        var exists = false
        val postHistory = repo?.getCompleteHistory() ?: ArrayList()
        for (p in postHistory) {
            if (p.realPath == history.realPath) {
                exists = true
                break
            }
        }
        return exists
    }
    private  fun isAudioHistoryExists(history: AudioHistory): Boolean {
        var exists = false
        val postHistory = repo?.getCompleteHistory() ?: ArrayList()
        for (p in postHistory) {
            if (p.realPath == history.realPath) {
                exists = true
                break
            }
        }
        return exists
    }

    fun addToFavorites(media: MediaModel): Int {
        return repo?.let { repository ->
            var status = -1
            repository.getFavoritePlaylist()?.let { favoritePlaylist ->
                status = addPlaylistItem(media, favoritePlaylist)
            }
            status
        } ?: -1
    }


    fun getSearchResults(keyword: String) {
        repo?.apply {
            allPlaylistItemVideos.postValue(getCompletePlaylistItems().filter {
                it.name.contains(keyword, true)
            })
        }
    }

    fun searchHistoryResults(keyword: String) {
        allHistory.postValue(repo.getMyVideoHistory().filter {
            it.name.contains(keyword, true)
        } as ArrayList<History>)
    }

    fun searchAudioHistoryResults(keyword: String) {
        allAudioHistory.postValue(repo.getMyAudioHistory().filter {
            it.name.contains(keyword, true)
        } as ArrayList<AudioHistory>)
    }

    fun searchVideosPlaylistItems(keyword: String) {
        val postPlaylist = repo.getCompletePlaylist()
        val videoList = ArrayList<MainPlaylist>()
        val audioList = ArrayList<MainPlaylist>()
        postPlaylist.forEach {
            if (it.videoPlaylist)
                videoList.add(it)
            else
                audioList.add(it)
        }
//        videoList.add(MainPlaylist(name = new, numItems = 0, videoPlaylist = true))
//        audioList.add(MainPlaylist(name = new, numItems = 0, videoPlaylist = false))
        allVideoPlaylist.postValue(videoList.filter {
            it.name.contains(keyword, true)
        } as ArrayList<MainPlaylist>)


    }
    fun searchAudioPlaylistItems(keyword: String) {
        val postPlaylist = repo.getCompletePlaylist()
        val audioList = ArrayList<MainPlaylist>()
        postPlaylist.forEach {
            if (!it.videoPlaylist) {
                audioList.add(it)
            }
        }
//        videoList.add(MainPlaylist(name = new, numItems = 0, videoPlaylist = true))
//        audioList.add(MainPlaylist(name = new, numItems = 0, videoPlaylist = false))
        allAudioPlaylist.postValue(audioList.filter {
            it.name.contains(keyword, true)
        } as ArrayList<MainPlaylist>)


    }


    fun getUpdatedData() {
        getPlaylist()
        getHistory()
        getPlaylistItems()
    }
    fun getAudioUpdatedData() {
        getPlaylist()
        getAudioHistory()
        getPlaylistItems()
    }


}