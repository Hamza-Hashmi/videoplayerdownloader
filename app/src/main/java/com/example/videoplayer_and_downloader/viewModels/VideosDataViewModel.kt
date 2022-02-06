package com.example.videoplayer_and_downloader.viewModels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videoplayer_and_downloader.DataSource.VideosDataRepository
import com.example.videoplayer_and_downloader.Utils.MySharedPreferences
import com.example.videoplayer_and_downloader.Utils.adItem
import com.example.videoplayer_and_downloader.Utils.getFolderAdItem
import com.example.videoplayer_and_downloader.models.CustomFiles
import com.example.videoplayer_and_downloader.models.Folder
import com.example.videoplayer_and_downloader.models.ImagesFolder
import com.example.videoplayer_and_downloader.models.MediaModel
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Albums
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Artist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class VideosDataViewModel( application:Application,val repo: VideosDataRepository) : AndroidViewModel(application){
    var videoFolderData = MutableLiveData<ArrayList<Folder>>()
    var videoFolderDataWithAds = MutableLiveData<ArrayList<Folder>>()
    var allFolderData = MutableLiveData<ArrayList<Folder>>()
    var allImagesData = MutableLiveData<ArrayList<Uri>>()
    var allImagesFolder = MutableLiveData<ArrayList<ImagesFolder>>()
    var allVideos = MutableLiveData<ArrayList<MediaModel>>()
    var allVideosSongs = MutableLiveData<ArrayList<MediaModel>>()

    var allAudios = MutableLiveData<ArrayList<MediaModel>>()
    var allAlbums = MutableLiveData<ArrayList<Albums>>()
    var allArtists = MutableLiveData<ArrayList<Artist>>()

    var allDeviceData = MutableLiveData<ArrayList<Folder>>()

    var onClickItem: ((String) -> Unit)? = null
    var onClickRateUs: ((String) -> Unit)? = null


    fun getAllFolders()
    {

        repo.getDataVideoFolder()
    }

    fun getAllFolders2()
    {

        allFolderData.postValue(repo.getDataVideoFolder())
    }

    fun getAllImagesFromDevice()
    {

        repo.getAllImagesFromDevice()
    }

    fun searchItemInFolder(keyword: String)
    {
        allFolderData.postValue(repo.allFolders.filter {
            it.name.contains(keyword, true)
        } as ArrayList<Folder>)
    }

    fun getVideoFolderData(){

        videoFolderData.postValue(repo.getFolderMediaVideo())
    }




    fun getUpdatedData() {

        allFolderData.postValue(repo.allFolders)
        allVideos.postValue(repo.allVideos)
        allVideosSongs.postValue(repo.allVideosSongs)
        allDeviceData.postValue(repo.allFolders)
//        allImagesData.postValue(repo.allImagesData)
//        allImagesFolder.postValue(repo.allImagesFolder)
        repo.getMediaData()
        videoFolderData.postValue(repo.videoFolders)


    }
    fun getAllImagesUpdatedData() {


        allImagesData.postValue(repo.allImagesData)
        allImagesFolder.postValue(repo.allImagesFolder)


    }



    fun updateMedia(
        existingFile: CustomFiles, newFile: CustomFiles
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            repo.updateMediaInData(existingFile, newFile)

        }.invokeOnCompletion {
            getUpdatedData()
        }
    }
    fun updateMedia(
        existingFile: CustomFiles, newFile: CustomFiles,onComplete:(() -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            repo.updateMediaInData(existingFile, newFile)

        }.invokeOnCompletion {
            onComplete?.invoke()
        }
    }


    fun deleteMedia(existingFile: MediaModel) {
        CoroutineScope(Dispatchers.Default).launch {
            repo.deleteMediaInData(existingFile = existingFile)
        }.invokeOnCompletion {
            getUpdatedData()
        }
    }

    fun postVideoFoldersWithAds(context: Context) {
        val originalFolders = ArrayList<Folder>(repo.videoFolders)
        val sharedPreferences = MySharedPreferences(context)
        if (!sharedPreferences.getSubscriptionCheck()) {
            val adPosition = if (sharedPreferences.getVideosGridLayout()) {
                4
            } else
                1
            if (originalFolders.size > adPosition) {
                if (originalFolders[adPosition].name != adItem)
                    originalFolders.add(adPosition, getFolderAdItem())
            }
        }
        videoFolderDataWithAds.postValue(originalFolders)
    }

    fun deleteMedia(realPath: String) {
        CoroutineScope(Dispatchers.Default).launch {
            Log.d("deletedUri5", realPath)
            repo.deleteMediaInData(deletedUri = realPath)
        }.invokeOnCompletion {
            getUpdatedData()
        }
    }


}