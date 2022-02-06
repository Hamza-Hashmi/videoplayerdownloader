package com.example.videoplayer_and_downloader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData

@Database(entities = [MainPlaylist::class,  History::class, AudioHistory::class, PlaylistItem::class,TrendingVideoData::class],version = 1,exportSchema = false)
abstract class MyAppDataBase:RoomDatabase() {

    abstract fun getAppData(): DatabaseDao

    companion object {

        @Volatile
        private var instance: MyAppDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            MyAppDataBase::class.java,
            "myVideo_Player_database"
        ).fallbackToDestructiveMigration()
            .build()



    }
}