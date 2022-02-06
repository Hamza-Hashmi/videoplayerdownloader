package com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloaderAdapters

import android.content.Context
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel.Commons.Companion.isDailymotionDownloadUrl
import com.example.videoplayer_and_downloader.Utils.isDailymotionDownloadUrl
import com.example.videoplayer_and_downloader.databinding.RowAvailableVideoFormatsBinding

class AvailableVideoFormatAdapter(
    var list: List<VideoListClass.VideoInfo>,
    var context: Context,
    var callback: (video: VideoListClass.VideoInfo, position: Int) -> Unit) : RecyclerView.Adapter<AvailableVideoFormatAdapter.AvailableVideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableVideoViewHolder {
      return AvailableVideoViewHolder(RowAvailableVideoFormatsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: AvailableVideoViewHolder, position: Int) {
         val video = list[position]


        if (isDailymotionDownloadUrl(video)!!){
            holder.binding.videoSize.visibility = View.GONE
        }


        // fbVideoName = video.name

        if (video.quality.equals("Unknown Quality")) {
            holder.binding.videoQualtiy.text = "context.getString(R.string.p240)"
        } else {
            holder.binding.videoQualtiy.text = video.quality
        }


        if (video.size != null) {
            val sizeFormatted = Formatter.formatShortFileSize(context, video.size!!.toLong())
            holder.binding.videoSize.text = sizeFormatted
            //fbVideoSize = sizeFormatted
        }

        when {
            video.checked -> {
                holder.binding.avaialabeItem.setBackgroundResource(R.color.pinkColor)
                holder.binding.videoQualtiy.setTextColor(context.resources.getColor(R.color.white))
                holder.binding.videoSize.setTextColor(context.resources.getColor(R.color.white))
            }
            else -> {
                holder.binding.avaialabeItem.setBackgroundResource(R.color.white)
                holder.binding.videoQualtiy.setTextColor(context.resources.getColor(R.color.pinkColor))
                holder.binding.videoSize.setTextColor(context.resources.getColor(R.color.pinkColor))

                //   itemView.foundVideoPixels.setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))
                //   itemView.foundVideoSize.setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))
            }
        }

        holder.itemView.setOnClickListener {

            Log.e("TAG", "onBindViewHolder:click "  )
            callback(video, position)
        }



    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(VideoList: List<VideoListClass.VideoInfo>) {
        list = VideoList
        notifyDataSetChanged()
    }

    class AvailableVideoViewHolder(val binding:RowAvailableVideoFormatsBinding):RecyclerView.ViewHolder(binding.root)


}