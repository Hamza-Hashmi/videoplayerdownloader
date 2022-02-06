package com.example.videoplayer_and_downloader.adsimplementation.ads_utils

import androidx.recyclerview.widget.RecyclerView

abstract class BaseItem {
    abstract fun itemType(): Int
    abstract fun bindItem(holder: RecyclerView.ViewHolder?, position: Int)
}