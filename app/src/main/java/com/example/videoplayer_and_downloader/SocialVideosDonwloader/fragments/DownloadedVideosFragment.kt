package com.example.videodownload.fragments

import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.VideoPlayerForWhatsapp
import com.example.videodownload.adapters.DownloadedVideosAdapter
import com.example.videodownload.allViewModel.MainViewModel
import com.example.videodownload.appUtils.*
import com.example.videodownload.appUtils.Constants.Companion.KEY_VIDEO_DATA_BUNDLE
import com.example.videodownload.datamodel.SavedDownloadVideos
import com.example.videodownload.download_feature.lists.DownloadQueues
import com.example.videoplayer_and_downloader.databinding.FragmentDownloadedVideosBinding
import com.example.videoplayer_and_downloader.databinding.SelectedVideoBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import kotlin.collections.ArrayList


class DownloadedVideosFragment : BaseFargment(){
    var _binding : FragmentDownloadedVideosBinding? = null
    val binding get() = _binding!!
    var name : String = ""
    var link : String = ""
    private   var queues: DownloadQueues? = null
    lateinit var bottomSheetDialog: BottomSheetDialog
    var videoList: ArrayList<SavedDownloadVideos> = ArrayList()
    lateinit var downloadedVideosAdapter: DownloadedVideosAdapter
    val viewModel: MainViewModel by sharedViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadedVideosBinding.inflate(layoutInflater)
        viewModel.downloadedData.observe(viewLifecycleOwner,{
            if (it.size >0){
                binding.noDownloadVideoLayout.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.downloadingCompleteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                downloadedVideosAdapter  = DownloadedVideosAdapter(requireActivity(),
                    it,
                    callBack = {pos ->
                       videoMenu(pos)

                    },
                callBackSelect = {name, link ->
                    playVideo(name,link)
                })
                downloadedVideosAdapter.notifyDataSetChanged()
                downloadedVideosAdapter.also { binding.downloadingCompleteRecyclerView.adapter = it }
            }else{
                binding.noDownloadVideoLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE


            }

        })
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.initDownloadedData()
        viewModel.loadDownloaderVideo()
    }

    fun videoDownloaded(videoName: String?): Boolean {
        var found = false
        for (video in videoList) {
            if (video.name == videoName) {
                found = true
                break
            }
        }
        return found
    }

    private fun renameVideo(link: String, pos: Int) {

        bottomSheetDialog.dismiss()
        getMainActivity()?.RenameDialog(link) { newName, oldName ->
            val baseName = oldName.split(".")[0]
            val type = oldName.split(".")[1]

            val downloadsFolder = getMainActivity()?.getMainStoragePath()
            val renamedFile = File(downloadsFolder, "$newName.$type")
            val file = File(downloadsFolder, "$baseName.$type")

            val newVideoName = "$newName.$type"
            if (videoDownloaded(newVideoName)) {
                getMainActivity()?.showShortMessage("Video with this name already Exist..")
                return@RenameDialog
            }

            if (file.renameTo(renamedFile)) {
                viewModel.downloadedData.observe(viewLifecycleOwner,{
                    it.get(pos).link = newVideoName
                    binding.downloadingCompleteRecyclerView?.adapter?.notifyItemChanged(pos)
                    downloadedVideosAdapter.notifyDataSetChanged()
                    bottomSheetDialog.dismiss()
                    MediaScannerConnection.scanFile(
                        getMainActivity(),
                        arrayOf(renamedFile.toString()),
                        null
                    ) { path, uri -> }
                })

            } else {
                Toast.makeText(activity, "invalid filename", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun deleteVideo(link : String,position: Int) {
        bottomSheetDialog.dismiss()
        getMainActivity()?.DeleteSavedVideo {
            if (it) {
                val downloadFolder = getMainActivity()?.getMainStoragePath()
                downloadFolder?.let {
                    val file = File(downloadFolder, link)
                    when {
                        file.exists() -> {
                            val delete = file.delete()
                            when {
                                delete -> {
                                    getMainActivity()?.showShortMessage("Deleted Successfully")
                                    bottomSheetDialog.dismiss()
                                    updateDownloadVideosList(position)


                                }
                                else -> getMainActivity()?.showShortMessage("Failed to delete this video")
                            }
                        }
                        else -> getMainActivity()?.showShortMessage("video not found")
                    }
                } ?: getMainActivity()?.showShortMessage("video not found")

            }
        }
    }
    private fun shareVideo(link: String) {
        val downloadFolder = getMainActivity()?.getMainStoragePath()
        downloadFolder?.let {
            val file = File(downloadFolder, link)
            getMainActivity()?.shareVideoFile(file)
        }
    }
    private fun playVideo(name : String,link : String) {
        val downloadFolder = getMainActivity()?.getMainStoragePath()
        downloadFolder?.let {
            val bundle = Bundle()
            bundle.putString(Constants.videoFilePath, name)
            bundle.putString(Constants.videoTitle, link)

            val intent = Intent(context, VideoPlayerForWhatsapp::class.java)
            intent.putExtra(KEY_VIDEO_DATA_BUNDLE, bundle)
            startActivity(intent)
        }
    }
    fun videoMenu(position: Int){
        bottomSheetDialog = BottomSheetDialog(requireContext())
        var binding : SelectedVideoBottomSheetBinding
        binding = SelectedVideoBottomSheetBinding.inflate(layoutInflater)
        viewModel.downloadedData.observe(viewLifecycleOwner,{
            val downloadVideo = it[position]
            name = downloadVideo.name
            link = downloadVideo.link
            Glide.with(requireActivity()).load(downloadVideo.name).into(
                binding.imgVideo
            )
            binding.tvVideoName.text = downloadVideo.link
            binding.tvVideoSize.text = downloadVideo.page
        })
        binding.apply {
            imgVideoShare.setOnClickListener {
                shareVideo(link)
            }
            imgVideoRename.setOnClickListener {
                renameVideo(link,position)
            }
            imgVideoDelete.setOnClickListener {
                deleteVideo(link,position)
            }
            btnCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }

    private fun updateDownloadVideosList(position: Int){
            downloadedVideosAdapter.downloadedArrayList.removeAt(position)
            downloadedVideosAdapter.notifyDataSetChanged()
    }

}