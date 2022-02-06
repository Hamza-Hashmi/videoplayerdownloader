package com.example.videoplayer_and_downloader.UI.fragments.audiosData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.videoplayer_and_downloader.UI.activites.MainActivity
import com.example.videoplayer_and_downloader.adapters.AudiosViewPaggerAdapter
import com.example.videoplayer_and_downloader.databinding.FragmentMainAudioBinding
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MainAudioFragment : Fragment() {

    var _binding:FragmentMainAudioBinding? =null
    val binding get() = _binding!!
    private var onSelectedTab=0
    private val tabTitles= arrayListOf("Songs","Artist","Albums")

    val mViewModel:AudioSongsViewModel by sharedViewModel()

    companion object {
        fun newInstance() = MainAudioFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentMainAudioBinding.inflate(layoutInflater)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intializeViewPagger()

//        sharedPreferenceData.putBoolean(IsTypesList,true)

        mViewModel.getAllAlbumsList()
        mViewModel.getAllTracksList()

//        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

    }
    private fun intializeViewPagger() {
        binding.viewPager.isUserInputEnabled = true
        binding.viewPager.adapter = activity?.let {  fragmentActivity->
            AudiosViewPaggerAdapter(fragmentActivity)
        }
//        binding.viewPager.offscreenPageLimit = 1
        TabLayoutMediator(
            binding.tablayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = tabTitles[position]
        }.attach()

    }

}