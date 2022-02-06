package com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.videodownload.appUtils.*
import com.example.videodownload.appUtils.Constants.Companion.KEY_VIDEO_DATA_BUNDLE
import com.example.videodownload.download_feature.lists.MyCompletedVideosClass
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.databinding.ActivityVideoPlayerForWhatsappBinding
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.io.*

class VideoPlayerForWhatsapp : AppCompatActivity(), View.OnClickListener, Player.Listener  {

    private var playbackPosition: Long = 0
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private var audioManager : AudioManager? = null
    var videoFilePath: String? = ""
    var videoFileName: String? = ""
    private var completedVideos : MyCompletedVideosClass? = null
    lateinit var binding : ActivityVideoPlayerForWhatsappBinding
    val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(applicationContext, "exoplayer-sample")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerForWhatsappBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.getBundleExtra(KEY_VIDEO_DATA_BUNDLE)

        videoFilePath = bundle?.getString(Constants.videoFilePath).toString()
        videoFileName = bundle?.getString(Constants.videoTitle).toString()

        //videoFilePath = intent.extras?.getString(Constants.WA_VIDEO_FILEPATH)
        //videoFileName = intent.extras?.getString(Constants.WA_VIDEO_TITLE)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?

        binding.vidoTitle.text = videoFileName
        binding.btnBack.setOnClickListener(this)
        binding.btnShareVideo?.setOnClickListener(this)
        binding.btnSaveVideo?.setOnClickListener(this)
        binding.btnOpenWhatsApp?.setOnClickListener(this)
        completedVideos = MyCompletedVideosClass.load(this@VideoPlayerForWhatsapp)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        videoFilePath?.let { startPlayer(it, "") }
        binding.exoplayerView.player = simpleExoPlayer
        simpleExoPlayer.seekTo(playbackPosition)
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.addListener(this)
        simpleExoPlayer
    }

    private fun startPlayer(Url: String, type: String) {
        val uri = Uri.parse(Url)
        val mediaSource = buildMediaSource(uri, type)
        simpleExoPlayer.prepare(mediaSource, true, false)
    }

    private fun buildMediaSource(uri: Uri, type: String): MediaSource {
        return if (type.equals("dash")) {
            DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        }
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoPlayer.currentPosition
        simpleExoPlayer.release()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        showShortMessage("invalid video ${error.message}")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == Player.STATE_BUFFERING)
            binding.videoProgressBar?.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            binding.videoProgressBar?.visibility = View.INVISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBack -> {
                onBackPressed()
            }
            R.id.btnSaveVideo -> {
                videoFilePath?.let {
                    val fileToCopy = File(it)
                    val folder = getMainStoragePath()
                    Log.e(Constants.LOG_TAG, "onClick: whatsapp file save name ---> " + fileToCopy.name)
                    when {
                        videoAlreadyDownloaded(fileToCopy.name) -> showShortMessage("Video already downloaded")
                        else -> copyFile(fileToCopy, File("$folder/${fileToCopy.name}"))
                    }
                } ?: showShortMessage("Download Failed")
            }

            R.id.btnShareVideo -> {
                videoFilePath?.let {
                    shareVideoFile(File(it))
                } ?: showShortMessage("invalid file")
            }
            R.id.btnOpenWhatsApp -> {
                try {
                    videoFilePath?.let {
                        repostVideoOnWhatsApp(File(it), this)
                    } ?: showShortMessage("invalid video file")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun repostVideoOnWhatsApp(file: File, context: Context) {

        val uri = FileProvider.getUriForFile(context, "com.example.videodownloader"+ ".fileprovider", file)

        Log.e(Constants.LOG_TAG, "repost: Video uri -> $uri", )

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/octet-stream"
        shareIntent.type = "*/*"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

        val isWhatsappInstalled = appInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {
            shareIntent.setPackage("com.whatsapp")
            context.startActivity(Intent.createChooser(shareIntent, "send"))
        } else {
            Toast.makeText(
                context,
                "whatsapp is not installed in your device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun videoAlreadyDownloaded(currentLink: String): Boolean {
        var found = false
        for (video in completedVideos!!.videos) {
            if (video.link == currentLink) {
                found = true
                break
            }
        }
        return found
    }

    private fun copyFile(srcFile: File, dstFile: File): Boolean {
        if (!dstFile.parentFile!!.exists())
            dstFile.parentFile!!.mkdirs()
        if (!dstFile.exists())
            dstFile.createNewFile()
        val `is`: InputStream = FileInputStream(srcFile)
        val os: OutputStream = FileOutputStream(dstFile)
        val buff = ByteArray(1024)
        var len: Int
        while (`is`.read(buff).also { len = it } > 0) {
            os.write(buff, 0, len)
        }
        `is`.close()
        os.close()
        completedVideos!!.addVideo(
            this,
            dstFile.name,
            srcFile.name,
            "whatsapp"
        )
        showShortMessage("Downloaded Successfully")
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}