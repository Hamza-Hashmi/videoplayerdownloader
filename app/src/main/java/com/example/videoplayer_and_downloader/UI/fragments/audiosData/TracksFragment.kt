package com.example.videoplayer_and_downloader.UI.fragments.audiosData

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.UI.activites.AudioPlayerActivity
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adapters.TracksAdapter
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.InterstitialAdUpdated
import com.example.videoplayer_and_downloader.databinding.FragmentTracksBinding
import com.example.videoplayer_and_downloader.models.MusicModel
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File

class TracksFragment : Fragment() {
    var _binding: FragmentTracksBinding? = null
    val binding get() = _binding!!
    private val TAG = "TracksFragment"
    val viewModel: AudioSongsViewModel by sharedViewModel()
    lateinit var adapter: TracksAdapter
  //  lateinit var playListAdapter: MainPlayListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTracksBinding.inflate(layoutInflater)

        CoroutineScope(Dispatchers.IO).launch {

                 }.invokeOnCompletion { CoroutineScope(Dispatchers.Main).launch { } }

      /*  if (!isHideAds){
            activity?.let {
                it.setNativeAd(
                    binding.shimmerViewContainer,
                    binding.refadslayout,
                    binding.refadslayoutFb,
                    binding.adCard,
                    R.layout.native_banner_layout,
                    R.layout.native_banner_fb_layout,
                    it.isAlreadyPurchased(),
                    TAG,
                    mAdPriority,
                    preLoadedNativeAd,
                    getString(R.string.banner_native_id)
                )
            }
        }

*/


        viewModel.tracksList.observe(viewLifecycleOwner) { tracksList ->

            Log.e(TAG, "onCreateView: ${tracksList.size}")
            if (tracksList.size > 0) {
                adapter = TracksAdapter(
                    requireContext(),
                    callback = { pos ->
                        if (itemClickCount >= 2) {
                            itemClickCount = 1

                            InterstitialAdUpdated.getInstance()
                                .showInterstitialAdNew(requireActivity(), onAction = {
                                    onClick(pos, tracksList)
                                })

                        } else {
                            itemClickCount += 1
                            onClick(pos, tracksList)

                        }


                    },
                    showMenu = { v, audioFile, pos ->
                        val popupMenu: PopupMenu = PopupMenu(requireContext(), v)
                        popupMenu.menuInflater.inflate(R.menu.audio_popup_menu, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                            when (item.itemId) {

                                R.id.setRingtone -> {
                                    setAsRingTone(audioFile)
                                }
                                R.id.share_audio -> {
                                    shareFile(audioFile.path!!, requireContext())
                                }
                                R.id.delete -> {
                                    context?.let {
                                        it.deleteAudioFromDevice(
                                            audioFile.path,
                                            pos,
                                            tracksList,
                                            adapter
                                        )
                                    }

                                }


                            }
                            true
                        })
                        popupMenu.show()
                    },
                    getString(R.string.native_id),
                    getString(R.string.fb_native_ad),
                    keyPriority = ad_periority
                )
                binding.allSongsRv.adapter = adapter


                context?.let {
                    adapter.setData(
                        tracksList, it.isInternetConnected()
                    )

                }


                adapter.notifyDataSetChanged()
            }
        }
      return binding.root
    }

    private fun onClick(pos: Int, tracksList: ArrayList<MusicModel>) {
        Intent(requireContext(), AudioPlayerActivity::class.java).also {
            audioListType = "allAudios"
            it.putExtra("position", pos)
            it.putParcelableArrayListExtra("trackList", tracksList)
            //tracksList
            startActivity(it)
        }
    }
/*

    private fun showAddToPlayListDialog(audioFile: MusicModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = LayoutDialogPlaylistBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getMainPlayList()
        }.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch{
                viewModel.allmainPlayList.observe(viewLifecycleOwner, Observer { playList->
                    Log.e("TAG", "showAddToPlayListDialog: ${playList.size}")
                    playListAdapter = MainPlayListAdapter(playList, callback = { playListName, id ->
                        Log.e("TAG", "showAddToPlayListDialog: $playListName $id")

                        CoroutineScope(Dispatchers.IO).launch {
                            val playListItem = playListItems().apply {
                                this.album = audioFile.album
                                this.artist = audioFile.artist
                                this.duration = audioFile.duration
                                this.path = audioFile.path
                                this.title = audioFile.title
                                this.playListId = id
                            }
                            viewModel.addPlayListItem(playListItem)
                        }.invokeOnCompletion {
                            CoroutineScope(Dispatchers.Main).launch {

                                Toast.makeText(requireContext(), "Added to playlist", Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss()
                            }
                        }
                    },showMenu = { view, item, pos ->

                    })

                    dialogBinding.dialogePlayListRv.adapter = playListAdapter
                    adapter.notifyDataSetChanged()
                })

                dialog.show()

            }
        }


    }
*/


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    fun setAsRingTone(model:MusicModel){
        try {
            val k: File = File(model.path, "myRingtone.mp3") // path is a file to /sdcard/media/ringtone


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
            val newUri: Uri? = requireActivity().getContentResolver().insert(uri!!, values)

            RingtoneManager.setActualDefaultRingtoneUri(
                requireContext(),
                RingtoneManager.TYPE_RINGTONE,
                newUri
            )
            Toast.makeText(requireContext(), "set", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Log.e(TAG, "setAsRingTone: $e")
        }

    }
}