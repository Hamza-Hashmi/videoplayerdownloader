package com.example.videodownload.factories

import androidx.fragment.app.Fragment
import com.example.videodownload.fragments.NestedScrollWebFragment
interface NestedScrollWebFactory{
    fun createNestedScrollWebFragment(sitesUrl: String?): Fragment
}

class NestedScrollWebFragmentFactory(var url: String?, var siteName: String?)  : NestedScrollWebFactory {
    override fun createNestedScrollWebFragment(sitesUrl: String?) = NestedScrollWebFragment.newInstance(sitesUrl,  siteName)
}