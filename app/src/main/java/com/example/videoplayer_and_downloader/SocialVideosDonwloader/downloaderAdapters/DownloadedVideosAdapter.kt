package com.example.videodownload.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videodownload.appUtils.getMainStoragePath
import com.example.videodownload.datamodel.SavedDownloadVideos
import com.example.videoplayer_and_downloader.R
import java.io.File
import kotlin.contracts.contract

class DownloadedVideosAdapter(var context : Context, var downloadedArrayList: ArrayList<SavedDownloadVideos>, var callBack: (pos: Int) -> Unit,
                                       var callBackSelect: (name : String, link : String) -> Unit) : RecyclerView.Adapter<DownloadedVideosAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_download_item_layout, parent, false)

        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val downloadVideos = downloadedArrayList[position]
        holder.name.text = downloadVideos.link
        holder.size.text = downloadVideos.page
        if (holder.downloadFolder != null) {
                Glide.with(holder.itemView.context).load(downloadVideos.name).into(
                        holder.media
                    )
            }
        holder.menu.setOnClickListener {
           callBack(position)
        }
        holder.videoPlay.setOnClickListener {
            callBackSelect(downloadVideos.name,downloadVideos.link)
        }
    }

    override fun getItemCount(): Int {
        return downloadedArrayList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.tvDownloadingVideoName)
        val size : TextView = itemView.findViewById(R.id.tvDownloadingVideoSize)
        val media : ImageView = itemView.findViewById(R.id.imgVideoMedia)
        val menu : ImageView = itemView.findViewById(R.id.btnVideoMenu)
        val videoPlay : ImageView = itemView.findViewById(R.id.btnVideoStopAndResume)
        val downloadFolder = itemView.context.getMainStoragePath()
    }
}


