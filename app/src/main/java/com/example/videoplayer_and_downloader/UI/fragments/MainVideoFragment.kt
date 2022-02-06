package com.example.videoplayer_and_downloader.UI.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.adapters.MyAdapter
import com.example.videoplayer_and_downloader.databinding.FragmentMainVideoBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainVideoFragment : Fragment() {

    var _binding:FragmentMainVideoBinding? = null
    val binding get() = _binding!!


    companion object {
        fun newInstance() = MainVideoFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainVideoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()

        setTitles()

        binding.apply {
            btnFolders.setOnClickListener {
                btnFolders.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_selected_bg)
                btnVideos.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_unselected_bg)
                btnFolders.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                btnVideos.setTextColor(ContextCompat.getColor(requireContext(),R.color.subTextColor))
                viewPager.setCurrentItem(0)
            }
            btnVideos.setOnClickListener {
                btnVideos.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_selected_bg)
                btnFolders.background = ContextCompat.getDrawable(requireContext(),R.drawable.button_unselected_bg)
                btnVideos.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                btnFolders.setTextColor(ContextCompat.getColor(requireContext(),R.color.subTextColor))
                viewPager.setCurrentItem(1)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun initViewPager() {
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = MyAdapter(requireActivity())
        binding.viewPager.offscreenPageLimit = 2
       /* TabLayoutMediator(
            binding.tablayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
//            tab.text = titles[position]
        }.attach()*/

    }

    private fun setTitles() {
        /*binding.tablayout.getTabAt(0)?.text = "Folders"
        binding.tablayout.getTabAt(1)?.text = "Videos"*/

    }


}