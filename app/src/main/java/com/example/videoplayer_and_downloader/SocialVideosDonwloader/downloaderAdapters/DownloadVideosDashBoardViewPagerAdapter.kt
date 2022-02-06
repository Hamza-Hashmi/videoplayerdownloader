package com.example.videodownload.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.videodownload.fragments.DownloadedVideosFragment
import com.example.videodownload.fragments.SearchVideoUrlFragment
import com.example.videodownload.fragments.VideoInProgressFragment

class DownloadVideosDashBoardViewPagerAdapter constructor(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                SearchVideoUrlFragment()
            }
            1 -> {
                VideoInProgressFragment()
            }
            2 ->{
                DownloadedVideosFragment()
            }
            else -> {
                SearchVideoUrlFragment()
            }
        }
    }
}