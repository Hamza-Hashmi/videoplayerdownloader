package com.example.videoplayer_and_downloader.UI.activites

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.InterstitialAdUpdated
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.preLoadedNativeAd
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.setNativeAd
import com.example.videoplayer_and_downloader.databinding.ActivitySplashBinding
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import com.example.videoplayer_and_downloader.viewModels.DatabaseViewModel
import com.example.videoplayer_and_downloader.viewModels.VideosDataViewModel
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var TAG:String="SplashActivity"

    val REQUEST_CODE_PERMISSIONS = 10
    private  val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    // This is an array of all the permission specified in the manifest
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val REQUIRED_PERMISSIONS_Gallery = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)



    private var interstitialAd: Any? =null
    var isAutoAdsRemoved = false

    private val sharedPreferenceData: SharedPreferenceData by inject()
    private var itemClickCount: Int=0

    val audioSongsDataViewModel: AudioSongsViewModel by viewModel()
    val myVideosDataViewModel: VideosDataViewModel by viewModel()
    val databaseViewModel: DatabaseViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()


        if (isInternetConnected() &&!isAutoAdsRemoved )
        {
            binding.main.visibility= View.GONE
            binding.adCard.visibility= View.VISIBLE
            setNativeAd(
                binding.shimmerViewContainer,
                binding.refadslayout,
                binding.refadslayoutFb,
                binding.adCard,
                R.layout.native_main_splash_ad,
                R.layout.fb_main_native_splash_ad,
                isAlreadyPurchased(),
                "PlaylistScreenNative",
                ad_periority,
                preLoadedNativeAd,
                getString(R.string.native_id)
            )}else{
            binding.main.visibility= View.VISIBLE
            binding.adCard.visibility= View.GONE
        }

        if (allPermissionsGranted())
        {

            loadStorageData()

        }
        else {

            GlobalScope.launch(context = Dispatchers.Main) {
                Log.e(TAG, "onCreate: waiting ", )
                delay(7000L)
                binding.nextBtn.visibility= View.VISIBLE
            }


        }


        binding.nextBtn.setOnClickListener {
            if (allPermissionsGranted()) {
                if (!isAlreadyPurchased()) {
                    InterstitialAdUpdated.getInstance().showInterstitialAdNew(this@SplashActivity) {
                        navigateToNextScreen(MainActivity::class.java)
                    }

                } else {

                    navigateToNextScreen(MainActivity::class.java)
                }
            }else{
                gotoPermissionScreen()
            }
        }
    }

    private fun gotoPermissionScreen()
    {
        if (!isAlreadyPurchased()) {
            InterstitialAdUpdated.getInstance().showInterstitialAdNew(this@SplashActivity){
                navigateToNextScreen(PermissionActivity::class.java)
            }

        }else{

            navigateToNextScreen(PermissionActivity::class.java)
        }
    }

    private fun loadStorageData() {

        CoroutineScope(Dispatchers.IO).launch {
            Log.e(TAG, "loadStorageData: first")
            audioSongsDataViewModel.getAllMusics()

        }.invokeOnCompletion {

            CoroutineScope(Dispatchers.IO).launch {

                myVideosDataViewModel.getAllFolders()

            }.invokeOnCompletion {
                CoroutineScope(Dispatchers.IO).launch {
                    myVideosDataViewModel.getVideoFolderData()

                }.invokeOnCompletion {

                    CoroutineScope(Dispatchers.IO).launch {
                        databaseViewModel.addMainPlaylistFavourite(favorites, 0, true)

                    }.invokeOnCompletion {

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(6000L)
                            binding.nextBtn.visibility = View.VISIBLE
                            Log.e(TAG, "loadStorageData: ${isAlreadyPurchased()}",)

                        }
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS_Gallery.all {

        ContextCompat.checkSelfPermission(this, it)  == PackageManager.PERMISSION_GRANTED
    }
}