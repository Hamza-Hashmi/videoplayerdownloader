package com.example.videoplayer_and_downloader.DataSource

import android.net.Uri
import android.util.Log
import com.example.videoplayer_and_downloader.models.*
import java.io.File

class VideosDataRepository(val storageDataHelper:StorageMediaHelper) {

    private val myTag="FolderDataRepository"

    val allFolders = ArrayList<Folder>()
    val allImagesFolder = ArrayList<ImagesFolder>()
    var allVideos = ArrayList<MediaModel>()
    var allVideosSongs = ArrayList<MediaModel>()
    val allAudios = ArrayList<MediaModel>()
    val videoFolders = ArrayList<Folder>()
    var allImagesData = ArrayList<Uri>()




    fun getAllImagesFromDevice()
    {
        allImagesData.clear()
        allImagesData=storageDataHelper.getLocalImagePaths()
        Log.e("images_data", "getAllImagesFromDevice: images ${allImagesData.size} ", )

        storeImagesInFolders(allImagesData)


    }

    fun getDataVideoFolder():ArrayList<Folder>
    {
        allVideosSongs.clear()
        allVideos.clear()
        allVideos=storageDataHelper.getVideoMedia()

        Log.e("allVideos", "getDataVideoFolder:  allVideos_data ${allVideosSongs.size}", )

        allVideosSongs=allVideos
        Log.e("allVideos", "getDataVideoFolder:  data ${allVideosSongs.size}", )
        return storeFilesInFolders(allVideos)
    }


    fun getFolderMediaVideo():ArrayList<Folder>
    {

        return getMediaDataVideo(allFolders)
    }



    fun getMediaData() {
        videoFolders.clear()
        Log.e(myTag, "getMediaData:  allFolders  sise ${allFolders}", )
        allFolders.forEachIndexed { _, folder ->
            val folderFile = File(folder.path)
            if (folderFile.exists()) {
                if (folder.videoFiles.size > 0) {
                    videoFolders.add(folder)
                }
//                if (folder.audioFiles.size > 0)
//                    audioFolders.add(folder)
            }
        }

    }

    private fun storeImagesInFolders( allImages:ArrayList<Uri>):ArrayList<ImagesFolder> {

        allImages.forEach { uri ->

            val file = File(uri.path)

            file.let { mainFile ->
                if (mainFile.exists()) {
                    val parentFile = file.parentFile
                    parentFile?.let { fileParent ->
                        if (fileParent.exists()) {
                            Log.e("images_data", "Making iamges Folders ... ${allImagesFolder.size}")
                            val folderPosition = getSelectedFolder(fileParent)
                            if (folderPosition == -1)
                                allImagesFolder.add(createNewImagesFolder(parentFile, uri))
                            else
                                allImagesFolder[folderPosition].imageslist.add(uri)
                        }
                    }
                }
            }
        }

        Log.e("images_data", "storeImagesInFolders:  size ${allImagesFolder.size}", )

        return allImagesFolder
    }


    private fun storeFilesInFolders( allVideos:ArrayList<MediaModel>):ArrayList<Folder> {
        allVideos.forEach { media ->
            val file = File(media.realPath)
            file.let { mainFile ->
                if (mainFile.exists()) {
                    val parentFile = file.parentFile
                    parentFile?.let { fileParent ->
                        if (fileParent.exists()) {
                            val folderPosition = getSelectedFolder(fileParent)
                            if (folderPosition == -1)
                                allFolders.add(createNewFolder(parentFile, media))
                            else
                                allFolders[folderPosition].videoFiles.add(media)
                        }
                    }
                }
            }
        }

        return allFolders

    }

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

    private fun createNewImagesFolder(parentFile: File, media: Uri): ImagesFolder {
        return   ImagesFolder(
            folderName = parentFile.name,
            folderPath = parentFile.absolutePath,

            imageslist = arrayListOf(media)
        )

    }

    private fun getSelectedFolder(parentFile: File): Int {
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


    fun getMediaDataVideo(allFolders:ArrayList<Folder>):ArrayList<Folder> {

        allFolders.forEachIndexed { _, folder ->
            val folderFile = File(folder.path)
            if (folderFile.exists()) {
                if (folder.videoFiles.size > 0) {
                    videoFolders.add(folder)
                }

            }
        }

        return videoFolders
    }



    fun updateMediaInData(existingFile: CustomFiles, newFile: CustomFiles) {
        if (existingFile.isVideo) {
            allVideos.find { media ->
                media.realPath == existingFile.realPath
            }?.updateNameAndPath(name = newFile.name, path = newFile.realPath)
        } else {
        }
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
        if (existingFile.isVideo) {
            for (i in 0 until allVideos.size) {
                if (allVideos[i].realPath == existingFile.realPath) {
                    allVideos.removeAt(i)
                    break
                }
            }
        } else {
            for (i in 0 until allAudios.size) {
                if (allAudios[i].realPath == existingFile.realPath) {
                    allAudios.removeAt(i)
                    break
                }
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
        for (i in 0 until allVideos.size) {
            if (allVideos[i].realPath == deletedUri) {
                allVideos.removeAt(i)
                break
            }
        }

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

}