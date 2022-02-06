package com.example.videoplayer_and_downloader.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.getUriPath
import com.example.videoplayer_and_downloader.databinding.RowFileManagerBinding
import java.io.File

data class FileManagerAdapter(
    var filesAndFolders: Array<File?>?,
    val callback:(selectedFile:File)->Unit
):RecyclerView.Adapter<FileManagerAdapter.FileManagerViewHolder>() {

    inner class FileManagerViewHolder(val binding:RowFileManagerBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileManagerViewHolder {
        return FileManagerViewHolder(RowFileManagerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FileManagerViewHolder, position: Int) {
        val selectedFile = filesAndFolders?.get(position)

        holder.binding.apply {
           this.tvName.text = selectedFile?.name

            Log.e("TAG", "onBindViewHolder: " + selectedFile?.name)

            if (selectedFile?.isDirectory!!) {
                this.ivThumbnail.setImageResource(R.drawable.ic_file_manager)
            } else {
                if (selectedFile.name.contains(".mp4")) {
                    Glide.with(ivThumbnail.context).load(selectedFile.path?.let {
                        getUriPath(
                            it
                        )
                    })
                        .placeholder(R.drawable.ic_folder_default)
                        .into(ivThumbnail)

                } else {
                    holder.itemView.visibility = View.GONE
                }
            }

        }
        holder.itemView.setOnClickListener {
            selectedFile?.let { it1 -> callback(it1) }
        }

    }

    override fun getItemCount(): Int {
        return filesAndFolders?.size!!
    }

}