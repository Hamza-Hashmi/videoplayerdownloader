package com.example.videoplayer_and_downloader.models

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.example.videoplayer_and_downloader.Utils.allPermissionsGranted
import com.example.videoplayer_and_downloader.Utils.androidVersionIs10OrAbove
import com.example.videoplayer_and_downloader.Utils.showToast
import java.io.File

class MyFileUtils(var TAG:String?="File_Util", private var context: Context) {
    fun renameFile(realPath: String, newName: String): File? {
        Log.e(TAG, "renameFile: ${realPath}  newName  ${newName}", )
        val oldFile = File(realPath)
        return if (context.allPermissionsGranted()) {
            oldFile.apply {

                return if (exists()) {
                    parentFile?.let {
                        if (it.exists()) {
                            val newFile = File(it, newName)
                            if (!newFile.exists()) {
                                if (renameTo(newFile)) {
                                    addMedia(context, newFile)
                                    removeMedia(context, oldFile)
                                    renamedSuccessfully()
                                    newFile
                                } else {
                                    renameFailed(1)
                                }
                            } else {
                                fileExists(1)
                            }
                        } else
                            renameFailed(2)
                    } ?: renameFailed(3)
                } else
                    renameFailed(4)

            }
        }
        else
            renameFailed(6)

    }

    private fun removeMedia(c: Context, f: File) {
        MediaScannerConnection.scanFile(c, arrayOf(f.toString()), null, null)
    }

    private fun addMedia(c: Context, f: File?) {
        MediaScannerConnection.scanFile(c, arrayOf(f.toString()), null, null)
    }


    fun deleteFile(media: MediaModel? = null, uri: Uri? = null): Boolean {


        return if (androidVersionIs10OrAbove()) {
            uri?.let {
                deleteFileByCursor(it, context)
            } ?: deleteFailed(6)
        } else {
            media?.let { item ->
                val file = File(item.realPath)
                if (file.exists()) {
                    if (file.delete())
                        deletedSuccessfully()
                    else
                        deleteFailed(9)
                } else
                    deleteFailed(8)
            } ?: deleteFailed(7)
        }
    }


    private fun renamedSuccessfully(): Boolean {
        showToast(context, "File Renamed Successfully")
        return true
    }

    private fun renameFailed(from: Int): File? {
        Log.d("renameError", from.toString())
        showToast(context, "Rename failed")
        return null
    }

    @Suppress("SameParameterValue")
    private fun fileExists(from: Int): File? {
        Log.d("file_Exist_Error", from.toString())
        showToast(context, "File Already exists")
        return null
    }

    private fun deleteFailed(from: Int): Boolean {
        Log.d("deleteError", from.toString())
        showToast(context, "Delete failed")
        return false
    }

    private fun deletedSuccessfully(): Boolean {
        showToast(context, "File deleted successfully")
        return true
    }

    private fun deleteFileByCursor(uri: Uri, context: Context): Boolean {
        return try {
            val srcDoc = DocumentFile.fromSingleUri(context, uri)
            srcDoc?.let { df ->
                if (df.delete())
                    deletedSuccessfully()
                else
                    deleteFailed(5)
            } ?: deleteFailed(10)
        } catch (e: Exception) {
            deleteFailed(4)
        }
    }

}