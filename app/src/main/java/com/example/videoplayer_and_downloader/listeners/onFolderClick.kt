package com.example.videoplayer_and_downloader.listeners

import com.example.videoplayer_and_downloader.models.Folder

interface onFolderClick {
    fun onItemClick(position: Int, currentFolder: Folder)
}