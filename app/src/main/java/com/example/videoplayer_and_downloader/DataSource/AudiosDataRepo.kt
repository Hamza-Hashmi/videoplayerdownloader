package com.example.videoplayer_and_downloader.DataSource

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.videoplayer_and_downloader.models.*
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.AlbumDetails
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Albums
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Artist
import java.io.File
import java.lang.Exception

class AudiosDataRepo(val context:Context) {


    var allAudioFilesList: ArrayList<MusicModel> = ArrayList()
    var audioAlbumsList: ArrayList<MusicModel> = ArrayList()
    var audioArtistList: ArrayList<MusicModel> = ArrayList()


/*
    val allFolders = ArrayList<Folder>()


    //    val videoFolders = ArrayList<Folder>()
    val audioFolders = ArrayList<Folder>()
    var allAudios = ArrayList<MediaModel>()
    var allAudiosSongs = ArrayList<MediaModel>()

    var allImagesData = ArrayList<Uri>()

    private val albumDetails = ArrayList<AlbumDetails>()
    var allAlbums = ArrayList<Albums>()
    var allArtists = ArrayList<Artist>()

*/

    fun getAllAudioFiles(){
        val duplicate = ArrayList<String>()
        val artsitDuplicate = ArrayList<String>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,  // FOR PATH
            MediaStore.Audio.Media.ARTIST
        )

        val cursor: Cursor? = context.applicationContext.getContentResolver().query(
            uri, projection,
            null, null, null
        )

        if (cursor != null) {
            var mDuration = 0
            while (cursor.moveToNext()) {
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2) ?: "20"
                val data = cursor.getString(3)
                val artist = cursor.getString(4)
                val musicFile = MusicModel(album, title, duration, data, artist)

                Log.e("TAG", "getAllAudioFiles: $title")
                try {
                    mDuration = metaData(duration.toInt())
                }catch (e: Exception){
                    mDuration = 5
                }

                if (!(data.contains("WhatsApp") )){
                    if(mDuration > 5){
                        if (data.contains(".mp3"))

                            allAudioFilesList.add(musicFile)
                        if (!duplicate.contains(album)) {
                            audioAlbumsList.add(musicFile)
                                duplicate.add(album)
                        }
                        if (!artsitDuplicate.contains(artist)){
                            audioArtistList.add(musicFile)
                            artsitDuplicate.add(artist)
                        }
                    }


                }

                // for removing duplicate albums

            }
            cursor.close()
        }
    }


    /*fun getCompleteData() {
        allFolders.clear()
        allAudios.clear()
        allAlbums.clear()
        allArtists.clear()

        //val audios_Data=fetchStorageData.getMediaAudio()

        *//*allAudios=audios_Data.audioSongs
        allAlbums=audios_Data.allAlbums
        allArtists=audios_Data.allArtists
        *//*//start making all Folders

        Log.e("TAG", "getCompleteData: audio_songs  ${allAudios.size}", )
        allAudiosSongs=allAudios
        Log.e("TAG", "getCompleteData: allAudiosSongs _size  ${allAudiosSongs.size}", )

        storeFilesInFolders()

    }
*/
/*
    fun getAllImagesFromDevice()
    {
        allImagesData.clear()
        allImagesData=fetchStorageData.getLocalImagePaths()


    }
*/



