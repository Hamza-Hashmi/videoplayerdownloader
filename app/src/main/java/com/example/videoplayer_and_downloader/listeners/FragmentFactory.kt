package com.example.videoplayer_and_downloader.listeners

import androidx.fragment.app.Fragment
import com.example.videoplayer_and_downloader.UI.fragments.DownloadsFragment
import com.example.videoplayer_and_downloader.UI.fragments.MainVideoFragment
import com.example.videoplayer_and_downloader.UI.fragments.audiosData.MainAudioFragment

interface FragmentFactory{
    fun createVideosFragment() : Fragment
    fun createAudiosFragment() : Fragment
    fun createDownloadsFragment() : Fragment

}

class MainFragmentFactory  : FragmentFactory {
    override fun createVideosFragment() = MainVideoFragment.newInstance()

    override fun createAudiosFragment() = MainAudioFragment.newInstance()

    override fun createDownloadsFragment() = DownloadsFragment.newInstance()

}