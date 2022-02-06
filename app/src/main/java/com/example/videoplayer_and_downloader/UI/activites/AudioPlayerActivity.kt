package com.example.videoplayer_and_downloader.UI.activites

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.databinding.ActivityAudioPlayerBinding
import com.example.videoplayer_and_downloader.listeners.MusicPlayerActions
import com.example.videoplayer_and_downloader.models.MusicModel
import com.example.videoplayer_and_downloader.services.AudioPlayerService
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

class AudioPlayerActivity : AppCompatActivity(),
    MusicPlayerActions,
    MediaPlayer.OnCompletionListener,
    ServiceConnection {
    var uri: Uri? = null
    var musicService: AudioPlayerService? = null
    var binding: ActivityAudioPlayerBinding? = null
    var playThread: Thread? = null
    var nextThread: Thread? = null
    var prevThread: Thread? = null
    var shuffle = "false"
    var repeat = "false"

    private val TAG = "MusicPlayerActivity"
    private var mVolumePlaying = true

    val viewModel : AudioSongsViewModel by viewModel()
    override fun onCreate(savedInsatanceState: Bundle?) {
        super.onCreate(savedInsatanceState)
        binding = ActivityAudioPlayerBinding.inflate(
            layoutInflater
        )
        shuffle = ShareHelper.getKey(this@AudioPlayerActivity, "shuffle").toString()
        repeat = ShareHelper.getKey(this@AudioPlayerActivity, "repeat").toString()

        if (TextUtils.isEmpty(repeat)){
            repeat = "false"
        }
        if (TextUtils.isEmpty(shuffle)){
            shuffle = "false"
        }


        binding?.btnBack?.setOnClickListener{
            finish()
        }
        Log.e("TAG", "onCreate: repeat $repeat shuffle $shuffle")

        setContentView(binding!!.root)
        intentMethod


      /*  if (isInternetConnected() && !isAlreadyPurchased()) {

            binding?.adCard?.visibility= View.VISIBLE
            setNativeAd(
                binding?.shimmerViewContainer!!,
                binding?.refadslayout!!,
                binding?.refadslayoutFb!!,
                binding?.adCard!!,
                R.layout.native_banner_layout,
                R.layout.native_banner_fb_layout,
                isAlreadyPurchased(),
                TAG,
                mAdPriority,
                preLoadedNativeAd,
                getString(R.string.banner_native_id)
            )
        }else{

            binding?.adCard?.visibility= View.GONE

        }
*/
        /*onProgressChanged = object : SeekBarOnProgressChanged {


            override fun onProgressChanged(
                waveformSeekBar: WaveformSeekBar,
                progress: Float,
                fromUser: Boolean
            ) {
                TODO("Not yet implemented")
            }
        }*/



        binding!!.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (musicService != null && b) {
                    musicService?.seekTo(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        if (musicService != null) {
            musicService?.seekUpdate()
        }


        binding?.btnShuffle?.setOnClickListener{
            if (!ShareHelper.getKey(this@AudioPlayerActivity, "shuffle").toString().equals("true")) {
                ShareHelper.putKey(this@AudioPlayerActivity, "shuffle", "true")
                shuffle = ShareHelper.getKey(this@AudioPlayerActivity, "shuffle").toString()
                Toast.makeText(this@AudioPlayerActivity, "Shuffle On", Toast.LENGTH_SHORT).show()
                binding!!.btnShuffle.setImageResource(R.drawable.ic_audio_shuffle_on)
            } else {
                ShareHelper.putKey(this@AudioPlayerActivity, "shuffle", "false")
                shuffle = ShareHelper.getKey(this@AudioPlayerActivity, "shuffle").toString()
                Toast.makeText(this@AudioPlayerActivity, "Shuffle Off", Toast.LENGTH_SHORT).show()
                binding!!.btnShuffle.setImageResource(R.drawable.ic_audio_shuffle)
            }
        }

        binding?.apply {
            this.btnRepeat.setOnClickListener {
                if (!ShareHelper.getKey(this@AudioPlayerActivity, "repeat").toString().equals("true")) {
                    ShareHelper.putKey(this@AudioPlayerActivity, "repeat", "true")
                    repeat = ShareHelper.getKey(this@AudioPlayerActivity, "repeat").toString()
                    Toast.makeText(this@AudioPlayerActivity, "Repeat On", Toast.LENGTH_SHORT).show()
                    btnRepeat.setImageResource(R.drawable.ic_audio_repeat_on)
                } else {
                    ShareHelper.putKey(this@AudioPlayerActivity, "repeat", "false")
                    repeat = ShareHelper.getKey(this@AudioPlayerActivity, "repeat").toString()
                    Toast.makeText(this@AudioPlayerActivity, "Repeat Off", Toast.LENGTH_SHORT)
                        .show()
                    btnRepeat.setImageResource(R.drawable.ic_audio_repeat)
                }

            }




        }

        binding?.btnMuteUnMute?.setOnClickListener { v: View? ->
            if (mVolumePlaying) {
                Log.d("TAG", "setVolume OFF")
                binding?.btnMuteUnMute?.setImageResource(R.drawable.ic_volume)
                musicService?.mute()
            } else {
                Log.d("TAG", "setVolume ON")
                binding?.btnMuteUnMute?.setImageResource(R.drawable.ic_unmute)
                musicService?.unMute()
            }
            mVolumePlaying = !mVolumePlaying
        }


    }

    override fun onResume() {
        super.onResume()


        // biniding service
        val intent = Intent(this@AudioPlayerActivity, AudioPlayerService::class.java)
        // bind service method accept 3 arguments intent,connetion(this),flag
        bindService(intent, this@AudioPlayerActivity, BIND_AUTO_CREATE)
        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
    }

    override fun onPause() {
        super.onPause()
        // unbind service
        unbindService(this)
    }

    private fun playThreadBtn() {
        playThread = object : Thread() {
            override fun run() {
                super.run()
                binding!!.btnPlayPause.setOnClickListener {
                    Log.e("TAG", "onClick: plAY " + " ELSE CAll")
                    playPauseBtnClick()
                }
            }
        }
        playThread?.start()
    }

    override fun playPauseBtnClick() {
        if (musicService?.isPlaying() == true) {
            binding!!.btnPlayPause.setImageResource(R.drawable.ic_audio_play)
            musicService?.pause()
            musicService?.showNotification(R.drawable.ic_notification_play)
            binding!!.seekbar.max = musicService?.getDuration()!!
            if (musicService != null) {
                musicService?.seekUpdate()
            }
        } else {
            musicService?.showNotification(R.drawable.ic_notification_pause)
            binding!!.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
            musicService?.start()
            binding!!.seekbar.max = musicService?.getDuration()!!
            if (musicService != null) {
                musicService?.seekUpdate()
            }
        }
    }

    private fun nextThreadBtn() {
        nextThread = object : Thread() {
            override fun run() {
                super.run()
                binding!!.btnNext.setOnClickListener {
                    Log.e("TAG", "onClick: next " + " ELSE CAll")
                    btnNextClicked()
                }
            }
        }
        nextThread?.start()
    }

    override fun btnNextClicked() {
        if (musicService?.isPlaying() == true) {
            musicService?.stop()
            musicService?.release()

            if (shuffle.equals("true") &&  repeat.equals("false")) {
                position = getRandom(listSong!!.size - 1);
            } else if (shuffle.equals("false") && repeat.equals("false")) {
                position = (position + 1) % listSong!!.size
            }
            uri = Uri.parse(listSong!![position].path)
            Log.e("TAG", "btnNextClicked: $position" )
            Log.e("TAG", "btnNextClicked: $uri" )



            musicService?.createMediaPlayer(position)
            metaData()
            binding!!.audioNameTv.text =
                listSong!![position].title
            binding!!.seekbar.max = musicService?.getDuration()!!

            //isSongFavorite(listSong.get(position).getPath());
            if (musicService != null) {
                musicService?.seekUpdate()
            }
            musicService?.onCompleted()
            musicService?.showNotification(R.drawable.ic_notification_pause)
            musicService?.start()
        } else {
            Log.e("TAG", "btnNextClicked: " + "  else call ")
            musicService?.stop()
            musicService?.release()

            if (shuffle.equals("true") && repeat.equals("false")) {
                position = getRandom(listSong!!.size - 1)
            } else if (!shuffle.equals("true")  && repeat.equals("false")) {
                position = (position + 1) % listSong!!.size
            }
            uri = Uri.parse(listSong!![position].path)
            musicService?.createMediaPlayer(position)
            metaData()
            binding!!.audioNameTv.text =
                listSong!![position].title

            binding!!.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
            //artistName.setText(listSong.get(position).getArtist());
            binding!!.seekbar.max = musicService?.getDuration()!!
            if (musicService != null) {
                musicService?.seekUpdate()
            }
            musicService?.onCompleted()
            musicService?.showNotification(R.drawable.ic_notification_play)
            musicService?.start()
        }
    }

    private fun getRandom(i: Int): Int {
        val random = Random()
        return random.nextInt(i + 1)
    }

    private fun prevThreadBtn() {
        prevThread = object : Thread() {
            override fun run() {
                super.run()
                binding!!.btnPrev.setOnClickListener {
                    Log.e("prev", "onClick: ")
                    Log.e("TAG", "onClick: prev " + " ELSE CAll")
                    btnPreviousClicked()
                }
            }
        }
        prevThread?.start()
    }

    override fun btnPreviousClicked() {
        if (musicService?.isPlaying() == true) {
            musicService?.stop()
            musicService?.release()


            //  Log.e("repeat", "btnPreviousClicked: " + repeat);

            if (shuffle.equals("true") && repeat.equals("false")){
                position = getRandom(listSong!!.size-1);
            }
            else if (!shuffle.equals("true")  && repeat.equals("false")){
                position = if (position - 1 < 0) listSong!!.size - 1 else position - 1
            }


            // repeate button is enabaled no need to change posittion
            uri = Uri.parse(listSong!![position].path)
            musicService?.createMediaPlayer(position)
            metaData()
            binding!!.audioNameTv.text =
                listSong!![position].title
            //artistName.setText(listSong.get(position).getArtist());
            binding!!.seekbar.max = musicService?.getDuration()!!
            //isSongFavorite(listSong.get(position).getPath());
            if (musicService != null) {
                musicService?.seekUpdate()
            }
            musicService?.showNotification(R.drawable.ic_notification_pause)
            musicService?.onCompleted()
            musicService?.start()
        } else {
            musicService?.stop()
            musicService?.release()

            if (shuffle.equals("true") && repeat.equals("false")) {
                position = getRandom(listSong!!.size - 1);

            } else if (!shuffle.equals("true")  && repeat.equals("false")) {

                position = if (position - 1 < 0) listSong!!.size - 1 else position - 1
            }

            // repeate button is enabaled no need to change posittion
            uri = Uri.parse(listSong!![position].path)
            musicService?.createMediaPlayer(position)
            metaData()
            binding!!.audioNameTv.text =
                listSong!![position].title
            binding!!.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
            //artistName.setText(listSong.get(position).getArtist());
            binding!!.seekbar.max = musicService?.getDuration()!!
            if (musicService != null) {
                musicService?.seekUpdate()
            }
            musicService?.showNotification(R.drawable.ic_notification_play)
            musicService?.onCompleted()
            musicService?.start()
        }
    }

    override fun updateSeekbar(currentDuration: Int) {
        binding!!.seekbar.progress = currentDuration

        val handler = Handler(Looper.getMainLooper()) // write in onCreate function
        handler.post(Runnable { binding?.tvSpendTime?.setText(convertMillisToTime(currentDuration)) })

    }

    fun convertMillisToTime(milli:Int):String{

        var millis = milli.toLong()
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
    }

    override fun setSeekBarDuration(duration: Int) {
        binding!!.seekbar.progress = 0
        binding!!.seekbar.max = duration
    }

    private val intentMethod: Unit
        private get() {
            listSong!!.clear()
            position = intent.getIntExtra("position", position)

            listSong = this.intent.getParcelableArrayListExtra("trackList")
            Log.e("TAG", "getIntentMethod: " + listSong!!.size)


/*
            Log.e(TAG, "list of song ${listSong!![position].album}"  )
            Log.e(TAG, "list of song ${listSong!![position].title}"  )
            Log.e(TAG, "list of song ${listSong!![position].artist}"  )
            Log.e(TAG, "list of song ${listSong!![position].duration}"  )
            Log.e(TAG, "list of song ${listSong!![position].path}"  )
*/

            if (listSong != null) {
                try{
                    binding!!.audioNameTv.text = listSong!![position].title
                    Log.e("listSon", "" + listSong!!.size)
                    //artistName.setText(listSong.get(position).getArtist());
                    binding!!.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
                    uri = Uri.parse(listSong!![position].path)
                    Log.e("uri", "" + uri)

                    isRepeatEnabled();

                    isShuffleEnabled();

                }
                catch (e:java.lang.Exception){
                    try{
                        position--
                        binding!!.audioNameTv.text = listSong!![position].title
                        Log.e("listSon", "" + listSong!!.size)
                        //artistName.setText(listSong.get(position).getArtist());
                        binding!!.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
                        uri = Uri.parse(listSong!![position].path)
                        Log.e("uri", "" + uri)

                        isRepeatEnabled();

                        isShuffleEnabled();

                    }catch (e:Exception){
                        position = position/2
                        binding!!.audioNameTv.text = listSong!![position].title
                        Log.e("listSon", "" + listSong!!.size)
                        //artistName.setText(listSong.get(position).getArtist());
                        binding!!.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
                        uri = Uri.parse(listSong!![position].path)
                        Log.e("uri", "" + uri)

                        isRepeatEnabled();

                        isShuffleEnabled();
                    }

                }
            }
            Log.e("first", "getIntentMethod: ")
            try{

                val intetn = Intent(this@AudioPlayerActivity, AudioPlayerService::class.java)
                intetn.putExtra("servicePosition", position)
                startService(intetn)
            }catch (e:Exception){
                Log.e("TAG", " ${e.message}" )
            }

        }

    /*@JvmName("onCompletion1")
    override fun onCompletion(mp: MediaPlayer) {

    }*/

    override fun onStop() {
        playThread!!.interrupt()
        prevThread!!.interrupt()
        nextThread!!.interrupt()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
        val myBinder = iBinder as AudioPlayerService.MyBinder
        musicService = myBinder.service
        musicService?.setCallBack(this)
        Log.e("TAG", "onServiceConnected: ")


        //seekBar.setProgress(0);
        //seekBar.setMax(musicService.getDuration());
        metaData()

        musicService?.showNotification(R.drawable.ic_notification_pause)
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        musicService = null
    }

    fun metaData() {
        listSong?.get(position)?.duration?.toInt()?.let {
            val mns = it / 60000 % 60000
            val scs = it % 60000 / 1000
            val songTime = String.format("%02d:%02d", mns, scs)
            binding!!.tvTotalLength.text = songTime
        }

    }

    companion object {
        var position = -1
        @JvmField
        var listSong: ArrayList<MusicModel>? = ArrayList()
    }

    private fun isShuffleEnabled() {
        if (shuffle.equals("true", ignoreCase = true)) {
            binding?.btnShuffle?.setImageResource(R.drawable.ic_audio_shuffle_on)
        } else {
            binding?.btnShuffle?.setImageResource(R.drawable.ic_audio_shuffle)
        }
    }

    private fun isRepeatEnabled() {
        if (repeat.equals("true", ignoreCase = true)) {
            binding?.btnRepeat?.setImageResource(R.drawable.ic_audio_repeat_on)
        } else {
            binding?.btnRepeat?.setImageResource(R.drawable.ic_audio_repeat)
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (musicService != null) {
            btnNextClicked()
            musicService!!.stop()
            musicService!!.release()
            Log.e("mainmm", "onCompletion: ")
            musicService!!.createMediaPlayer(position)
            musicService!!.start()
            musicService!!.onCompleted()
        }
    }


}