/*
    fun getAllAudioData() {

        allAudios.clear()
        allAlbums.clear()
        allArtists.clear()

        val audios_Data=fetchStorageData.getMediaAudio()

        allAudios=audios_Data.audioSongs
        allAlbums=audios_Data.allAlbums
        allArtists=audios_Data.allArtists
        //start making all Folders

        Log.e("TAG", "getCompleteData: audio_songs  ${allAudios.size}", )
        Log.e("TAG", "getCompleteData: allAlbums  ${allAlbums.size}", )
        Log.e("TAG", "getCompleteData: allArtists  ${allArtists.size}", )
        allAudiosSongs=allAudios
        Log.e("TAG", "getCompleteData: allAudiosSongs _size  ${allAudiosSongs.size}", )


    }

    fun getAllAudioFolders()
    {
        fetchStorageData.getAllAudioFolders(allAudiosSongs)
    }
*/

    /*private fun storeFilesInFolders() {
        allAudioFilesList.forEach { media ->
            val file = File(media.path)
            file.let { mainFile ->
                if (mainFile.exists()) {
                    val parentFile = file.parentFile
                    parentFile?.let { fileParent ->
                        if (fileParent.exists()) {
                            val folderPosition = getSelectedFolder(fileParent)
                            if (folderPosition == -1)
                                allFolders.add(createNewFolder(parentFile, media))
                            else
                                allFolders[folderPosition].audioFiles.add(media)
                        }

                    }
                }
            }
        }

    }
*/

    private fun createNewFolder(parentFile: File, media: MediaModel): Folder {
        return if (media.isVideo) {
            Folder(
                name = parentFile.name,
                path = parentFile.absolutePath,
                videoFiles = arrayListOf(media),
                audioFiles = ArrayList()
            )
        } else {
            Folder(
                name = parentFile.name,
                path = parentFile.absolutePath,
                videoFiles = ArrayList(),
                audioFiles = arrayListOf(media)
            )
        }

    }

  /*  private fun getSelectedFolder(parentFile: File): Int {
        var position = -1
        for (i in 0 until allFolders.size) {
            val currentFolder = allFolders[i]
            if (currentFolder.path == parentFile.absolutePath) {
                position = i
                break
            }
        }
        return position
    }
*/

  /*  fun getMediaData() {
//        videoFolders.clear()
        audioFolders.clear()

        Log.e("TAG", "getMediaData: allFolders size  ${allFolders.size}", )

        allFolders.forEachIndexed { _, folder ->
            val folderFile = File(folder.path)
            if (folderFile.exists()) {
//                if (folder.videoFiles.size > 0) {
//                    videoFolders.add(folder)
//                }
                if (folder.audioFiles.size > 0)
                    audioFolders.add(folder)

            }
        }

    }

*/
   /* fun updateMediaInData(existingFile: CustomFiles, newFile: CustomFiles) {

        allAudios.find { media ->
            media.realPath == existingFile.realPath
        }?.updateNameAndPath(name = newFile.name, path = newFile.realPath)

        allFolders.forEachIndexed { index, folder ->
            if (existingFile.isVideo) {
                folder.videoFiles.find { media ->
                    media.realPath == existingFile.realPath
                }?.updateNameAndPath(name = newFile.name, path = newFile.realPath)
                allFolders[index].updateVideoFiles(folder.videoFiles)
            } else {
                folder.audioFiles.find { media ->
                    media.realPath == existingFile.realPath
                }?.updateNameAndPath(name = newFile.name, path = newFile.realPath)
                allFolders[index].updateAudioFiles(folder.audioFiles)
            }
        }
    }


    fun deleteMediaInData(existingFile: MediaModel) {
        for (i in 0 until allAudios.size) {
            if (allAudios[i].realPath == existingFile.realPath) {
                allAudios.removeAt(i)
                break
            }
        }

        if (existingFile.isVideo) {
            var mainIndex = -1
            var videoIndex = -1
            for (i in 0 until allFolders.size) {
                var found = false
                for (j in 0 until allFolders[i].videoFiles.size) {
                    if (allFolders[i].videoFiles[j].realPath == existingFile.realPath) {
                        mainIndex = i
                        videoIndex = j
                        found = true
                        break
                    }
                }
                if (found)
                    break
            }
            if (mainIndex != -1 && videoIndex != -1)
                allFolders[mainIndex].videoFiles.removeAt(videoIndex)
        } else {
            var mainIndex = -1
            var audioIndex = -1
            for (i in 0 until allFolders.size) {
                var found = false
                for (j in 0 until allFolders[i].audioFiles.size) {
                    if (allFolders[i].audioFiles[j].realPath == existingFile.realPath) {
                        mainIndex = i
                        audioIndex = j
                        found = true
                        break
                    }
                }
                if (found)
                    break
            }
            if (mainIndex != -1 && audioIndex != -1)
                allFolders[mainIndex].audioFiles.removeAt(audioIndex)
        }
    }


    fun deleteMediaInData(deletedUri: String) {
        Log.d("deletedUri6", deletedUri)

        for (i in 0 until allAudios.size) {
            if (allAudios[i].realPath == deletedUri) {
                allAudios.removeAt(i)
                break
            }
        }

        var mainIndex = -1
        var videoIndex = -1
        for (i in 0 until allFolders.size) {
            var found = false
            for (j in 0 until allFolders[i].videoFiles.size) {
                if (allFolders[i].videoFiles[j].realPath == deletedUri) {
                    mainIndex = i
                    videoIndex = j
                    found = true
                    break
                }
            }
            if (found)
                break
        }

        if (mainIndex != -1 && videoIndex != -1)
            allFolders[mainIndex].videoFiles.removeAt(videoIndex)

        var mainIndex2 = -1
        var audioIndex2 = -1
        for (i in 0 until allFolders.size) {
            var found = false
            for (j in 0 until allFolders[i].audioFiles.size) {
                if (allFolders[i].audioFiles[j].realPath == deletedUri) {
                    mainIndex2 = i
                    audioIndex2 = j
                    found = true
                    break
                }
            }
            if (found)
                break
        }
        if (mainIndex2 != -1 && audioIndex2 != -1)
            allFolders[mainIndex2].audioFiles.removeAt(audioIndex2)

    }
*/

    fun metaData(duration:Int) : Int {

        val dur = duration
        val scs = dur % 60000 / 1000
        return scs
    }
}