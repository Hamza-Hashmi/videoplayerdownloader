package com.example.videoplayer_and_downloader.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.UI.activites.AudioPlayerActivity
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo
import com.example.videoplayer_and_downloader.services.AudioPlayerService
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.StringBuilder
import java.util.*

    var mySlectedSongList = ""
    var selectedData = ""


    fun getRandom(i: Int): Int {
        val random = Random()
        return random.nextInt(i + 1)
    }

    fun ImageView.setSongImage(song_path:String) {

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        getSongThumbnail2(song_path)


        Glide.with(this)
            .load(getSongThumbnail2(song_path))
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_audio_placeholder)
            .into(this)

    }


    fun getSongThumbnail(songPath: String): ByteArray? {
        var imgByte: ByteArray?
        MediaMetadataRetriever().also {
            try {
                it.setDataSource(songPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imgByte = it.embeddedPicture
            it.release()
        }
        return imgByte
    }




    fun Activity.isPlayerServiceIsRunning()= (isMyServiceRunning(AudioPlayerService::class.java))


    fun getIncreaseSongPosition(songPosition: Int, songList: ArrayList<MediaModel>): Int {
        if (songPosition < songList.size && songPosition >0)
            return songPosition + 1
        else
            return  0

    }
    fun getDecreaseSongPosition( songPosition: Int,songList: ArrayList<MediaModel>): Int {
        if (songPosition <songList.size && songPosition > 0)
            return songPosition -1
        else
            return  0

    }


    suspend fun getTestFunction(): Int {
        var count=0

        CoroutineScope(Dispatchers.Main).launch {
            delay(6000)
            count=500
        }
//   val myThread= CoroutineScope(Dispatchers.Main).launch{
        runBlocking {
            delay(3000)
            count=count+100
        }



//    }

        Log.e("TAG", "getTestFunction:  count ${count}", )

        return count

    }

    fun ImageView.setPlayPauseIcon(isPlayingSong:Boolean){
        Log.e("TAG", "setPlayPauseIcon:  isPlayingSong ${isPlayingSong}", )
        if (isPlayingSong){
            setDerableIntoImage(R.drawable.ic_audio_pause)
        }else{
            setDerableIntoImage(R.drawable.ic_audio_play)

        }

    }
    fun ImageView.setPlayPauseIcon2(isPlayingSong:Boolean){
        Log.e("TAG", "setPlayPauseIcon:  isPlayingSong ${isPlayingSong}", )
        if (isPlayingSong){
            setDerableIntoImage(R.drawable.ic_audio_pause)
        }else{
            setDerableIntoImage(R.drawable.ic_audio_play)

        }

    }

    fun ImageView.setPlayPauseIcon_List(isPlayingSong:Boolean){
        Log.e("TAG", "setPlayPauseIcon:  isPlayingSong ${isPlayingSong}", )
        if (isPlayingSong){
            setDerableIntoImage(R.drawable.ic_audio_pause)
        }else{
            setDerableIntoImage(R.drawable.ic_audio_play)

        }

    }


    fun Activity.playThreadBtn(imageView: ImageView, isPlayingSong: Boolean, selectedPosition:Int, onCallback: (isPlaying:Boolean) -> Unit) {

        CoroutineScope(Dispatchers.Main).launch {

            Log.e("TAG", "onClick: plAY " + "  ${isPlayingSong}")
            imageView.setPlayPauseIcon2(isPlayingSong)
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.IO).launch {

                if (isPlayingSong) {
                    val myIntent = Intent(this@playThreadBtn, AudioPlayerService::class.java)
                    myIntent.putExtra("servicePosition", selectedPosition)
                    myIntent.putExtra("ActionName", "SongPause")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(myIntent)
                    } else {
                        startService(myIntent)
                    }

                    CoroutineScope(Dispatchers.Main).async {

                        onCallback.invoke(false)
                    }
                }else{

                    val myIntent = Intent(this@playThreadBtn, AudioPlayerService::class.java)
                    myIntent.putExtra("servicePosition", selectedPosition)
                    myIntent.putExtra("ActionName", "SongPlay")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(myIntent)
                    } else {
                        startService(myIntent)
                    }
                    CoroutineScope(Dispatchers.Main).async {
                        onCallback.invoke(true)
                    }



                }


            }



        }

    }
    fun Activity.playThreadBtnMusicList(imageView: ImageView, isPlayingSong: Boolean, selectedPosition:Int, onCallback: (isPlaying:Boolean) -> Unit) {

        CoroutineScope(Dispatchers.Main).launch {

            Log.e("TAG", "onClick: plAY " + "  ${isPlayingSong}")
            imageView.setPlayPauseIcon_List(isPlayingSong)
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.IO).launch {

                if (isPlayingSong) {
                    val myIntent = Intent(this@playThreadBtnMusicList, AudioPlayerService::class.java)
                    myIntent.putExtra("servicePosition", selectedPosition)
                    myIntent.putExtra("ActionName", "SongPause")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(myIntent)
                    } else {
                        startService(myIntent)
                    }

                    CoroutineScope(Dispatchers.Main).async {

                        onCallback.invoke(false)
                    }
                }else{

                    val myIntent = Intent(this@playThreadBtnMusicList, AudioPlayerService::class.java)
                    myIntent.putExtra("servicePosition", selectedPosition)
                    myIntent.putExtra("ActionName", "SongPlay")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(myIntent)
                    } else {
                        startService(myIntent)
                    }
                    CoroutineScope(Dispatchers.Main).async {
                        onCallback.invoke(true)
                    }



                }


            }



        }

    }


    fun ImageView.setDerableIntoImage(resId: Int)
    {
        setImageDrawable(context.getDrawable(resId))
    }




    fun ImageView.loadSongImage(song_path:String) {

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        getSongThumbnail2(song_path)

        Glide.with(this)
            .load(getSongThumbnail2(song_path))
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_audio_placeholder)
            .into(this)

    }

    fun getSongThumbnail2(songPath: String): Bitmap? {
        var imgByte: ByteArray?
        MediaMetadataRetriever().also {
            try {
                it.setDataSource(songPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imgByte = it.embeddedPicture
            it.release()
        }

        return imgByte?.let { BitmapFactory.decodeByteArray(imgByte, 0, it?.size) }

    }


    fun openPlayerActivity(tag:String="SongspLAYER",
                           context: Context,
                           position: Int,
                           info: SelectedListInfo,
                           realPath: String
    ) {

        Log.e(tag, "openPlayerActivity: ")

        context.startActivity(Intent(context, AudioPlayerActivity::class.java).apply {

            my_listType=info.listType

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(selectedVideo, position)
            putExtra(selectedVideoPath, realPath)
            putExtra(callForPlayerFrom, callPlayerApp)
        })






    }


    fun Context.getAudioThumbnail(audio_Uri: Uri): ByteArray? {

        val mmr = MediaMetadataRetriever()
        val rawArt: ByteArray?
        val art: Bitmap
        val bfo = BitmapFactory.Options()

        mmr.setDataSource(this, audio_Uri)
        rawArt = mmr.embeddedPicture

// if rawArt is null then no cover art is embedded in the file or is not
// recognized as such.

// if rawArt is null then no cover art is embedded in the file or is not
// recognized as such.
        if (null != rawArt) art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.size, bfo)

        return rawArt
    }




    @SuppressLint("Range")
    fun Context.getAlbumArt(albumId:String):String{
        var path: String=""

        val cursor: Cursor? = getContentResolver().query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART),
            MediaStore.Audio.Albums._ID + "=?",
            arrayOf<String>(java.lang.String.valueOf(albumId)),
            null
        )

        if (cursor!!.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
            // do whatever you need to do
        }
        Log.e("TAG", "getAlbumArt: path ")

        return path
    }


    fun ImageView.getAlbumCoveredIcon(albumId:Long){
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        val sArtworkUri = Uri
            .parse("content://media/external/audio/albumart")
        val albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId)


        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                this.context.getContentResolver(), albumArtUri
            )
            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true)


            Glide.with(this)
                .load(bitmap)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.ic_audio_placeholder)
                .into(this)
        } catch (exception: FileNotFoundException) {
            exception.printStackTrace()
            Log.e("TAG", "getAlbumCoveredIcon:  exception  ${exception.toString()}")
            bitmap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.ic_audio_placeholder
            )
            Glide.with(this)
                .load(R.drawable.ic_audio_placeholder)
                .placeholder(circularProgressDrawable)
                .into(this)
        } catch (e: IOException) {

            Log.e("TAG", "getAlbumCoveredIcon:  IOException  ${e.toString()}")

            e.printStackTrace()
        }
    }

    fun Context.getSongTotalDuration(realPath: String?): String {
        val dur = getAudioFileLength(realPath, false)!!.toInt()
        val mns = dur / 60000 % 60000
        val scs = dur % 60000 / 1000
        val songTime = String.format("%02d:%02d", mns, scs)

        return songTime
    }

    fun Context.getSongCurrentTime(duartion:Int): String {

        val mns = duartion / 60000 % 60000
        val scs = duartion % 60000 / 1000
        val songTime = String.format("%02d:%02d", mns, scs)
        return songTime
    }

    fun Context.getAudioFileLength(path: String?, stringFormat: Boolean): String? {
        val stringBuilder = StringBuilder()
        try {
            val uri = Uri.parse(path)
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(this, uri)
            val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val millSecond = duration!!.toInt()
            if (millSecond < 0) return 0.toString() // if some error then we say duration is zero
            if (!stringFormat) return millSecond.toString()
            val hours: Int
            val minutes: Int
            var seconds = millSecond / 1000
            hours = seconds / 3600
            minutes = seconds / 60 % 60
            seconds = seconds % 60
            if (hours > 0 && hours < 10) stringBuilder.append("0").append(hours)
                .append(":") else if (hours > 0) stringBuilder.append(hours).append(":")
            if (minutes < 10) stringBuilder.append("0").append(minutes)
                .append(":") else stringBuilder.append(minutes).append(":")
            if (seconds < 10) stringBuilder.append("0").append(seconds) else stringBuilder.append(
                seconds
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }


