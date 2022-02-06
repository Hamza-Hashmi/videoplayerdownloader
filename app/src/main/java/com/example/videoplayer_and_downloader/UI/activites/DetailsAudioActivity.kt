package com.example.videoplayer_and_downloader.UI.activites

import android.content.ContentValues
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.getNativeAdObject
import com.example.videoplayer_and_downloader.databinding.*
import com.example.videoplayer_and_downloader.models.MusicModel
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import smartobject.videoplayer.supervideoplayer.hdvideoplayer.videoeditor.adaptters.DetailsAudioAdapter
import java.io.File
import java.lang.Exception

class DetailsAudioActivity : AppCompatActivity() {
    private val TAG = "DetailsAudioActivity"
    lateinit var binding: DetailedActivityBinding
    lateinit var adapter: DetailsAudioAdapter
    var filterList: ArrayList<MusicModel> = ArrayList()
   /* lateinit var playListAdapter: MainPlayListAdapter
   */ val viewModel: AudioSongsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailedActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val which = this.intent.getStringExtra("which")
        val type = this.intent.getStringExtra("type")
        Log.e(TAG, "onCreate: type $type" )

        viewModel.getAllTracksList()

        if (isInternetConnected() && !isAlreadyPurchased() &&!isHideAd) {

            loadBottomNativeAd()
        }else{

            binding.layoutDetailAudioAdContainer.visibility= View.GONE

        }

