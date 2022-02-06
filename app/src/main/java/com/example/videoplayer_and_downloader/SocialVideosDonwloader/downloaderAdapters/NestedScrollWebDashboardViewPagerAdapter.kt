package com.example.videodownload.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.videodownload.factories.NestedScrollWebFactory

class NestedScrollWebDashboardViewPagerAdapter(fm: FragmentActivity, val factory: NestedScrollWebFactory, var url: String?) : FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
       return 1
    }
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                factory.createNestedScrollWebFragment(url)
            }
            else -> {
                factory.createNestedScrollWebFragment(url)
            }
        }
    }
}