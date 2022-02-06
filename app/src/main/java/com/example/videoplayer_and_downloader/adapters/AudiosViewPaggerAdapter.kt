package com.example.videoplayer_and_downloader.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.videoplayer_and_downloader.UI.fragments.audiosData.AlbumsFragment
import com.example.videoplayer_and_downloader.UI.fragments.audiosData.ArtistFragment
import com.example.videoplayer_and_downloader.UI.fragments.audiosData.PlayListFragment
import com.example.videoplayer_and_downloader.UI.fragments.audiosData.TracksFragment


class AudiosViewPaggerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return TracksFragment()
            1 -> return ArtistFragment()
            2 -> return AlbumsFragment()
          //  3 -> return PlayListFragment()
            else->return TracksFragment()


        }

    }
}