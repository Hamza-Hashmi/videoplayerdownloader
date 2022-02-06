package com.example.videoplayer_and_downloader.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History")
class History(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val name: String,
    val realPath: String,
    val isVideo: Boolean,
    val uri: String
)
