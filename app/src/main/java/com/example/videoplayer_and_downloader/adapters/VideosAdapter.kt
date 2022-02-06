package com.example.videoplayer_and_downloader.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.adItem
import com.example.videoplayer_and_downloader.Utils.deviceItemVideo
import com.example.videoplayer_and_downloader.Utils.getSingleMediaAdItem
import com.example.videoplayer_and_downloader.Utils.none
import com.example.videoplayer_and_downloader.listeners.onMediaClick
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo
import com.example.videoplayer_and_downloader.viewHolders.MyVideoViewHolder
import com.google.android.gms.ads.nativead.NativeAd

class VideosAdapter(private val clickInterface: onMediaClick,
private val context: Context,
private var itemLayout: Int
) :
RecyclerView.Adapter<MyVideoViewHolder>() {

    private var videoList = ArrayList<MediaModel>()
    private var selectedFolder = none
    var nativeAd: NativeAd? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVideoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return MyVideoViewHolder(v, viewType)
    }

    override fun onBindViewHolder(holder: MyVideoViewHolder, position: Int) {
        holder.bind(
            videoList[position],
            context,
            clickInterface,
            position,
            SelectedListInfo(
                listType = deviceItemVideo,

                selectedParent = selectedFolder,
                videoList[position].isVideo
            ),
            itemCount,
            nativeAd
        )
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (videoList[position].name == adItem)
            R.layout.row_native_ad
        else
            itemLayout
    }

    fun updateList(
        itemList: ArrayList<MediaModel>,
        selectedFolder: String,
        showAd: Boolean = true
    ) {
        this.videoList = ArrayList(itemList)
        if (showAd) {
            if (videoList.size > 15) {
                for (i in 0 until videoList.size) {
                    if ((i == 2 || (i % 17 == 0 && i != 0)) && videoList[i].name != adItem)
                        addAdItem(i)
                }
            } else if (videoList.size >= 2)
                addAdItem(2)
        }
        this.selectedFolder = selectedFolder
        notifyDataSetChanged()
    }


    fun containsAd(): Boolean {
        var contains = false
        try {
            contains = videoList[2].name == adItem
        } catch (e: IndexOutOfBoundsException) {
        }
        return contains
    }

    private fun addAdItem(pos: Int) {
        if (videoList.size == 2)
            this.videoList.add(videoList.size, getSingleMediaAdItem())
        else {
            if (this.videoList[pos].name != adItem)
                this.videoList.add(pos, getSingleMediaAdItem())
        }
    }


    fun updateItemLayout(itemLayout: Int) {
        this.itemLayout = itemLayout
        notifyDataSetChanged()
    }

    fun updateAd(nativeAd: NativeAd) {
        this.nativeAd = nativeAd
        notifyDataSetChanged()
    }

}