package com.example.videoplayer_and_downloader.UI.activites

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.ProgressDialog
import com.example.videoplayer_and_downloader.Utils.REQUIRED_PERMISSIONS_Gallery
import com.example.videoplayer_and_downloader.Utils.allPermissionsGranted
import com.example.videoplayer_and_downloader.Utils.favorites
import com.example.videoplayer_and_downloader.databinding.ActivityPermissionBinding
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import com.example.videoplayer_and_downloader.viewModels.DatabaseViewModel
import com.example.videoplayer_and_downloader.viewModels.VideosDataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PermissionActivity : AppCompatActivity() {


    private  val TAG = "AppPermissionActivity"
    private lateinit var binding: ActivityPermissionBinding
    val databaseViewModel: DatabaseViewModel by viewModel()
    val audioSongsDataViewModel: AudioSongsViewModel by viewModel()
    val myVideosDataViewModel: VideosDataViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnAllowAccess.setOnClickListener {

            if (allPermissionsGranted()) {
                loadStorageData()
            } else {
                requestMultiplePermissions()
            }


        }

    }


    private fun requestMultiplePermissions() {

        permReqLuncher.launch(REQUIRED_PERMISSIONS_Gallery)
    }

    val permReqLuncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->

        permissions.entries.forEach {
            Log.e(TAG, "${it.key} = ${it.value}")
        }
        val granted = permissions.entries.all {
            it.value == true
        }

        if (granted) {
            loadStorageData()
            // Good pass
        } else {
//            requestMultiplePermissions()
            // Failed pass
        }
    }


    private fun loadStorageData() {

        progressDialog = ProgressDialog(this)
        progressDialog.showWithMessage(getString(R.string.loading_wait))

        CoroutineScope(Dispatchers.IO).launch{

            myVideosDataViewModel.getAllFolders()
        }.invokeOnCompletion {

            CoroutineScope(Dispatchers.IO).launch{

                myVideosDataViewModel.getVideoFolderData()

            }.invokeOnCompletion {
                CoroutineScope(Dispatchers.IO).launch{
                    audioSongsDataViewModel.getAllMusics()
                }.invokeOnCompletion {

                    CoroutineScope(Dispatchers.IO).launch {
                        databaseViewModel.addMainPlaylistFavourite(favorites,0,true)
                    }.invokeOnCompletion {

                        CoroutineScope(Dispatchers.Main).launch{


                            delay(2000)
                            progressDialog.let {
                                if (it.isShowing)
                                    it.dismiss()
                            }


                        }.invokeOnCompletion {

                            startActivity(Intent(this@PermissionActivity,MainActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }

    }
}