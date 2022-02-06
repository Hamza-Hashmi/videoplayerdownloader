package com.example.videoplayer_and_downloader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MainPlaylist")
data class MainPlaylist(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "num_items")
    var numItems: Int = 0,
    @ColumnInfo(name = "type")
    var videoPlaylist: Boolean
)
