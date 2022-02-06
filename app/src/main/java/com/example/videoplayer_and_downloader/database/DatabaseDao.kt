package com.example.videoplayer_and_downloader.database

import androidx.room.*
import com.example.videoplayer_and_downloader.TrendingVideos.trendingModels.TrendingVideoData


@Dao
interface DatabaseDao {

    @Query("SELECT * FROM MainPlaylist")
    fun getAllMainPlaylists(): List<MainPlaylist>

    @Query("SELECT * FROM History")
    fun getAllHistoryItems(): List<History>

    @Query("SELECT * FROM AudioHistory")
    fun getAllAudioHistoryItems(): List<AudioHistory>

    @Query("SELECT * FROM PlaylistItem")
    fun getAllPlaylistItems(): List<PlaylistItem>

    @Query("SELECT * FROM PlaylistItem WHERE playlistName=:name")
    fun getAllPlaylistItemsByName(name: String): List<PlaylistItem>

    @Insert
    fun insertMainPlaylist(vararg users: MainPlaylist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMainPlaylist(users: List<MainPlaylist>)


    @Query("SELECT * FROM MainPlaylist WHERE name=:name")
    fun getPlaylistByName(name: String): MainPlaylist?

    @Query("SELECT * FROM TrendingVideoData")
    fun getFavList():List<TrendingVideoData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryItem(vararg users: History)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAudioHistoryItem(vararg users: AudioHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIntoFavourite(vararg fav: TrendingVideoData)


    @Query("DELETE FROM TrendingVideoData WHERE url=:videoUrl")
    fun deleteFromFav(videoUrl:String):Int

    @Query("DELETE FROM History WHERE realPath=:videoPath")
    fun deleteHistoryByPath(videoPath: String): Int

    @Query("DELETE FROM AudioHistory WHERE realPath=:videoPath")
    fun deleteAudioHistoryByPath(videoPath: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylistItem(vararg users: PlaylistItem)

    @Query("DELETE FROM PlaylistItem WHERE realPath=:videoPath AND playlistId=:mainPlaylistId")
    fun deletePlaylistItemByPath(videoPath: String, mainPlaylistId: Int): Int

    @Update
    suspend fun updateVideoHistory(history: History)

    @Query("UPDATE History SET name = :name , realPath=:real_path,uri=:uri WHERE realPath = :old_path")
    suspend fun updateVideoHistoryById(old_path:String,name: String,real_path:String,uri: String):Int


    @Query("UPDATE PlaylistItem SET name = :name , realPath=:real_path,uri=:uri WHERE realPath = :old_path")
    suspend fun updateVideoPlaylisItemById(old_path:String,name: String,real_path:String,uri: String):Int

 @Query("UPDATE AudioHistory SET name = :name , realPath=:real_path ,uri=:uri WHERE realPath = :old_path")
    suspend fun updateAudioHistoryById(old_path:String,name: String,real_path:String,uri:String):Int

    @Update
    suspend fun updateAudioHistory(audioHistory: AudioHistory)

    @Delete
    fun deleteMainPlaylist(vararg playlist: MainPlaylist)

    @Query("UPDATE MainPlaylist SET num_items =num_items+ :num WHERE id IN (:ids)")
    fun updatePlaylistItems(ids: List<Int>, num: Int)

    @Query("UPDATE MainPlaylist SET num_items =num_items+ :num WHERE id =:ids")
    fun updatePlaylistItem(ids: Int, num: Int)
}