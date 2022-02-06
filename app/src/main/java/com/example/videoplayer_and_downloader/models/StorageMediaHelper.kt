package com.example.videoplayer_and_downloader.models

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.AlbumDetails
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Albums
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.Artist
import java.io.File

class StorageMediaHelper(val context: Context) {

    val myTag = "StorageMediaHelper"
    fun getVideoMedia() : ArrayList<MediaModel>{
        val allVideos = ArrayList<MediaModel>()

        val mainUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.VideoColumns.DISPLAY_NAME,
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.VideoColumns._ID,
        )
        val c = context.contentResolver.query(
            mainUri,
            projection,
            null,
            null,
            null
        )
        c?.apply {
            while (moveToNext()) {
                try {
                    Log.d(myTag, "applying Video")
                    val displayName =
                        getString(c.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME))
                    val songUri =
                        getLong(c.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID))
                    val realPath =
                        c.getString(c.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA))


                    val finalUri = ContentUris.withAppendedId(mainUri, songUri)

                    Log.d(myTag, "uri $finalUri")
                    val media = MediaModel(
                        name = displayName,
                        realPath = realPath,
                        isVideo = true,
                        uri = finalUri.toString()
                    )
                    allVideos.add(media)
                } catch (ignored: Exception) {
                }


            }
            close()
        }

        return allVideos
    }

  /*  fun getMediaAudio() : AudioDataList{

        val allArtists = ArrayList<Artist>()
        val allAlbums = ArrayList<Albums>()
        val albumDetails = ArrayList<AlbumDetails>()
        val allAudios = ArrayList<MediaModel>()

        val mainUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
            MediaStore.Audio.AlbumColumns.ALBUM,
            MediaStore.Audio.AlbumColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns._ID,
        )
        val c = context.contentResolver.query(
            mainUri,
            projection,
            null,
            null,
            null
        )
        c?.apply {
            try {
                while (moveToNext()) {
                    Log.d(myTag, "applying Audio")

                    var albumName1 = "unknown"
                    var artistName1 = "unknown"

                    val albumName =
                        getString(c.getColumnIndexOrThrow(MediaStore.Audio.AlbumColumns.ALBUM))
                    albumName?.let {
                        albumName1 = it
                    }

                    val displayName =
                        getString(c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME))

                    val artistName =
                        getString(c.getColumnIndexOrThrow(MediaStore.Audio.AlbumColumns.ARTIST))
                    artistName?.let {
                        artistName1 = it
                    }

                    val songUri =
                        getLong(c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID))

                    val realPath =
                        c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA))

                    val finalUri = ContentUris.withAppendedId(mainUri, songUri)
                    val media = MediaModel(
                        name = displayName,
                        realPath = realPath,
                        isVideo = false,
                        artist = artistName1,
                        album = albumName1,
                        uri = finalUri.toString()
                    )
                    allAudios.add(media)

                    if (nameFound(albumName) || nameFound(artistName)) {
                        albumDetails.add(
                            AlbumDetails(
                                displayName,
                                artistName,
                                albumName,
                                realPath
                            )
                        )
                        if (nameFound(albumName)) {
                            var albumIndex = -1
                            for (i in 0 until allAlbums.size) {
                                if (allAlbums[i].name == albumName) {
                                    albumIndex = i
                                    break
                                }
                            }
                            if (albumIndex == -1) {
                                allAlbums.add(
                                    Albums(
                                        albumName,
                                        arrayListOf(media)
                                    )
                                )
                            } else
                                allAlbums[albumIndex].audioFiles.add(media)
                        }
                        if (nameFound(artistName)) {
                            var artistIndex = -1
                            for (i in 0 until allArtists.size) {
                                if (allArtists[i].name == albumName) {
                                    artistIndex = i
                                    break
                                }
                            }
                            if (artistIndex == -1) {
                                allArtists.add(
                                    Artist(
                                        artistName,
                                        arrayListOf(media)
                                    )
                                )
                            } else
                                allArtists[artistIndex].audioFiles.add(media)
                        }
                    }

                }
            } catch (ignored: Exception) {
            }
            close()
        }

        return AudioDataList(audioSongs = allAudios,allArtists = allArtists,allAlbums = allAlbums)
    }
*/

    /*fun getAllAudioFiles(){
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
                val musicMode = MediaModel().apply {
                    this.album = album
                    this.name = title
                    this.realPath = data
                    this.artist = artist
                }

                val musicFile = MusicModel(album, title, duration, data, artist)

                try {
                    mDuration = metaData(duration.toInt())
                }catch (e: java.lang.Exception){
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
*/

    fun getLocalImagePaths() : ArrayList<Uri> {
        val result = ArrayList<Uri>()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        context.contentResolver.query(uri, projection, null, null, null)?.use {
            while (it.moveToNext()) {
                result.add(
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        it.getLong(0)
                    )
                )
            }
        }
        Log.e("images_data", "getLocalImagePaths: ${result.size}", )
        return  result
    }

    private fun nameFound(name: String): Boolean {
        return !name.contains("unknown", ignoreCase = true)
    }

    fun getAllAudioFolders(allAudios: ArrayList<MediaModel>): ArrayList<Folder> {
        val audioFolders = ArrayList<Folder>()

        allAudios.forEach { media ->
            val file = File(media.realPath)
            file.let { mainFile ->
                if (mainFile.exists()) {
                    val parentFile = file.parentFile
                    parentFile?.let { fileParent ->
                        if (fileParent.exists()) {
                            val folderPosition = getMySelectedFolder(fileParent, audioFolders)
                            if (folderPosition == -1)
                                audioFolders.add(createNewFolder(parentFile, media))
                            else
                                audioFolders[folderPosition].audioFiles.add(media)
                        }
                    }
                }
            }
        }
        return audioFolders
    }

    private fun getMySelectedFolder(parentFile: File, allFolders: ArrayList<Folder>): Int {
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

    private fun createNewFolder(parentFile: File, mediaData: MediaModel): Folder {
        return Folder(
            name = parentFile.name,
            path = parentFile.absolutePath,
            videoFiles = ArrayList(),
            audioFiles = arrayListOf(mediaData)
        )


    }

}