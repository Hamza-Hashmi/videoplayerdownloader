package com.example.videoplayer_and_downloader.UI.activites

import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager.widget.ViewPager
import com.example.videodownload.adapters.DownloadVideosDashBoardViewPagerAdapter
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.DownloadVideosDashboardActivity
import com.example.videoplayer_and_downloader.TrendingVideos.TrendingActivity
import com.example.videoplayer_and_downloader.UI.fragments.ExitDialog
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.Utils.Constants.Companion.israting
import com.example.videoplayer_and_downloader.adapters.MainActiviytViewPaggerAdapter
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.ExistloadPrenativeAd
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.getInterstitialAdObject
import com.example.videoplayer_and_downloader.databinding.ActivityMainBinding
import com.example.videoplayer_and_downloader.listeners.MainFragmentFactory
import com.facebook.ads.InterstitialAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    val sharedPreferenceData: SharedPreferenceData by inject()
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentFactory = MainFragmentFactory()

       val mainAdapter = MainActiviytViewPaggerAdapter(supportFragmentManager, fragmentFactory)

        binding.viewPager.adapter = mainAdapter
        binding.viewPager.offscreenPageLimit = 3

  /*      binding.bttmNav.menu.getItem(2).isEnabled = true;
  *///      binding.viewPager.addOnPageChangeListener(onPageChangeListener)
        binding.viewPager.setPagingEnabled(false)
    /*    binding.bttmNav.setOnNavigationItemSelectedListener(
            itemSelectedListener
        )
*/
        binding.apply {
           btnHome.setOnClickListener {

                viewPager.currentItem = 0

               btnBtmTrending.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
               btnBtmSites.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
               btnBtmMusic.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
               btnBtmDownloads.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
            }

            ivMenu.setOnClickListener {
                openDrawer()
            }

            btnBtmSites.setOnClickListener {
                btnBtmTrending.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmSites.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.pinkColor))
                btnBtmMusic.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmDownloads.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                startActivity(Intent(this@MainActivity, DownloadVideosDashboardActivity::class.java))

            }

            btnBtmTrending.setOnClickListener {
                btnBtmTrending.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.pinkColor))
                btnBtmSites.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmDownloads.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmMusic.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                startActivity(Intent(this@MainActivity,TrendingActivity::class.java))

            }
            btnBtmMusic.setOnClickListener {
                btnBtmMusic.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.pinkColor))
                btnBtmTrending.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmSites.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmDownloads.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                binding.viewPager.currentItem = 1
            }
            btnBtmDownloads.setOnClickListener {
                btnBtmDownloads.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.pinkColor))
                btnBtmMusic.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmTrending.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmSites.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                binding.viewPager.currentItem = 2
            }

        }

        ExistloadPrenativeAd(isAutoAdsRemoved= isAlreadyPurchased(),TAG=TAG+" Exit_Native Ad",onLoadNativeAd= {
            bottomSheetNative=it
            Log.e("TAG", "onCreate: it ${it.toString()}" )

        })

        setUpNavigationView()
        loadInterstitial()

    }

  /*  private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            binding.bttmNav.getMenu().getItem(position).setChecked(true)
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

  */  private val itemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.sites -> {
                    //binding.viewPager.currentItem = 0
                }

                R.id.music -> {

                }
                R.id.downloads ->{

                }
                R.id.trending ->{
                }
                /*R.id.downloadsFragment2 ->{
                    binding.viewPager.currentItem = 3
                }
*/
                else -> binding.viewPager.currentItem = 0
            }
            true
        }

    var fbIntID: String = ""
    var admobIntID: String = ""

    var interstitialAd: Any? = null

    private fun loadInterstitial() {
        admobIntID = getString(R.string.inter_id)
        fbIntID = getString(R.string.fb_inter_id)

        getInterstitialAdObject("DrawerInterstitial", admobIntID, fbIntID,
            ad_periority, { adObject ->
                adObject?.let {
                    interstitialAd = adObject
                }
            },
            onAdClosed ={
                // onAdClosed
                loadInterstitial()
            }, onAdClicked = {
                // onAdClicked
            })
    }


    private fun showInterstitial() {
        /* itemClickCount +=1
        if (itemClickCount > 2) {
            itemClickCount = 1*/
        interstitialAd.let {
            if (it == null) {
                loadInterstitial()
            } else {
                if (it is com.google.android.gms.ads.interstitial.InterstitialAd) {
                    it.show(this)
                } else if (it is InterstitialAd && it.isAdLoaded) {
                    it.show()
                } else {
                    loadInterstitial()
                }
            }
        }
        /*}*/
    }

    private fun setUpNavigationView() {
        binding.navigationView.itemIconTintList = null
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_privacyPolicy -> {
                    closeDrawer()
                    gotoPrivacy("https://sites.google.com/view/satellitefinderandbubblelevelp/home")
                }
                R.id.nav_rateus -> {
                    closeDrawer()
                    rateUs()
                }
                R.id.nav_share->{
                    closeDrawer()
                    shareApp(resources.getString(R.string.app_name))
                }
                R.id.nav_home ->{
                    closeDrawer()
                }
                R.id.nav_trendingVideos ->{
                    showInterstitial()
                    startActivity(Intent(this@MainActivity, TrendingActivity::class.java))
                    closeDrawer()
                }
                R.id.nav_videoDownloader ->{
                    startActivity(Intent(this@MainActivity, DownloadVideosDashboardActivity::class.java))
                    closeDrawer()
                }
                R.id.nav_audioSongs ->{
                    closeDrawer()
                    binding.viewPager.currentItem = 1
                }
                R.id.nav_downlaods->{
                    closeDrawer()
                    binding.viewPager.currentItem = 2

                }
            }
            false
        }
    }

    private fun closeDrawer(){
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun openDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    override fun onBackPressed() {
        //super.onBackPressed()
        if (binding.viewPager.currentItem == 0) {

            //if(navController.currentDestination?.id == R.id.fbFragment2){
            Log.e("TAG", "onBackPressed: Home  ")
           // if (sharedPreferenceData.getBoolean(israting, false)) {
               // if (bottomSheetNative != null) {
                    supportFragmentManager.let {
                        ExitDialog.newInstance(Bundle()).apply {
                            show(it, tag)
                        }
                    }
                /*} else {

                    showExitDialog {
                        if(it){
                            finish()
                        }
                    }
                }*/
    //        }
    /*else {
                val rattingDailog = RattingDailog(this, sharedPreferenceData)
//                onRateUsDailog()
                rattingDailog.show()

            }*/
        } else {
            Log.e("TAG", "onBackPressed: ")
            //   onBackPressed()
            binding.apply {
                btnBtmTrending.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmSites.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmMusic.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                btnBtmDownloads.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
            }

            binding.viewPager.setCurrentItem(0)
        }

    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            btnBtmTrending.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
            btnBtmSites.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
            btnBtmMusic.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
            btnBtmDownloads.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
        }

        binding.viewPager.currentItem = 0


    }

}
