package com.example.videoplayer_and_downloader.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData
import com.example.videoplayer_and_downloader.databinding.RowTrendingItemsBinding
import com.example.videoplayer_and_downloader.listeners.onTrendingMediaClick

class TrendingsAdapter(val videosList:ArrayList<TrendingVideoData>, val listener:onTrendingMediaClick) :
    RecyclerView.Adapter<TrendingsAdapter.TrendingVideoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingVideoViewHolder {
        return TrendingVideoViewHolder(RowTrendingItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: TrendingVideoViewHolder, position: Int) {
        val videos  = videosList.get(position)
        Log.e("TAG", "onBindViewHolder url in adapter:  ${videos.url}" )

        holder.binding.apply{
            Glide.with(this.ivTrendingThumb.context).load(videos.thumbnail_720_url).into(ivTrendingThumb)
            this.tvTrendingVideoName.text = videos.title
            this.btnAddToFav.setOnClickListener {
                listener.onAddToFavourite(videos)
                btnAddToFav.setImageResource(R.drawable.ic_favourite_selected)
            }
            this.btnShareTrending.setOnClickListener {
                listener.onShareVideo(videos)
            }
        }


        holder.itemView.setOnClickListener{
            listener.onPlayClick(videos)
        }
    }

    override fun getItemCount(): Int {
        return videosList.size
    }
    class TrendingVideoViewHolder(val binding:RowTrendingItemsBinding): RecyclerView.ViewHolder(binding.root){
    }
}