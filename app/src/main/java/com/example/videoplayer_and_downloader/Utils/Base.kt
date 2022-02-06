package com.example.videoplayer_and_downloader.Utils

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.MyFilePaths.getPath
import com.example.videoplayer_and_downloader.listeners.onMediaClick
import com.example.videoplayer_and_downloader.models.*
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import com.example.videoplayer_and_downloader.viewModels.DatabaseViewModel
import com.example.videoplayer_and_downloader.viewModels.VideosDataViewModel
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


open class Base : AppCompatActivity() {

    // important instruction
    // rate us ko
    //bad ma isko uncomment krna hai

    val databaseViewModel: DatabaseViewModel by viewModel()
    val myVideosDataViewModel: VideosDataViewModel by viewModel()
    val audioSongsViewModel: AudioSongsViewModel by viewModel()


    //  val billingViewModel: BillingViewModel by inject()

    val mainVideoClickInterface = getVideoClickInterface()
   /* private var rateUsInterface = object : RateUsDialog.IRateUs {
        override fun dialogYesClicked(rating: Int, isExit: Boolean, finishApp: Boolean) {
            if (isExit) {
                finishAffinity()
            } else {
                if (rating == 5) {
                    launchMarket()
                    sharedPreferences.setAlreadyRated()
                } else
                    goToFeedback()
            }
        }

        override fun dialogNoClicked(isExit: Boolean, finishApp: Boolean) {
            if (isExit)
                rateUsDialog.dismiss()
            else {
                if (finishApp)
                    finishAffinity()
                else
                    rateUsDialog.dismiss()
            }
        }

    }
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        sharedPreferences = MySharedPreferences(this)
    }
    //lateinit var rateUsDialog: RateUsDialog
    lateinit var sharedPreferences: MySharedPreferences


    private fun getVideoClickInterface(): onMediaClick {
        return object : onMediaClick {

            override fun onMediaPlayed(
                position: Int,
                singleMedia: MediaModel,
                info: SelectedListInfo
            ) {
                val f = File(singleMedia.realPath)
                if (f.exists()) {
                    my_listType=info.listType
                    openPlayerActivity(
                        context = this@Base,
                        position = position,
                        info = info,
                        realPath = singleMedia.realPath
                    )
                } else {
                    showToast(this@Base, R.string.error_files_missing)
                }
            }

            override fun onMediaAddedToPlaylist(position: Int, singleMedia: MediaModel) {
                openAddPlaylistActivity(this@Base, singleMedia)
            }
            override fun onMediaRemovedFromPlaylist(position: Int, singleMedia: MediaModel,info: SelectedListInfo) {




                    var data_return= false

                CoroutineScope(Dispatchers.Default).launch {
                    data_return= databaseViewModel.deleteVideoFromPlaylist(singleMedia)

                }.invokeOnCompletion {
                    CoroutineScope(Dispatchers.Main).launch {

                        if (data_return)
                            showToast(this@Base, R.string.item_deleted_playlist)
                        else
                            showToast(this@Base, R.string.cannot_delete_video_playlist)
                    }
                }



            }

            override fun onMediaRemovedFromHistory(position: Int, singleMedia: MediaModel,info: SelectedListInfo) {



                var data_return= false

                CoroutineScope(Dispatchers.Default).launch {
                    data_return= databaseViewModel.deleteVideoFromHistory(singleMedia)

                }.invokeOnCompletion {
                    CoroutineScope(Dispatchers.Main).launch {

                        if (data_return)
                            showToast(this@Base, R.string.item_deleted_history)
                        else
                            showToast(this@Base, R.string.cannot_delete_video_history)
                    }
                }

            }

            override fun onMediaAddedToFavorites(position: Int, singleMedia: MediaModel) {

                var returnType=0

                CoroutineScope(Dispatchers.Default).launch {

                    returnType=databaseViewModel.addToFavorites(singleMedia)

                }.invokeOnCompletion {
                    CoroutineScope(Dispatchers.Main).launch {
                        when (returnType) {
                            1 -> {
                                CoroutineScope(Dispatchers.Default).launch {
                                    databaseViewModel.getPlaylist()
                                }.invokeOnCompletion {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        showToast(this@Base, R.string.media_added_to_favorites)
                                    }
                                }
                            }
                            0 -> showToast(this@Base, R.string.media_already_exists_favorites)
                            -1 -> showToast(this@Base, R.string.media_failed_to_add_favorites)
                        }

                    }
                }



            }

            override fun onMediaRemovedFromFavorites(position: Int, singleMedia: MediaModel) {

                var data_return= false

                CoroutineScope(Dispatchers.Default).launch {
                    data_return= databaseViewModel.deleteFavorite(singleMedia)

                }.invokeOnCompletion {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (data_return)
                            showToast(this@Base, R.string.item_deleted_favorites)
                        else
                            showToast(this@Base, R.string.cannot_delete_video_favorites)
                    }
                }

            }

            override fun onMediaRename(position: Int, singleMedia: MediaModel,info: SelectedListInfo) {
                val renameDialog =
                    RenameDialog(
                        this@Base,
                        CustomFiles(
                            name = singleMedia.name,
                            realPath = singleMedia.realPath,
                            isVideo = singleMedia.isVideo,
                            uri = singleMedia.uri
                        ),
                        getRenameInterface()
                    )
                renameDialog.show()

            }

            override fun onMediaDelete(position: Int, singleMedia: MediaModel,info: SelectedListInfo) {
               deleteFileFromStorage(singleMedia,{path->
                    myVideosDataViewModel.deleteMedia(path)
                })
            }

            override fun onMediaShare(position: Int, singleMedia: MediaModel) {
                shareMedia(this@Base, singleMedia)
            }
        }
    }


    private fun getRenameInterface(): RenameDialog.IRenamed {
        return object : RenameDialog.IRenamed {
            override fun onRenamed(existingFile: CustomFiles, newFile: CustomFiles) {

                Log.e("TAG", "onRenamed: existingName ${existingFile.name}", )
                Log.e("TAG", "onRenamed: existingrealPath ${existingFile.realPath}", )
                Log.e("TAG", "onRenamed: existinguri ${existingFile.uri}", )

                Log.e("TAG", "onRenamed: new_Name ${existingFile.name}", )
                Log.e("TAG", "onRenamed: new_realPath ${existingFile.realPath}", )
                Log.e("TAG", "onRenamed: new_uri ${existingFile.uri}", )

                if (existingFile.isVideo) {
                    myVideosDataViewModel.updateMedia(
                        existingFile = existingFile,
                        newFile = newFile
                    ) {


                        CoroutineScope(Dispatchers.IO).launch {
                            myVideosDataViewModel.getUpdatedData()
                        }.invokeOnCompletion {


                         updateVideoHistory(existingFile, newFile){
                             updateVideoPlaylistItem(existingFile,newFile)

                         }


                        }


                    }

                }else{


                    }


                }
            }

        }

    private fun updateVideoPlaylistItem(existingFile: CustomFiles, newFile: CustomFiles) {
        var returnType = 0

        CoroutineScope(Dispatchers.IO).launch {

            returnType = databaseViewModel.updateVideoPlaylisItemById(
                existingFile.realPath,
                newFile.name,
                newFile.realPath,
                newFile.uri
            )
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {

            }
        }

    }

    private fun updateAudioHistory(existingFile: CustomFiles, newFile: CustomFiles,onComplete:(()->Unit)) {
        var returnType = 0
        CoroutineScope(Dispatchers.IO).launch {

            returnType = databaseViewModel.updateAudioHistoryById(
                existingFile.realPath,
                newFile.name,
                newFile.realPath,
                newFile.uri
            )
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                Log.e("TAG", "onRenamed: returnType ${returnType} ",)
                when (returnType) {
                    0 -> {
                        onComplete.invoke()
                    }
                    1 -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            databaseViewModel.getAudioUpdatedData()
                        }.invokeOnCompletion {
                            onComplete.invoke()

                        }
                    }

                }

            }
        }    }

    private fun updateVideoHistory(existingFile: CustomFiles, newFile: CustomFiles,onComplete:(()->Unit)) {
        var returnType = 0
        CoroutineScope(Dispatchers.IO).launch {

            returnType = databaseViewModel.updateVideoHistoryById(
                existingFile.realPath,
                newFile.name,
                newFile.realPath,
                newFile.uri
            )
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                Log.e("TAG", "onRenamed: returnType ${returnType} ",)
                when (returnType) {
                    0 -> {
                        onComplete.invoke()

                    }
                    1 -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            databaseViewModel.getUpdatedData()
                        }.invokeOnCompletion {
                            onComplete.invoke()

                        }
                    }

                }


            }
        }
    }


    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                CoroutineScope(Dispatchers.Default).launch {
                    databaseViewModel.getUpdatedData()
                }
            }
        }


    val resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
               printLog(TAG = "overlaypermission","calll")
            }
        }


    val deleteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { uri ->
                    val path: String = getPath(this, uri)
                    Log.d("deletedUri3", path)
                    val deleteDialog = DeleteDialog(
                        context = this@Base,
                        newUri = uri
                    ) {
                        Log.d("deletedUri4", path)
                        myVideosDataViewModel.deleteMedia(path)
                    }
                    deleteDialog.show()
                }
            }
        }


    fun getSelectedFolder(folderList: ArrayList<Folder>, info: SelectedListInfo): Folder? {
        var folder: Folder? = null
        for (i in 0 until folderList.size) {
            val currentFolder = folderList[i]
            if (currentFolder.name == info.selectedParent) {
                folder = currentFolder
                break
            }
        }
        return folder
    }

    fun getSelectedAlbum(folderList: ArrayList<Albums>, info: SelectedListInfo): Albums? {
        var folder: Albums? = null
        for (i in 0 until folderList.size) {
            val currentFolder = folderList[i]
            if (currentFolder.name == info.selectedParent) {
                folder = currentFolder
                break
            }
        }
        return folder
    }

    fun getSelectedArtist(folderList: ArrayList<Artist>, info: SelectedListInfo): Artist? {
        var folder: Artist? = null
        for (i in 0 until folderList.size) {
            val currentFolder = folderList[i]
            if (currentFolder.name == info.selectedParent) {
                folder = currentFolder
                break
            }
        }
        return folder
    }

    fun initData(listType: String) {
        Log.e("Base Activity ", "initData: ")

        when (listType) {
            history -> {

            }
            deviceItemVideo -> {

                CoroutineScope(Dispatchers.Default).launch {
                    myVideosDataViewModel.getUpdatedData()
                }.invokeOnCompletion {
                    Log.e("TAG", "initData:  ${it?.localizedMessage}")
                    Log.e("TAG", "initData:  ${it?.message}")

                    CoroutineScope(Dispatchers.Main).launch {

                    }

                }


            }
            deviceItemAudioFolder -> {
/*

                CoroutineScope(Dispatchers.Default).launch {
                    audioSongsViewModel.getUpdatedData()
                }.invokeOnCompletion {
                    Log.e("TAG", "initData:  ${it?.localizedMessage}")
                    Log.e("TAG", "initData:  ${it?.message}")

                    CoroutineScope(Dispatchers.Main).launch {

                    }

                }

*/


            }

        }


    }

    //bad ma uncomment krna hai

    /*fun rateUsNow(isExit: Boolean = false, finishApp: Boolean = false) {
        rateUsDialog = RateUsDialog(
            this@Base,
            rateUsInterface,
            isExit1 = isExit,
            finishApp1 = finishApp
        )
        rateUsDialog.show()
    }*/


}