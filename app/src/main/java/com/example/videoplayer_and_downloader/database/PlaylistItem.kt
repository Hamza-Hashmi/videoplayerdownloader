package com.example.videoplayer_and_downloader.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PlaylistItem")
data class PlaylistItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String,
    val realPath: String,
    val isVideo: Boolean,
    val artist: String = "<unknown>",
    val album: String = "<unknown>",
    val playlistName: String,
    val playlistId: Int,
    val isFavorite: Boolean,
    val uri: String
)