        viewModel.tracksList.observe(this, Observer { list ->

            Log.e(TAG, "onCreate: listSize", )
            if (list.size > 0) {
                binding.detailRv.visibility = View.VISIBLE
                binding.noDatIv.visibility = View.GONE
                binding.noDataTv.visibility = View.GONE
                when (type) {
                    "album" -> {
                        for (music in list) {
                            if (music.album.equals(which)) {
                                filterList.add(music)
                            }
                        }
                        adapter =
                            DetailsAudioAdapter(
                                filterList,
                                this@DetailsAudioActivity,
                                callback = { pos ->

                                    Intent(
                                        this@DetailsAudioActivity,
                                        AudioPlayerActivity::class.java
                                    ).also {

                                        //audioListType = "allAudios"
                                        it.putExtra("position", pos)
                                        it.putParcelableArrayListExtra("trackList", filterList)
                                        //tracksList
                                        startActivity(it)
                                    }
                                },
                                showMenu = { view, audioFile, pos ->

                                    showMenu(view, audioFile, pos)
                                },
                                "details"
                            )
                        binding.detailRv.adapter = adapter
                        adapter.notifyDataSetChanged()

                    }
                    "artist" -> {
                        for (music in list) {
                            if (music.artist.equals(which)) {
                                filterList.add(music)
                            }
                        }
                        adapter =
                            DetailsAudioAdapter(
                                filterList,
                                this@DetailsAudioActivity,
                                callback = { pos ->

                                    Intent(
                                        this@DetailsAudioActivity,
                                        AudioPlayerActivity::class.java
                                    ).also {

                                        //audioListType = "allAudios"
                                        it.putExtra("position", pos)
                                        it.putParcelableArrayListExtra("trackList", filterList)
                                        //tracksList
                                        startActivity(it)
                                    }
                                },
                                showMenu = { view, audioFile, pos ->

                                    /*val popupMenu: PopupMenu = PopupMenu(this@DetailsAudioActivity,view)
                            popupMenu.menuInflater.inflate(R.menu.audio_popup_menu,popupMenu.menu)
                            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                                when(item.itemId) {
                                    R.id.addToPlayList ->
                                        Toast.makeText(this@DetailsAudioActivity, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                                    R.id.details ->
                                        Toast.makeText(this@DetailsAudioActivity, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                                    R.id.addFavorite ->{
                                        Log.e("TAG", "onCreateView: add fav" )

                                        CoroutineScope(Dispatchers.IO).launch {
                                            var fav = Favorite().apply {
                                                this.path = audioFile.path
                                                this.duration = audioFile.duration
                                                this.title = audioFile.title
                                                this.artist = audioFile.artist
                                                this.album = audioFile.album
                                            }

                                            viewModel.addFavourite(fav)
                                        }.invokeOnCompletion {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                Toast.makeText(this@DetailsAudioActivity, "favorite save", Toast.LENGTH_SHORT).show()

                                            }
                                        }

                                    }

                                }
                                true
                            })
                            popupMenu.show()
    */
                                    showMenu(view, audioFile, pos)
                                },
                                "details"
                            )
                        binding.detailRv.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }

                }
            }else{
                binding.detailRv.visibility = View.GONE
                binding.noDatIv.visibility = View.VISIBLE
                binding.noDataTv.visibility = View.VISIBLE
            }
        })

    }

    private fun showMenu(view: View, audioFile: MusicModel, pos: Int) {
        val popupMenu: PopupMenu = PopupMenu(this@DetailsAudioActivity, view)
        popupMenu.menuInflater.inflate(R.menu.audio_popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.setRingtone ->{
                     setAsRingTone(audioFile)
                }

                R.id.share_audio -> {
                    shareFile(audioFile.path!!, this@DetailsAudioActivity)
                }
                R.id.delete -> {
                    deleteAudioFromDeviceDetails(audioFile.path,pos,filterList,adapter)
                }
            }
            true
        })
        popupMenu.show()

    }



    fun setAsRingTone(model:MusicModel){
      try {
          val k: File = File(model.path, model.title) // path is a file to /sdcard/media/ringtone


          val values = ContentValues()
          values.put(MediaStore.MediaColumns.DATA, k.absolutePath)
          values.put(MediaStore.MediaColumns.TITLE, "My Song title")
          values.put(MediaStore.MediaColumns.SIZE, 215454)
          values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
          values.put(MediaStore.Audio.Media.ARTIST, "Madonna")
          values.put(MediaStore.Audio.Media.DURATION, 230)
          values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
          values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
          values.put(MediaStore.Audio.Media.IS_ALARM, false)
          values.put(MediaStore.Audio.Media.IS_MUSIC, false)

//Insert it into the database

//Insert it into the database
          val uri: Uri? = MediaStore.Audio.Media.getContentUriForPath(k.absolutePath)
          val newUri: Uri? = this.getContentResolver().insert(uri!!, values)

          RingtoneManager.setActualDefaultRingtoneUri(
              this@DetailsAudioActivity,
              RingtoneManager.TYPE_RINGTONE,
              newUri
          )
          Toast.makeText(this@DetailsAudioActivity, "set", Toast.LENGTH_SHORT).show()
      }catch (e:Exception){
          Log.e(TAG, "setAsRingTone: $e" )
      }
    }

    private fun loadBottomNativeAd() {
     getNativeAdObject("VideoFolderTopNatvie", NativeAdOptions.ADCHOICES_TOP_LEFT, getString(
            R.string.native_id), getString(R.string.fb_native_ad),
            ad_periority, { nativeAdO->

                    nativeAdO?.let {
                        when (it) {
                            is NativeAd -> {
                                val inflate = FbExistScreenAdLayoutBinding.inflate(LayoutInflater.from(this@DetailsAudioActivity), binding.layoutDetailAudioAdContainer, false)
                                populateFbNativeBanner(it, inflate, binding.layoutDetailAudioAdContainer)
                            }
                            is com.google.android.gms.ads.nativead.NativeAd -> {
                                val inflate = NativeExitScreenAdsBinding.inflate(LayoutInflater.from(this@DetailsAudioActivity), binding.layoutDetailAudioAdContainer, false)
                                populateNativeBanner(it, inflate, binding.layoutDetailAudioAdContainer)
                            }
                            else -> {

                            }
                        }
                    }
            } , {
            })
    }
    private fun populateNativeBanner(nativeAd: com.google.android.gms.ads.nativead.NativeAd, adBinding: NativeExitScreenAdsBinding, nativeContainer: FrameLayout) {

        nativeContainer.removeAllViews()
        nativeContainer.addView(adBinding.root)
        val rootView = adBinding.root

        val mediaView: com.google.android.gms.ads.nativead.MediaView = adBinding.adMedia//findViewById(R.id.bannerMedia)

        mediaView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                try {
                    if (child is ImageView) {
                        child.adjustViewBounds = true
                        child.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                } catch (e: Exception) {
                }
            }

            override fun onChildViewRemoved(parent: View, child: View) {}
        })
        rootView.mediaView = mediaView

        // Set other ad assets.
        rootView.headlineView = adBinding.adHeadline//findViewById(R.id.tvAdmobTitle)
        rootView.bodyView = adBinding.adBody//findViewById(R.id.ad_body)
        rootView.callToActionView = adBinding.adCallToAction//findViewById(R.id.ad_call_to_action)
        rootView.iconView = adBinding.adAppIcon//findViewById(R.id.ad_app_icon)
        rootView.advertiserView = adBinding.adBody//findViewById(R.id.ad_body)

        // The headline is guaranteed to be in every UnifiedNativeAd.
        (rootView.headlineView as TextView).text = nativeAd.headline

        // check before trying to display them.
        if (nativeAd.body == null) {
            rootView.bodyView.visibility = View.INVISIBLE
        } else {
            rootView.bodyView.visibility = View.VISIBLE
            (rootView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            rootView.callToActionView.visibility = View.INVISIBLE
        } else {
            rootView.callToActionView.visibility = View.VISIBLE
            (rootView.callToActionView as TextView).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            rootView.iconView.visibility = View.INVISIBLE
        } else {
            (rootView.iconView as ImageView).setImageDrawable(nativeAd.icon.drawable)
            rootView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            rootView.advertiserView.visibility = View.INVISIBLE
        } else {
            (rootView.advertiserView as TextView).text = nativeAd.advertiser
            rootView.advertiserView.visibility = View.VISIBLE
        }
        rootView.setNativeAd(nativeAd)
    }
    private fun populateFbNativeBanner(nativeAd: NativeAd?, fbItem: FbExistScreenAdLayoutBinding, nativeContainer: FrameLayout) {

        nativeContainer.removeAllViews()
        nativeContainer.addView(fbItem.root)

        nativeAd?.let {
            val nativeAdLayout = fbItem.fbNativeAdView
            val llAdChoiceContainer = fbItem.adChoicesContainer
            val tvAdTitle = fbItem.fbAdHeadline
            val fbMediaView = fbItem.fbAdMedia
            val tvAdBody = fbItem.fbAdBody
            val btnAdCallToAction = fbItem.fbAdCallToAction
            val ivAdIcon = fbItem.fbAdAppIcon
            nativeAd.unregisterView()
            // Add the AdOptionsView
            val adOptionsView = AdOptionsView(fbItem.fbAdHeadline.context, nativeAd, nativeAdLayout)
            llAdChoiceContainer?.removeAllViews()
            llAdChoiceContainer?.addView(adOptionsView, 0)
            // Set the Text.
            tvAdTitle?.text = nativeAd.advertiserName
            tvAdBody?.text = nativeAd.adBodyText
            if (nativeAd.hasCallToAction()) {
                btnAdCallToAction?.visibility = View.VISIBLE
            } else btnAdCallToAction?.visibility = View.GONE

            btnAdCallToAction?.text = nativeAd.adCallToAction

            // Create a list of clickable views
            val clickableViews = ArrayList<View>()
            clickableViews.add(fbMediaView)
            clickableViews.add(ivAdIcon)
            clickableViews.add(tvAdTitle)
            clickableViews.add(btnAdCallToAction)
            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(
                nativeAdLayout,
                fbMediaView,
                ivAdIcon,
                clickableViews)
        }
    }

}