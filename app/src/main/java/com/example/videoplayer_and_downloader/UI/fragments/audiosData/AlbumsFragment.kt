package com.example.videoplayer_and_downloader.UI.fragments.audiosData

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.UI.activites.AnotherActivity
import com.example.videoplayer_and_downloader.UI.activites.DetailsAudioActivity
import com.example.videoplayer_and_downloader.UI.fragments.MyBaseFragment
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adapters.AlbumsAdapter
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.InterstitialAdUpdated
import com.example.videoplayer_and_downloader.databinding.FragmentAlbumsBinding
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AlbumsFragment : Fragment() {

    private  val TAG = "AlbumsFragment"
    val viewModel: AudioSongsViewModel by sharedViewModel()
    var _binding: FragmentAlbumsBinding? = null
    val binding get() = _binding!!
    lateinit var adapter: AlbumsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentAlbumsBinding.inflate(layoutInflater)
      /*  if (!isHideAds){
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



        viewModel.albumsList.observe(viewLifecycleOwner) { albumList ->
            Log.e(TAG, "onCreateView: albumslist $albumList")
            if (albumList.size > 0) {

                Log.e("TAG", "onCreateView: listsize greater  ${albumList.size}")
                adapter = AlbumsAdapter(
                    callback = { albumName ->
                        if (itemClickCount >= 2) {
                            itemClickCount = 1

                            InterstitialAdUpdated.getInstance()
                                .showInterstitialAdNew(requireActivity(), onAction = {
                                    onClick(albumName)
                                })

                        } else {
                            itemClickCount += 1
                            onClick(albumName)

                        }
                    },
                    getString(R.string.native_id),
                    getString(R.string.fb_native_ad),
                    keyPriority = ad_periority
                )

                binding.albumsRV.adapter = adapter

                activity?.let {

                    adapter.setData(
                        albumList,
                        it.isInternetConnected(),
                    )
                }

                adapter.notifyDataSetChanged()
            } else {
                Log.e(TAG, "onCreateView: album list less then zero ${albumList.size}")
            }
        }

        return binding.root
    }

    private fun onClick(albumName: String) {
        Intent(requireContext(), DetailsAudioActivity::class.java).also{
            it.putExtra("which",albumName)
            it.putExtra("type","album")
            startActivity(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}