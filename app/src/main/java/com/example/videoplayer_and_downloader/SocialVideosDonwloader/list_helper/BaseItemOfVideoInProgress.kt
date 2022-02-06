package com.example.videodownload.list_helper

import androidx.recyclerview.widget.RecyclerView
import com.example.videodownload.callbacklisteners.downloadProgressClickListener

abstract class BaseItemOfVideoInProgress {
    abstract fun itemType(): Int
    abstract fun bind(holder: RecyclerView.ViewHolder, paused: Boolean, downloadClickListener: downloadProgressClickListener, position: Int, itemCount: Int)

}
