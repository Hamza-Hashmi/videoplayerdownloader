package com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.videodownload.adapters.DownloadVideosDashBoardViewPagerAdapter
import com.example.videodownload.allViewModel.MainViewModel
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.TinyDB
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils.MyDownloadManagerClass
import com.example.videoplayer_and_downloader.databinding.ActivityDownloadVideosDashboardBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DownloadVideosDashboardActivity : AppCompatActivity(){

    val mainViewModel: MainViewModel by viewModel()
    lateinit var dashBoardViewPagerAdapter: DownloadVideosDashBoardViewPagerAdapter
    lateinit var binding : ActivityDownloadVideosDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadVideosDashboardBinding.inflate(layoutInflater)
        dashBoardViewPagerAdapter = DownloadVideosDashBoardViewPagerAdapter(this)
        setViewPager()
        checkInstLoginStatus()
        checkFacebookLogin()
        checkFaceBookWatchLogin()
        checktwitterLogin()
        registerObserver()

        setContentView(binding.root)

    }
    fun setViewPager(){
        binding.dashboardViewPager.adapter = dashBoardViewPagerAdapter
        binding.dashboardViewPager.offscreenPageLimit = 3
        binding.dashboardBottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.download_url -> {
                    binding.dashboardViewPager.currentItem = 0
                    return@setOnItemSelectedListener true
                }
                R.id.video_in_progress -> {
                    binding.dashboardViewPager.currentItem = 1
                    return@setOnItemSelectedListener true
                }
                R.id.video_download -> {
                    binding.dashboardViewPager.currentItem = 2
                    return@setOnItemSelectedListener true
                }
                else -> {
                    binding.dashboardViewPager.currentItem = 0
                }
            }
            false
        }
        binding.dashboardViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.dashboardBottomNavigation.menu.getItem(position).isChecked = true
            }
        })

    }
    fun checkFacebookLogin() {
        try {
            var calender = Calendar.getInstance()
            var date = Date()
            calender.time = date
            var dayOfWeek = calender.get(Calendar.DAY_OF_WEEK)
            val savedDialogDate = TinyDB.getInstance(this).getInt("toDaydateFB")
            if (savedDialogDate < dayOfWeek) {
                TinyDB.getInstance(this).putBoolean("currentDateFB", false)
                var c = Calendar.getInstance()
                var date = Date()
                c.time = date
                var dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
                Log.d("LOG_TAG", "Show In App Dialog : date Is-> $dayOfWeek")
                TinyDB.getInstance(this).putInt("toDaydateFB", dayOfWeek)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checktwitterLogin() {
        try {
            var calender = Calendar.getInstance()
            var date = Date()
            calender.time = date
            var dayOfWeek = calender.get(Calendar.DAY_OF_WEEK)
            val savedDialogDate = TinyDB.getInstance(this).getInt("toDaydatetwitter")
            if (savedDialogDate < dayOfWeek) {
                TinyDB.getInstance(this).putBoolean("currentDatetwitter", false)
                var c = Calendar.getInstance()
                var date = Date()
                c.time = date
                var dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
                Log.d("LOG_TAG", "Show In App Dialog : date Is-> $dayOfWeek")
                TinyDB.getInstance(this).putInt("toDaydatetwitter", dayOfWeek)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkFaceBookWatchLogin() {
        try {
            var calender = Calendar.getInstance()
            var date = Date()
            calender.time = date
            var dayOfWeek = calender.get(Calendar.DAY_OF_WEEK)
            val savedDialogDate = TinyDB.getInstance(this).getInt("toDaydateFBWatch")
            if (savedDialogDate < dayOfWeek) {
                TinyDB.getInstance(this).putBoolean("currentDateFBWatch", false)
                var c = Calendar.getInstance()
                var date = Date()
                c.time = date
                var dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
                Log.d("LOG_TAG", "Show In App Dialog : date Is-> $dayOfWeek")
                TinyDB.getInstance(this).putInt("toDaydateFBWatch", dayOfWeek)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkInstLoginStatus() {
        kotlin.runCatching {
            val calender = Calendar.getInstance()

            calender.time = Date()
            val dayOfWeek = calender.get(Calendar.DAY_OF_WEEK)
            val savedDialogDate = TinyDB.getInstance(this).getInt("toDaydate")
            if (savedDialogDate < dayOfWeek) {
                TinyDB.getInstance(this).putBoolean("currentDate", false)
                val c = Calendar.getInstance()
                c.time = Date()
                val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)

                TinyDB.getInstance(this).putInt("toDaydate", dayOfWeek)
            }
        } .onFailure {
            it.stackTrace
        }
    }

    val permissionlistener: PermissionListener = object : PermissionListener {

        override fun onPermissionGranted() {
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {

            TedPermission.with(this@DownloadVideosDashboardActivity)
                .setPermissionListener(this)
                .setDeniedTitle("Permission denied")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("Setting")
                .setPermissions(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check()
        }
    }
    private fun registerObserver() {

        mainViewModel.openDownloadFragmentLiveData.observe(this) {
            if (it) {

                if (TedPermission.isGranted(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {

                } else {
                    TedPermission.with(this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedTitle("Permission denied")
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setGotoSettingButtonText("Setting")
                        .setPermissions(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        .check()
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        MyDownloadManagerClass.setOnDownloadFinishedListener(null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}