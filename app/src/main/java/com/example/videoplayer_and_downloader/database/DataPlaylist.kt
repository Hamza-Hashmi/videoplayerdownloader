package com.example.videoplayer_and_downloader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


data class DataPlaylist(
    @ColumnInfo(name = "fileName")
    var fileName: String,
    @ColumnInfo(name = "TotelItems")
    var TotelItems: Int = 0,
    @ColumnInfo(name = "fileType")
    var fileType: Boolean
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
