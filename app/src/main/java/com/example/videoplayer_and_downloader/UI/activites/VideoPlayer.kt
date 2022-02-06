package com.example.videoplayer_and_downloader.UI.activites

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adapters.VideoPlayerButtonsAdapter
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo
import com.example.videoplayer_and_downloader.videoPlayerHelper.PlayerActivity
import com.google.android.exoplayer2.MediaItem
import com.example.videoplayer_and_downloader.database.AudioHistory
import com.example.videoplayer_and_downloader.database.History
import com.example.videoplayer_and_downloader.database.PlaylistItem
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.BottomBarItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class VideoPlayer : PlayerActivity() {
    private lateinit var buttonsAdapter: VideoPlayerButtonsAdapter
    private var selectedPos = 0
    private var selectedRealPath: String? = null
    private lateinit var audioService: Intent
    private lateinit var videoService: Intent
    private var playlistToPlay = ArrayList<MediaModel>()
    private var myTitle: String = videos

    private var info: SelectedListInfo? = null
    private lateinit var progressDialog: ProgressDialog
    private lateinit var currentItem: MediaModel
    private var isFvt = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViews() {
        progressDialog = ProgressDialog(this)
        info = intent.getParcelableExtra(playerActivity)
        /*audioService = Intent(this, MediaService::class.java)
        videoService = Intent(this, FloatingService::class.java)*/
        selectedPos = intent.getIntExtra(selectedVideo, 0)
        selectedRealPath = intent.getStringExtra(selectedVideoPath)
        initObservers()
        initData(my_listType)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initObservers() {
        info?.let {
            when (it.listType) {
                history -> {
                    if (it.isVideo) {

                        databaseViewModel.allHistory.observe(this, { historyList ->
                            myTitle = history
                            playlistToPlay = getListFromHistory(historyList)
                            calculatePosAndStartPlayer()
                        })

                        CoroutineScope(Dispatchers.Default).launch {
                            databaseViewModel.getUpdatedData()
                        }.invokeOnCompletion {


                            CoroutineScope(Dispatchers.Main).launch {

                            }

                        }
                    } else {
                        databaseViewModel.allAudioHistory.observe(this, { audioList ->

                            myTitle = videos
                            playlistToPlay = getListFromAudioHistory(audioList)
                            calculatePosAndStartPlayer()
                        })

                        CoroutineScope(Dispatchers.Default).launch {
                            databaseViewModel.getAudioUpdatedData()
                        }.invokeOnCompletion {


                            CoroutineScope(Dispatchers.Main).launch {

                            }

                        }
                    }
                }
                deviceItemVideo -> {
                    if (it.selectedParent == none || it.selectedParent == all) {
                        myVideosDataViewModel.allVideos.observe(this, { videoList ->
                            myTitle = videos
                            playlistToPlay = videoList
                            calculatePosAndStartPlayer()
                        })
                    } else {
                        myVideosDataViewModel.videoFolderData.observe(this, { folderList ->
                            if (folderList.isEmpty()) {
                                myTitle = videos
                            } else {
                                val selectedFolder = getSelectedFolder(folderList, it)
                                selectedFolder?.let { finalSelectedFolder ->
                                    myTitle = if (finalSelectedFolder.name == "0")
                                        getString(R.string.root)
                                    else
                                        finalSelectedFolder.name
                                    playlistToPlay = finalSelectedFolder.videoFiles
                                }
                                calculatePosAndStartPlayer()
                            }
                        })
                    }
                }
                /*deviceItemAudioFolder -> {
                    if (it.selectedParent == none || it.selectedParent == all) {
                        audioSongsViewModel.allAudios.observe(this, { audioList ->
                            myTitle = videos
                            playlistToPlay = audioList
                            calculatePosAndStartPlayer()
                        })
                    } else {
                        audioSongsViewModel.audioFolderData.observe(this, { folderList ->
                            if (folderList.isEmpty()) {
                                myTitle = getString(R.string.songs)
                            } else {
                                val selectedFolder = getSelectedFolder(folderList, it)
                                selectedFolder?.let { finalSelectedFolder ->
                                    myTitle = finalSelectedFolder.name
                                    playlistToPlay = finalSelectedFolder.audioFiles
                                    calculatePosAndStartPlayer()
                                }
                            }
                        })
                    }
                }*/
                playlist -> {
                    if (it.isVideo) {
                        if (it.selectedParent == all || it.selectedParent == none) {
                            myTitle = playlist
                            databaseViewModel.allPlaylistItemVideos.observe(this, { playlistItems ->
                                if (playlistItems.isEmpty())
                                    myTitle = playlist
                                else {
                                    playlistToPlay = getPlaylistList(playlistItems)
                                    calculatePosAndStartPlayer()
                                }
                            })


                            CoroutineScope(Dispatchers.Default).launch {
                                databaseViewModel.getUpdatedData()
                            }.invokeOnCompletion {


                                CoroutineScope(Dispatchers.Main).launch {

                                }

                            }
                        } else {
                            myTitle = it.selectedParent
                            databaseViewModel.allPlaylistByNameVideos.observe(
                                this,
                                { playlistItems ->
                                    if (playlistItems.isEmpty())
                                        myTitle = playlist
                                    else {
                                        myTitle = it.selectedParent
                                        playlistToPlay = getPlaylistList(playlistItems)
                                        calculatePosAndStartPlayer()
                                    }
                                })

                            CoroutineScope(Dispatchers.Default).launch {

                                databaseViewModel.getPlaylistItemsByName(it.selectedParent)
                            }
                        }
                    } else {
                        if (it.selectedParent == all || it.selectedParent == none) {
                            myTitle = playlist
                            databaseViewModel.allPlaylistItemAudios.observe(this, { playlistItems ->
                                if (playlistItems.isEmpty())
                                    myTitle = playlist
                                else {
                                    playlistToPlay = getPlaylistList(playlistItems)
                                    calculatePosAndStartPlayer()
                                }


                            })
                        } else {
                            myTitle = it.selectedParent
                            databaseViewModel.allPlaylistByNameAudios.observe(
                                this,
                                { playlistItems ->
                                    if (playlistItems.isEmpty())
                                        myTitle = playlist
                                    else {
                                        playlistToPlay = getPlaylistList(playlistItems)
                                        calculatePosAndStartPlayer()
                                    }

                                })

                            CoroutineScope(Dispatchers.Default).launch {

                                databaseViewModel.getPlaylistItemsByName(it.selectedParent)
                            }
                        }
                    }
                }
                mediaItemService -> {
                    currentMediaItem?.let { serviceItem ->
                        playlistToPlay = arrayListOf(serviceItem)
                        updateCurrentItem(serviceItem)
                        selectedPos = 0
                        startPlayerNow(false)
                    }
                }
                else -> {
                    if (it.isVideo)
                        myVideosDataViewModel.allVideos.observe(this, { videoList ->
                            myTitle = videos
                            playlistToPlay = videoList
                            calculatePosAndStartPlayer()
                        })
                    else {
                    }

                }
            }
        }
    }

    private fun updateCurrentItem(item: MediaModel) {
        currentItem = item

        CoroutineScope(Dispatchers.Default).launch {

            isFvt = databaseViewModel.isFavorite(currentItem.realPath)

        }
    }

    private fun calculatePosAndStartPlayer() {
        progressDialog.showWithMessage(getString(R.string.loading_media))
        defaultThenMain({
            selectedRealPath?.let { path ->
                for (i in 0 until playlistToPlay.size) {
                    if (playlistToPlay[i].realPath == path) {
                        selectedPos = i
                        break
                    }
                }
            }
        }, {
            progressDialog.dismiss()
            startPlayerNow()
        })


    }

    private fun getPlaylistList(playlistItems: List<PlaylistItem>): java.util.ArrayList<MediaModel> {
        val mediaList = ArrayList<MediaModel>()
        playlistItems.forEach {
            mediaList.add(
                MediaModel(
                    name = it.name,
                    realPath = it.realPath,
                    isVideo = it.isVideo,
                    isFvt = it.isFavorite,
                    isPlaylist = true,
                    artist = it.artist,
                    album = it.album,
                    uri = it.uri,
                    playlistName = it.playlistName
                )
            )
        }
        return mediaList
    }

    private fun getListFromHistory(historyList: ArrayList<History>): java.util.ArrayList<MediaModel> {
        val mediaList = ArrayList<MediaModel>()
        historyList.forEach {
            val item =
                MediaModel(
                    name = it.name,
                    realPath = it.realPath,
                    isVideo = it.isVideo,
                    uri = it.uri
                )
            mediaList.add(item)
        }
        return mediaList
    }

    private fun getListFromAudioHistory(historyList: ArrayList<AudioHistory>): java.util.ArrayList<MediaModel> {
        val mediaList = ArrayList<MediaModel>()
        historyList.forEach {
            val item =
                MediaModel(
                    name = it.name,
                    realPath = it.realPath,
                    isVideo = it.isVideo,
                    uri = it.uri
                )
            mediaList.add(item)
        }
        return mediaList
    }


    override fun getMainList(): MutableList<MediaModel> {
        return playlistToPlay
    }

    override fun updateNotification() {
        if (sharedPreferences.getAudioServiceRunning())
            sendBroadcast(Intent(BROADCAST_RECEIVER).putExtra(BROADCAST_ACTION, ACTION_UPDATE))
    }

    override fun updateLandscapeOrientation(landscape: Boolean, isPlaying: Boolean) {

        CoroutineScope(Dispatchers.Default).launch {

            isFvt = databaseViewModel.isFavorite(currentItem.realPath)
        }

      val layoutManager = if (landscape)
            GridLayoutManager(this@VideoPlayer, buttonsAdapter.getLandscapeList().size)
        else
            GridLayoutManager(this@VideoPlayer, buttonsAdapter.getPortraitList().size)

        player?.let {
            if (getCurrentItem().isUniqueMedia() || getCurrentItem().isPortraitVideo(this) || landscape)
                adHolder.visibility = View.GONE
            else
                adHolder.visibility = View.VISIBLE
        } ?: run { adHolder.visibility = View.GONE }


       rvBottomBar.layoutManager = layoutManager
        buttonsAdapter.updateList(landscape, isPlaying)
    }


    private fun initBottomBarRecyclerView() {
        buttonsAdapter = VideoPlayerButtonsAdapter(getClickInterface(), this)
        rvBottomBar.adapter = buttonsAdapter
        initOrientation()
    }

    private fun getClickInterface(): VideoPlayerButtonsAdapter.VideoPlayerItemClick {
        return object : VideoPlayerButtonsAdapter.VideoPlayerItemClick {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemClick(item: BottomBarItem) {
                when (item.title) {
                    getString(R.string.shuffle_string) -> switchShuffleMode()
                    getString(R.string.repeat) -> switchRepeatMode()
                    getString(R.string.orientation) -> switchOrientation(false)
                    getString(R.string.speed) -> exoSpeed.performClick()
                    getString(R.string.play), getString(R.string.pause) -> exoPlayPause.performClick()
                    getString(R.string.aspect) -> changeAspectRatio()
                    getString(R.string.previous_video) -> exoPrev.performClick()
                    getString(R.string.next_video) -> exoNext.performClick()
                    getString(R.string.next_video) -> changeAspectRatio()
                 /*   getString(R.string.all_list) -> showAllListNow()
                    getString(R.string.mini_player) -> {
                        if (isMiniPlayerSupported(this@VideoPlayer))
                            enterMiniplayer()
                    }
                    getString(R.string.favorite) -> {
                        onAddToFavourite()
                    }
 */               }
            }
        }
    }


    private fun startPlayerNow(newPlayer: Boolean = true) {
        if (sharedPreferences.getVideoServiceRunning())
            stopService(videoService)
        else if (sharedPreferences.getAudioServiceRunning())
            stopService(audioService)


        if (playlistToPlay.size > 0) {
            if (selectedPos >= playlistToPlay.size)
                selectedPos = 0
            if (playlistToPlay[selectedPos].isVideo) {

                sharedPreferences.setDefaultScreenOrientation(resources.getString(R.string.screen_orientation_portrait))

            } else {

                sharedPreferences.setDefaultScreenOrientation(resources.getString(R.string.screen_orientation_landscape))

            }
            updateCurrentItem(playlistToPlay[selectedPos])
        } else
            return

        updateGestureEnabled()
        mPrefs.updateMedia(currentItem, "video/*")


        val flag = when {
            newPlayer -> {
                if (playerIsNull()) {
                    initializePlayer()
                    false
                } else
                    true
            }
            playerIsNull() -> {
                currentMediaItem?.let { serviceItem ->
                    playlistToPlay = arrayListOf(serviceItem)
                    updateCurrentItem(serviceItem)
                    selectedPos = 0
                    initializePlayer()
                }
                false
            }
            else -> {
                true
            }
        }

        initBottomBarRecyclerView()
        if (currentItem.isVideo) {
            sharedPreferences.setDefaultScreenOrientation(resources.getString(R.string.screen_orientation_landscape))

            updateUiOrientation()
        } else {
            updatePortraitUI()
        }

        if (flag) {
            continuePlayer(currentItem)
            updateLandscapeOrientation(
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
                player.isPlaying
            )
        }

        if (currentItem.isVideo) {
            CoroutineScope(Dispatchers.Default).launch {
                databaseViewModel.addHistory(currentItem)
            }.invokeOnCompletion {
                CoroutineScope(Dispatchers.Default).launch {
                }
            }
        } else {

            CoroutineScope(Dispatchers.Default).launch {
                databaseViewModel.addAudioHistory(currentItem)
            }.invokeOnCompletion {
                CoroutineScope(Dispatchers.Default).launch {
                }
            }

            currentMediaItem = currentItem
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ContextCompat.startForegroundService(this, audioService)
            else
                startService(audioService)

            playAsAudio.visibility = View.GONE
            playAsAudio.performClick()
        }
    }



    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.let { bundle ->
                val action = bundle.getString(BROADCAST_ACTION)
                action?.let { action1 ->
                    if (action1 == ACTION_CLOSE_ACTIVITY) {
                        onBackPressed()
                    }
                }
            }
        }
    }


    override fun onBackPressed() {
            releasePlayer()

        super.onBackPressed()
    }

    override fun getPreviousExoItem(): MediaItem {
        if (playlistToPlay.size > 1) {
            if (!player.shuffleModeEnabled) {
                selectedPos--
                if (selectedPos < 0)
                    selectedPos = playlistToPlay.size - 1
            } else
                selectedPos =
                    getRandomWithExclusion(
                        rnd = Random(),
                        0,
                        playlistToPlay.size - 1,
                        selectedPos
                    )
        }
        updateCurrentItem(playlistToPlay[selectedPos])
        return getExoItem(currentItem)
    }

    private fun getRandomWithExclusion(
        rnd: Random,
        @Suppress("SameParameterValue") start: Int,
        end: Int,
        vararg exclude: Int
    ): Int {
        var random: Int = start + rnd.nextInt(end - start + 1 - exclude.size)
        for (ex in exclude) {
            if (random < ex) {
                break
            }
            random++
        }
        return random
    }

    override fun getNextExoItem(): MediaItem {
        if (playlistToPlay.size > 1) {
            if (!player.shuffleModeEnabled) {
                selectedPos++
                if (selectedPos >= playlistToPlay.size)
                    selectedPos = 0
            } else
                selectedPos =
                    getRandomWithExclusion(
                        rnd = Random(),
                        0,
                        playlistToPlay.size - 1,
                        selectedPos
                    )
        }
        updateCurrentItem(playlistToPlay[selectedPos])
        return getExoItem(currentItem)
    }

    override fun shareMyMedia() {
        shareMedia(context = this@VideoPlayer, currentItem)
    }

    override fun showAllSongsList() {

    }

    override fun addIntoFavourite() {
        onAddToFavourite()
    }

    private fun onAddToFavourite() {
        this@VideoPlayer?.let { contxt ->
            var returnType = 0

            CoroutineScope(Dispatchers.Default).launch {

                returnType = databaseViewModel.addToFavorites(currentItem)

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
        }
    }

    override fun mediaIsAudio(): Boolean {
        return !currentItem.isVideo
    }


    override fun getCurrentItem(): MediaModel {
        return try {
            currentItem
        } catch (e: Exception) {
            getDummyMedia()
        }
    }


    override fun startMyFloatingService() {


        currentMediaItem = currentItem
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ContextCompat.startForegroundService(this, videoService)
        else
            startService(videoService)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(BROADCAST_RECEIVER))

    }


    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onPause() {
        super.onPause()
    }
}