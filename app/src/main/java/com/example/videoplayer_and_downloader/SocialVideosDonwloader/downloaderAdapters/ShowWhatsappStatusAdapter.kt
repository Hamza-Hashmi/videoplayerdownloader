package com.example.videodownload.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videodownload.whatsApp.FileModel
import com.example.videoplayer_and_downloader.R

class ShowWhatsappStatusAdapter(var itemClickCalllback: (video : FileModel, pos : Int) -> Unit): RecyclerView.Adapter<ShowWhatsappStatusAdapter.WaViewHolder>() {

    var whatsappStatusList = ArrayList<FileModel>()

    class WaViewHolder(view:View): RecyclerView.ViewHolder(view){
         val videoView : ImageView = view.findViewById(R.id.videoView)
        fun bind(video: FileModel, position: Int, callback: (video: FileModel, pos: Int) -> Unit){
            Glide.with(videoView.context?.applicationContext!!)
                .load(video.name!!.absoluteFile)
                .into(videoView)
            itemView.setOnClickListener{
                callback(video, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaViewHolder {
        return WaViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.video_status_viewholder_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WaViewHolder, position: Int) {
        holder.bind(whatsappStatusList.get(position), position, itemClickCalllback)
    }

    override fun getItemCount(): Int =whatsappStatusList.size

    fun updateList(newList : ArrayList<FileModel>){
        whatsappStatusList.clear()
        if(newList.size > 0){
            whatsappStatusList = newList
        }
        notifyDataSetChanged()

    }

}