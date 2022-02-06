package com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.videodownload.adapters.ShowWhatsappStatusAdapter
import com.example.videodownload.allViewModel.WhatsappStatusViewModel
import com.example.videodownload.appUtils.Constants
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.whatsApp.Common
import com.example.videoplayer_and_downloader.databinding.ActivityShowWhatsappStatusBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShowWhatsappStatus : AppCompatActivity() {


    val whatsappStatusViewModel : WhatsappStatusViewModel by viewModel()
    var staggeredGridLayoutManager : StaggeredGridLayoutManager? = null
    var showWhatsappStatusAdapter : ShowWhatsappStatusAdapter? = null
    lateinit var binding : ActivityShowWhatsappStatusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowWhatsappStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnOpenWhatsApp.setOnClickListener {
            val whatsappApp = "com.whatsapp"
            kotlin.runCatching {

                val intent = packageManager.getLaunchIntentForPackage(whatsappApp)
                startActivity(intent)

            } .onFailure{

            }
        }

        binding.tvOpenWhatsApp.setOnClickListener {
            binding.btnOpenWhatsApp.performClick()
        }

        if (Common.STATUS_DIRECTORY.exists()) {
            whatsappStatusViewModel.getLiveStatusVideos(Common.STATUS_DIRECTORY)
        } else if (Common.STATUS_DIRECTORY_NEW.exists()) {
            whatsappStatusViewModel.getLiveStatusVideos(Common.STATUS_DIRECTORY_NEW)
        } else {
            binding.whatsappStatusRecyclerView?.visibility = View.GONE
            binding.noVideoLinearLayout?.visibility = View.VISIBLE
        }

        staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.whatsappStatusRecyclerView?.setHasFixedSize(true)
        binding.whatsappStatusRecyclerView?.layoutManager = staggeredGridLayoutManager
        showWhatsappStatusAdapter = ShowWhatsappStatusAdapter { video, pos ->
            val intent = Intent(this, VideoPlayerForWhatsapp::class.java)
            intent.putExtra(Constants.WA_VIDEO_FILEPATH, video.name.absolutePath)
            intent.putExtra(Constants.WA_VIDEO_TITLE, video.name.name)

            startActivity(intent)
        }
        binding.whatsappStatusRecyclerView?.adapter = showWhatsappStatusAdapter
        whatsappStatusViewModel.selectedVideos.observe(this, Observer {
            if (it.size > 0) {
                binding.whatsappStatusRecyclerView?.visibility = View.VISIBLE
                binding.noVideoLinearLayout?.visibility = View.GONE
                showWhatsappStatusAdapter?.updateList(it)
            } else {
                binding.whatsappStatusRecyclerView?.visibility = View.GONE
                binding.noVideoLinearLayout?.visibility = View.VISIBLE
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
}