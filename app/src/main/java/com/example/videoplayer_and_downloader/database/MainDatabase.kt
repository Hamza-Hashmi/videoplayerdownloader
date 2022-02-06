package com.example.videoplayer_and_downloader.database

import android.app.Activity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData
import com.example.videoplayer_and_downloader.Utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MainPlaylist::class, History::class, AudioHistory::class, PlaylistItem::class,TrendingVideoData::class],
    version = 2,exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao

   // abstract fun skuDetailsDao(): AugmentedSkuDetailsDao

    companion object {
        private var instance: MainDatabase? = null
        fun getInstance(context: Context): MainDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    MainDatabase::class.java,
                    context.resources.getString(R.string.main_data_base)

                ).allowMainThreadQueries()
                    .addMigrations(object :Migration(1,2){
                        override fun migrate(database: SupportSQLiteDatabase) {
                            database.execSQL("CREATE  TABLE IF NOT EXISTS 'AudioHistory' ('id'INTEGER ,PRIMARY KEY('id'))")
                        }

                    })

                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.Default).launch {
                                instance?.let {
                                    val allVideo = MainPlaylist(
                                        id = allVideoPlaylistId,
                                        name = all,
                                        videoPlaylist = true
                                    )
                                    val favoriteVideo =
                                        MainPlaylist(
                                            id = favoriteVideoPlaylistId,
                                            name = favorites,
                                            videoPlaylist = true
                                        )
                                    val allAudio = MainPlaylist(
                                        id = allAudioPlaylistId,
                                        name = all,
                                        videoPlaylist = false
                                    )
                                    val favoriteAudio =
                                        MainPlaylist(
                                            id = favoriteAudioPlaylistId,
                                            name = favorites,
                                            videoPlaylist = false
                                        )
                                    it.databaseDao().insertMainPlaylist(allVideo)
                                    it.databaseDao().insertMainPlaylist(favoriteVideo)
                                    it.databaseDao().insertMainPlaylist(allAudio)
                                    it.databaseDao().insertMainPlaylist(favoriteAudio)
                                }
                            }.invokeOnCompletion {
                                val activity = try {
                                    context as Activity
                                } catch (e: Exception) {
                                    null
                                }

                            }
                        }
                    }).build()
            }
            return instance!!
        }
    }

}