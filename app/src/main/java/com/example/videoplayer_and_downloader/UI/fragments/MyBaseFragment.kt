package com.example.videoplayer_and_downloader.UI.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.videoplayer_and_downloader.*
import com.example.videoplayer_and_downloader.UI.activites.AddPlayListItemActivity
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.listeners.onFolderClick
import com.example.videoplayer_and_downloader.listeners.onMediaClick
import com.example.videoplayer_and_downloader.models.CustomFiles
import com.example.videoplayer_and_downloader.models.Folder
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import com.example.videoplayer_and_downloader.viewModels.DatabaseViewModel
import com.example.videoplayer_and_downloader.viewModels.VideosDataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File

abstract class MyBaseFragment<VB : ViewBinding>(val bindingInflater: (LayoutInflater) -> VB) :
    Fragment() {


    private  val TAG = "AppBaseFragment"


    val myVideosDataViewModel: VideosDataViewModel by sharedViewModel()
    private var _binding: ViewBinding? = null
  val audioSongsDataViewModel: AudioSongsViewModel by sharedViewModel()
   val databaseViewModel: DatabaseViewModel by sharedViewModel()
    private val sharedPreferenceData: SharedPreferenceData by inject()


    @Suppress("UNCHECKED_CAST")
    protected val binding: VB get() = _binding as VB


    public val listLayoutManager: LinearLayoutManager
        get() {
            return LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

    public val gridLayoutManager: GridLayoutManager
        get() {
            return GridLayoutManager(context, 2)
        }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(layoutInflater)
        return requireNotNull(_binding).root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setListeners()
        setObservers()
        loadData()
    }

    abstract fun initViews()

    open fun setListeners(){}

    open fun setObservers(){}
    open fun loadData(){}



    fun getVideoClickInterface(): onMediaClick {
        Log.e(TAG, "getVideoClickInterface:  call ", )
        return object : onMediaClick {
            override fun onMediaPlayed(
                position: Int,
                media: MediaModel,
                info: SelectedListInfo
            ) {

                val f = File(media.realPath)
                context?.let {


                    my_listType = history
                    if (f.exists()) {
                        openPlayerActivity(
                            context = it,
                            position = position,
                            info = info,
                            realPath = media.realPath
                        )
                    } else {
                        showToast(it, R.string.error_files_missing)
                    }

                }

            }

            override fun onMediaAddedToPlaylist(position: Int, media: MediaModel) {

                Log.e(TAG, "onMediaAddedToPlaylist: ", )
                addOnFavourite(media)

            }

            override fun onMediaRemovedFromPlaylist(
                position: Int,
                media: MediaModel,
                selectedListInfo: SelectedListInfo
            ) {

                context?.let { contxt ->

                    var data_return = false
                    CoroutineScope(Dispatchers.Default).launch {
                        data_return = databaseViewModel.deleteVideoFromPlaylist(media)

                    }.invokeOnCompletion {

                        CoroutineScope(Dispatchers.Main).launch {
                            if (data_return) {
                                showToast(contxt, R.string.item_deleted_playlist)
                            } else
                                showToast(contxt, R.string.cannot_delete_video_playlist)
                        }
                    }
                }
            }

            override fun onMediaRemovedFromHistory(
                position: Int,
                media: MediaModel,
                selectedListInfo: SelectedListInfo
            ) {

                context?.let { contxt ->

                    var mDataReturn = false

                    CoroutineScope(Dispatchers.Default).launch {
                        mDataReturn = databaseViewModel.deleteVideoFromHistory(media)

                    }.invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {

                            if (mDataReturn)
                                showToast(contxt, R.string.item_deleted_history)
                            else
                                showToast(contxt, R.string.cannot_delete_video_history)
                        }
                    }
                }
            }

            override fun onMediaAddedToFavorites(position: Int, media: MediaModel) {


                context?.let { contxt ->
                    var returnType = 0

                    CoroutineScope(Dispatchers.Default).launch {

                        returnType = databaseViewModel.addToFavorites(media)

                    }.invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {
                            when (returnType) {
                                1 -> {
                                    CoroutineScope(Dispatchers.Default).launch {
                                        databaseViewModel.getPlaylist()
                                    }.invokeOnCompletion {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            showToast(contxt, R.string.media_added_to_favorites)
                                        }
                                    }
                                }
                                0 -> showToast(contxt, R.string.media_already_exists_favorites)
                                -1 -> showToast(contxt, R.string.media_failed_to_add_favorites)
                            }

                        }
                    }
                } ?: run {
                    activity?.let { showToast(it, R.string.error_message) }
                }


            }

            override fun onMediaRemovedFromFavorites(position: Int, media: MediaModel) {

                context?.let { contxt ->
                    var data_return = false

                    CoroutineScope(Dispatchers.Default).launch {
                        data_return = databaseViewModel.deleteFavorite(media)

                    }.invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (data_return)
                                showToast(contxt, R.string.item_deleted_favorites)
                            else
                                showToast(contxt, R.string.cannot_delete_video_favorites)
                        }
                    }
                }
            }

            override fun onMediaRename(
                position: Int,
                media: MediaModel,
                selectedListInfo: SelectedListInfo
            ) {

                context?.let {

                    val renameDialog =
                        RenameDialog(
                            it,
                            CustomFiles(
                                name = media.name,
                                realPath = media.realPath,
                                isVideo = media.isVideo,
                                uri = media.uri
                            ),
                            object : RenameDialog.IRenamed {
                                override fun onRenamed(existingFile: CustomFiles, newFile: CustomFiles) {
                                    myVideosDataViewModel.updateMedia(
                                        existingFile = existingFile,
                                        newFile = newFile
                                    ) {
                                        myVideosDataViewModel.getUpdatedData()
                                    }
                                }

                            }
                        )
                    renameDialog.show()
                }

            }

            override fun onMediaDelete(
                position: Int,
                media: MediaModel,
                selectedListInfo: SelectedListInfo
            ) {

                context?.let {



                    it.deleteFileFromStorage(media,{singleMedia->
                        myVideosDataViewModel.deleteMedia(singleMedia)
                    })

                }
            }

            override fun onMediaShare(position: Int, media: MediaModel) {
                context?.let {
                    shareMedia(it, media)
                }
            }

            override fun onAlbumClick(albumName: String, info: SelectedListInfo) {
                super.onAlbumClick(albumName, info)
                my_listType = info.listType
                selectedData = albumName
            //    startActivity(Intent(activity, AllAudioSongsActivity::class.java))
            }

            override fun onArtistClick(artistName: String, info: SelectedListInfo) {
                super.onArtistClick(artistName, info)
                my_listType=info.listType
                selectedData=artistName

            }
        }
    }

    fun addOnFavourite(media: MediaModel){
        Log.e(TAG, "addOnFavourite: ", )
        val intent = Intent(context, AddPlayListItemActivity::class.java)
        intent.putExtra(MediaModel::class.java.simpleName, media)
        resultLauncher.launch(intent)
    }

   val deleteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { uri ->
                    context?.let {
                        val path: String = MyFilePaths.getPath(it, uri)
                        Log.d("deletedUri3", path)
                        val deleteDialog = DeleteDialog(
                            context = it,
                            newUri = uri
                        ) {
                            Log.d("deletedUri4", path)
                            myVideosDataViewModel.deleteMedia(path)
                        }
                        deleteDialog.show()
                    }

                    context?.let {
                        it.deleteFileFromStorage(uri) { path ->
                            myVideosDataViewModel.deleteMedia(path)
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

  fun getFolderClickInterface():onFolderClick{
        return object :onFolderClick{
            override fun onItemClick(position: Int, currentFolder: Folder) {
                Log.e(TAG, "onItemClick: position ${position}", )
                Log.e(TAG, "onItemClick: currentFolder ${currentFolder}", )

                my_listType = deviceItemVideo
                val info = SelectedListInfo(
                    listType = deviceItemVideo,

                    selectedParent = currentFolder.name,
                    true
                )
                context?.let { openAnotherrOtherActivity(it, info) }
            }


        }

    }
}