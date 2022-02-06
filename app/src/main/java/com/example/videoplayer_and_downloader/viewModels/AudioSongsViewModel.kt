package com.example.videoplayer_and_downloader.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer_and_downloader.DataSource.AudiosDataRepo
import com.example.videoplayer_and_downloader.Utils.MySharedPreferences
import com.example.videoplayer_and_downloader.models.CustomFiles
import com.example.videoplayer_and_downloader.models.Folder
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.MusicModel
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Albums
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Artist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioSongsViewModel(val repo: AudiosDataRepo) : ViewModel() {



    var tracksList= MutableLiveData<ArrayList<MusicModel>>()
    var albumsList = MutableLiveData<ArrayList<MusicModel>>()
    var artistList = MutableLiveData<ArrayList<MusicModel>>()

    fun getAllMusics(){
        Log.e("TAG", "getAllMusics: " )
        repo.getAllAudioFiles()
    }

    fun getAllTracksList(){
        tracksList.postValue(repo.allAudioFilesList)
    }

    fun getAllAlbumsList(){
        Log.e("TAG", "getAllAlbumsList: " )
        albumsList.postValue(repo.audioAlbumsList)
    }

    fun getAllArtistList(){
        artistList.postValue(repo.audioArtistList)
    }


}