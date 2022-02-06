package com.example.videoplayer_and_downloader.UI.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.bottomSheetNative
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.setPreloadNativeAd
import com.example.videoplayer_and_downloader.databinding.FragmentExitDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class ExitDialog : BottomSheetDialogFragment() {

    lateinit var binding: FragmentExitDialogBinding

  //  val mainViewModel: MainViewModel by viewModel()
    private val TAG = ExitDialog::class.java.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExitDialogBinding.inflate(layoutInflater)

        Log.e("bottomsheet", "onCreateView: bottomsheet ad ${bottomSheetNative}" )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("bottomsheet", "onViewCreated: bottomsheet ad ${bottomSheetNative}" )
        activity?.setPreloadNativeAd(
            binding.refadslayout,
            binding.refadslayoutFb,
            R.layout.native_exit_screen_ads,
            R.layout.fb_exist_screen_ad_layout,
            bottomSheetNative
        )

        binding.btnYes.setOnClickListener {
            dismiss()
            activity?.finishAffinity()
        }
        binding.btnNo.setOnClickListener {

            dismiss()
//            mListener?.onItemClick("Cancel")
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
    override fun onDetach() {
        super.onDetach()

    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): ExitDialog {
            val fragment = ExitDialog()
            fragment.arguments = bundle
            return fragment
        }
    }

}