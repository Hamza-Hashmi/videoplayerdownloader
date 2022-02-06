package com.example.videoplayer_and_downloader.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData
import com.example.videoplayer_and_downloader.Utils.favoriteAudioPlaylistId
import com.example.videoplayer_and_downloader.databinding.RowTrendingItemsBinding
import com.example.videoplayer_and_downloader.listeners.onTrendingMediaClick

class TrendingFavAdapter(val list:ArrayList<TrendingVideoData>,val listener:onTrendingMediaClick): RecyclerView.Adapter<TrendingFavAdapter.FavViewHolder>(){


    inner class FavViewHolder(val binding:RowTrendingItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        return FavViewHolder(RowTrendingItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val favData = list[position]
        holder.binding.apply {
            this.btnAddToFav.setImageResource(R.drawable.ic_favourite_selected)
            this.tvTrendingVideoName.text = favData.title
            Glide.with(this.ivTrendingThumb.context).load(favData.thumbnail_720_url).into(ivTrendingThumb)

            btnAddToFav.setOnClickListener {
                listener.onRemoveFromFav(favData)
            }

            btnShareTrending.setOnClickListener {
                listener.onShareVideo(favData)
            }

        }

        holder.itemView.setOnClickListener {
               listener.onPlayClick(favData)
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}