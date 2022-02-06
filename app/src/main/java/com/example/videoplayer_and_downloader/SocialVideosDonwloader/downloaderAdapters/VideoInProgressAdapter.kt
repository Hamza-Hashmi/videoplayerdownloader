package com.example.videodownload.adapters

import android.graphics.drawable.Drawable
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.NestedScrollWebDashboardActivity
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.DownloadVideosDashboardActivity
import com.example.videodownload.appUtils.ShowDeleteDialog
import com.example.videodownload.appUtils.getMainStoragePath
import com.example.videodownload.callbacklisteners.downloadProgressClickListener
import com.example.videodownload.datamodel.videoDetail
import com.example.videodownload.list_helper.BaseItemOfVideoInProgress
import com.example.videodownload.list_helper.ItemType
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils.MyDownloadManagerClass
import java.io.File
import java.text.DecimalFormat

class VideoInProgressAdapter (
    downloadClick: downloadProgressClickListener,
    videoList: List<videoDetail>,
    isPause: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var downloadClickListener = downloadClick
    private var isPaused = isPause
    var list: ArrayList<BaseItemOfVideoInProgress> = ArrayList()
    class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val media = itemView.findViewById<ImageView>(R.id.imgVideo)
        val downloadSize = itemView.findViewById<TextView>(R.id.tvVideoSize)
        val videoIsPaused = itemView.findViewById<TextView>(R.id.tvVideoIsPaused)
        val videoDownloadingCancel = itemView.findViewById<ImageView>(R.id.btnCancel)
        val pauseVideo = itemView.findViewById<ImageView>(R.id.btnPause)
        val internetSpeed = itemView.findViewById<TextView>(R.id.internetspeed)
        val videoName = itemView.findViewById<TextView>(R.id.tvVideoName)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.videoProgressBar)

        fun bindView(video: videoDetail, isPaused: Boolean, downloadClick: downloadProgressClickListener, position: Int, totalSize: Int) {

            if(isPaused){
                videoIsPaused.visibility= View.VISIBLE
                downloadSize.visibility= View.GONE
            }else{
                videoIsPaused.visibility= View.GONE
                downloadSize.visibility= View.VISIBLE
            }

            val dailymotionHomeVideo = itemView.context.resources.getString(R.string.dailymotion_url) + "/"
            val thumbnailUrl = when {
                video.page?.contains(dailymotionHomeVideo, true)!! -> video.page?.substring(
                    IntRange(
                        0,
                        dailymotionHomeVideo.length - 1
                    )
                ) + "thumbnail" + video.page?.substring(
                    IntRange(
                        dailymotionHomeVideo.length - 1,
                        video.page!!.length - 1
                    )
                )
                else -> video.link
            }

            Glide.with(itemView.context).load(thumbnailUrl).into(
                media
            )

            videoDownloadingCancel.setOnClickListener {
                if (videoDownloadingCancel.context is DownloadVideosDashboardActivity){
                    (videoDownloadingCancel.context as DownloadVideosDashboardActivity).ShowDeleteDialog {
                        if(it){
                            downloadClick.onDownloadCancel(position)
                        }
                    }
                }else if (videoDownloadingCancel.context is NestedScrollWebDashboardActivity){
                    (videoDownloadingCancel.context as NestedScrollWebDashboardActivity).ShowDeleteDialog {
                        if(it){
                            downloadClick.onDownloadCancel(position)
                        }
                    }
                }

            }

            pauseVideo.setOnClickListener {
                when {
                    isPaused -> downloadClick.onDownloadStart()
                    else -> downloadClick.onDownloadPause()
                }
            }

            val downloadFolder = itemView.context.getMainStoragePath()

            val downloadSpeedValue: Long = MyDownloadManagerClass.getDownloadSpeed()
            val downloadSpeedText = "" + Formatter.formatShortFileSize(itemView.context, downloadSpeedValue) + "/S"
            internetSpeed.setText(downloadSpeedText)

            if (downloadFolder != null) {
                videoName.text = video.name
                val extString = "." + video.type
                var downloaded: String
                val file = File(downloadFolder, video.name + extString)
                if (file.exists()) {

                    if (video.size != null) {

                        if(video.page?.contains(dailymotionHomeVideo)!!){
                            val downloadedSize = file.length()
                            downloaded = Formatter.formatShortFileSize(itemView.context, downloadedSize)
                                downloadSize.text = downloaded
                            if (!isPaused) {
                                if (!progressBar.isIndeterminate) {
                                    progressBar.isIndeterminate = true
                                }
                            } else {
                                progressBar.isIndeterminate = false
                            }
                        }
                        else{
                            val downloadedSize = file.length()
                            downloaded = Formatter.formatFileSize(itemView.context, downloadedSize)
                            var percent = 100.0 * downloadedSize / video.size!!.toLong()
                            if (percent > 100.0) {
                                percent = 100.0
                            }
                            val percentFormat = DecimalFormat("00.00")
                            val percentFormatted = percentFormat.format(percent)
                            progressBar.progress = percent.toInt()
                            val formattedSize = Formatter.formatFileSize(itemView.context, video.size!!.toLong())
                            val statusString =
                                downloaded + " / " + formattedSize + " " + percentFormatted +
                                        "%"
                            downloadSize.text = statusString
                        }

                    } else {
                        val downloadedSize = file.length()
                        downloaded = Formatter.formatShortFileSize(itemView.context, downloadedSize)
                        downloadSize.text = downloaded
                        if (!isPaused) {
                            if (!progressBar.isIndeterminate) {
                                progressBar.isIndeterminate = true
                            }else{

                            }
                        } else {
                            progressBar.isIndeterminate = false
                        }
                    }
                } else {
                    if (video.size != null) {
                        val formattedSize =
                            Formatter.formatShortFileSize(itemView.context, video.size!!.toLong())
                        val statusString = "0KB / $formattedSize 0%"
                        downloadSize.text = statusString
                        progressBar.progress = 0
                    } else {
                        val statusString = "0kB"
                            downloadSize.text = statusString
                        progressBar.progress = 0
                    }
                }
            } else {
                Log.e("LOG_TAG", "bindView: Download folder is null in adapter ")
            }

            when {
                isPaused -> pauseVideo.setImageResource(R.drawable.videostopicon)
                else -> pauseVideo.setImageResource(R.drawable.videopauseicon)
            }

            val draw: Drawable? =  videoDownloadingCancel.context.getDrawable(R.drawable.progress_bar_style)
            progressBar.setProgressDrawable(draw)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return myViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.video_in_progress_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        item.bind(holder, isPaused, downloadClickListener, position, itemCount)
    }

    fun updateVideoList(downloadVideoList: ArrayList<videoDetail>, isPaused: Boolean) {
        this.isPaused = isPaused

        addList(downloadVideoList)
    }
    fun addList(itemList1: ArrayList<videoDetail>?) {
        list.clear()
        itemList1?.forEachIndexed { i, x ->
                list.add(VideoItem(x, isPaused))
        }
        notifyDataSetChanged()
    }

    private class VideoItem(var videoItem: videoDetail, var isPaused: Boolean, ) : BaseItemOfVideoInProgress() {

        override fun itemType(): Int {
            return ItemType.RealItem
        }

        override fun bind(holder: RecyclerView.ViewHolder, paused: Boolean, downloadClickListener: downloadProgressClickListener, position: Int, itemCount: Int) {
            (holder as myViewHolder).bindView( videoItem, isPaused, downloadClickListener, position, itemCount)

        }

    }
    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = list[position].itemType()

}