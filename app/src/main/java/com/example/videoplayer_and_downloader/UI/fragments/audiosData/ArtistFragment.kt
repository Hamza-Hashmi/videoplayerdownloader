package com.example.videoplayer_and_downloader.UI.fragments.audiosData

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.UI.activites.DetailsAudioActivity
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adapters.ArtistAdapter
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.InterstitialAdUpdated
import com.example.videoplayer_and_downloader.databinding.FragmentArtistBinding
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArtistFragment : Fragment() {
    var _binding: FragmentArtistBinding?=null
    val binding get() = _binding!!
    private val TAG = "ArtisitFragment"
    val viewModel: AudioSongsViewModel by viewModel()
    lateinit var adapter : ArtistAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentArtistBinding.inflate(layoutInflater)
        viewModel.getAllArtistList()
     /*   if (!isHideAds){
            activity?.let {
                it.setNativeAd(
                    binding.shimmerViewContainer,
                    binding.refadslayout,
                    binding.refadslayoutFb,
                    binding.adCard,
                    R.layout.native_banner_layout,
                    R.layout.native_banner_fb_layout,
                    it.isAlreadyPurchased(),
                    TAG,
                    mAdPriority,
                    preLoadedNativeAd,
                    getString(R.string.banner_native_id)
                )
            }
        }
*/


        viewModel.artistList.observe(viewLifecycleOwner, Observer {  artistList ->

            if (artistList.size > 0){

                adapter = ArtistAdapter(callback = { artistName->
                    if (itemClickCount >= 2 ) {
                        itemClickCount = 1

                        InterstitialAdUpdated.getInstance().showInterstitialAdNew(requireActivity(),onAction={
                            onClick(artistName)
                        })

                    } else {
                        itemClickCount += 1
                        onClick(artistName)


                    }
                },getString(R.string.native_id), getString(R.string.fb_native_ad), keyPriority =  ad_periority)

                binding.artistRv.adapter = adapter

                activity?.let{

                    adapter.setData(artistList,it.isInternetConnected())
                }

                adapter.notifyDataSetChanged()
            }
        })
        return binding.root
    }

    private fun onClick(artistName: String) {
        Intent(requireContext(), DetailsAudioActivity::class.java).also{
            it.putExtra("which",artistName)
            it.putExtra("type","artist")
            startActivity(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}