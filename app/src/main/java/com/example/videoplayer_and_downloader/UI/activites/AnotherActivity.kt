package com.example.videoplayer_and_downloader.UI.activites

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adapters.AnotherHistoryAdapter
import com.example.videoplayer_and_downloader.adapters.MainVideoListAdapter
import com.example.videoplayer_and_downloader.databinding.ActivityAnotherBinding
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo

class AnotherActivity : Base() {
    var playlistClickCounter = 1
    var historyClickCounter = 1
    private  val TAG = "OtherActivity"
    private val listLayoutManager: LinearLayoutManager
        get() {
            return LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }



    private lateinit var mainVideoListAdapter: MainVideoListAdapter
    private lateinit var binding: ActivityAnotherBinding
    private var info: SelectedListInfo? = null


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        initData(my_listType)

        doInit()
        initRecyclerView()




        initObservers()

        initClickListeners()


    }


    private fun showPlayListAd() {
        if (playlistClickCounter < 2)
            playlistClickCounter++
        else {
            playlistClickCounter = 0
         //   showAdmobInterstitial()
        }
    }

    private fun showHistoryAd() {
        if (historyClickCounter < 1)
            historyClickCounter++
        else {
            historyClickCounter = 0
           // showAdmobInterstitial()
        }
    }


    private fun initClickListeners() {


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initObservers() {
        info?.let {
            Log.e(TAG, "initObservers: listpe ${it.listType}", )

            when (it.listType) {
                               deviceItemVideo -> {

                    Log.e(TAG, "initObservers: listpe ${it.listType}", )
                    myVideosDataViewModel.videoFolderData.observe(this) { folderList ->

                        Log.e(TAG, "initObservers: data ${folderList.size}",)
                        val selectedFolder = getSelectedFolder(folderList, it)
                        selectedFolder?.let { finalSelectedFolder ->
                            if (finalSelectedFolder.name == "0")
                                binding.headingTv.text = getString(R.string.root)
                            else
                                binding.headingTv.text = finalSelectedFolder.name

//                            videoAdaptor.updateList(
//                                finalSelectedFolder.videoFiles,
//                                finalSelectedFolder.name,
//                                !sharedPreferences.getSubscriptionCheck()
//                            )


                            if (finalSelectedFolder.videoFiles.isEmpty()) {
                                Log.e(
                                    TAG,
                                    "initObservers: size emty ${finalSelectedFolder.videoFiles}"
                                )
                                //binding.videosLargeNativeAd.visibility=View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                                binding.ivNoMedia.visibility = View.VISIBLE
                            } else {
                                Log.e(TAG, "initObservers: size  ${finalSelectedFolder.videoFiles}")

                                //  binding.videosLargeNativeAd.visibility=View.GONE

                                  mainVideoListAdapter.setData(finalSelectedFolder.videoFiles as MutableList<MediaModel>,isNetworkConnected())

                                mainVideoListAdapter.notifyDataSetChanged()
                            }
                        }

                    }


                               }


                else -> {

                }
            }
        }
    }

    private fun showNoMedia(show: Boolean) {
        if (show) {
            binding.recyclerView.visibility = View.GONE
            binding.ivNoMedia.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.ivNoMedia.visibility = View.GONE
        }
    }


    private fun doInit() {
        binding = ActivityAnotherBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.headerLayoutOther..menuIcon.setImageResource(R.drawable.ic_back_main)
        info = intent.getParcelableExtra(otherActivity)
    }

    private fun initRecyclerView() {
        info?.let {


            when (it.listType) {
                deviceItemVideo -> {

                    mainVideoListAdapter = MainVideoListAdapter(mainVideoClickInterface,getString(R.string.native_id), getString(R.string.fb_native_ad), keyPriority =  ad_periority)
                    binding.recyclerView.layoutManager = listLayoutManager
                    binding.recyclerView.adapter = mainVideoListAdapter
                }

            }

        }

        fun showAdIfRequire(listType: String) {
            when (listType) {
                deviceItemVideo, deviceItemAudioFolder, deviceItemAudioArtist, deviceItemAudioAlbum -> {
                }
                playlist -> {
                    showPlayListAd()
                }
                history -> {
                    showHistoryAd()
                }
            }
        }


    }
}