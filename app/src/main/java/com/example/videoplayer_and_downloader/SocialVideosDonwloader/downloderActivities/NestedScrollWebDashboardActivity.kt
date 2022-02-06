package com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.example.videodownload.adapters.NestedScrollWebDashboardViewPagerAdapter
import com.example.videodownload.appUtils.Constants
import com.example.videodownload.factories.NestedScrollWebFragmentFactory
import com.example.videoplayer_and_downloader.databinding.ActivityNestedScrollWebDashboardBinding


class NestedScrollWebDashboardActivity : AppCompatActivity(){

    lateinit var binding: ActivityNestedScrollWebDashboardBinding
    lateinit var nestedScrollWebDashboardViewPagerAdapter: NestedScrollWebDashboardViewPagerAdapter
    lateinit var  sitesUrl : String
    lateinit var sitesName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestedScrollWebDashboardBinding.inflate(layoutInflater)

        sitesUrl = intent.extras?.getString(Constants.KEY_URL).toString()
        sitesName = intent.extras?.getString(Constants.KEY_SITE_NAME).toString()
        setNestedScrollWebViewPager()

        setContentView(binding.root)
    }

    fun setNestedScrollWebViewPager(){
        val fragmentFactory = NestedScrollWebFragmentFactory(sitesUrl,sitesName)
        nestedScrollWebDashboardViewPagerAdapter = NestedScrollWebDashboardViewPagerAdapter(this, fragmentFactory,sitesUrl)
        binding.nestedScrollWebViewPager.adapter = nestedScrollWebDashboardViewPagerAdapter
        binding.nestedScrollWebViewPager.offscreenPageLimit = 1

    }

}