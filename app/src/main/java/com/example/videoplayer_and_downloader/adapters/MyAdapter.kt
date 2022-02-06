package com.example.videoplayer_and_downloader.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.videoplayer_and_downloader.UI.fragments.AllVideosFragment
import com.example.videoplayer_and_downloader.UI.fragments.VideoFoldersFragments

class MyAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return VideoFoldersFragments()
            1 -> return AllVideosFragment()

        }
        return VideoFoldersFragments()
    }
}