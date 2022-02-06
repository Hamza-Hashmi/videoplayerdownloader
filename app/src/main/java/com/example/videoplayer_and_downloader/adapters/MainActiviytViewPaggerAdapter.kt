package com.example.videoplayer_and_downloader.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.fragment.app.FragmentPagerAdapter
import com.example.videoplayer_and_downloader.listeners.MainFragmentFactory


class MainActiviytViewPaggerAdapter(fm: FragmentManager, val mainFragmentFactory: MainFragmentFactory) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> mainFragmentFactory.createVideosFragment()
            1 -> mainFragmentFactory.createAudiosFragment()
            2 -> mainFragmentFactory.createDownloadsFragment()

            else -> mainFragmentFactory.createVideosFragment()
        }

    }
}