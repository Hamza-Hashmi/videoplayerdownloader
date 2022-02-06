package com.example.videoplayer_and_downloader.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.BaseItem
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.NativeAdViewHolder
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.NativeAdsCallBack
import com.example.videoplayer_and_downloader.databinding.LayoutAdContainerListBinding
import com.example.videoplayer_and_downloader.databinding.RowAudioSongsBinding
import com.example.videoplayer_and_downloader.models.MusicModel
import com.videoplayertest.myhdvideo.mediaplayer.enums.ItemType2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.HashMap

class TracksAdapter( val context:Context, val callback:(position:Int) ->Unit, val showMenu:(view:View, audioFile:MusicModel, pos:Int) ->Unit,var nativeAdId: String, var fbNative: String, var keyPriority: Int = 3) :RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    NativeAdsCallBack {

    private var imageEntities: MutableList<BaseItem> = java.util.ArrayList()
    private var categoryList: MutableList<MusicModel>? = null
    var adsHashMap = HashMap<Int, Any>()


    init {
        this.categoryList = categoryList

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType2.REAL_ITEM.ordinal -> {
                val binding= RowAudioSongsBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                MyViewHolder(binding)
            }
            else -> {
                val inflate = LayoutAdContainerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return NativeAdViewHolder(inflate, nativeAdId, fbNative, this, keyPriority)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = imageEntities[position]) {
            is NativeItem -> {

                if (adsHashMap[position] != null) {
                    item.nativeAd = adsHashMap[position] as Any
                }
                item.bindItem(holder, position)
            }
            is MyItemView -> {
                item.bindItem(holder, position)
            }
        }


    }

    override fun getItemCount(): Int {
        return if (imageEntities.isEmpty())
            return 0
        else {
            imageEntities.size
        }
    }

    override fun onNewAdLoaded(nativeAd: Any, position: Int) {
        adsHashMap[position] = nativeAd
    }

    override fun onAdClicked(position: Int) {
        adsHashMap.remove(position)
        notifyDataSetChanged()

    }


    fun setData(packsList: MutableList<MusicModel>, isNetworkAvilable: Boolean) {

        imageEntities.clear()
        categoryList = packsList
        //printLog(tag," " )
        packsList.forEachIndexed { index, imageEntity ->

            if ( isNetworkAvilable) {
                if (index == 2) {
                    imageEntities.add(NativeItem(null))
                } else if (index != 0 && (index == 2 || (index-2) % 6 == 0))
                    imageEntities.add(NativeItem(null))
                imageEntities.add(MyItemView(imageEntity))
            } else
                imageEntities.add(MyItemView(imageEntity))


        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (imageEntities[position].itemType() == ItemType2.REAL_ITEM.ordinal) {
            ItemType2.REAL_ITEM.ordinal
        }
        else {
            ItemType2.NATIVE_AD.ordinal
        }

    }
    private class NativeItem(var nativeAd: Any?) : BaseItem() {
        override fun itemType(): Int {
            return ItemType2.NATIVE_AD.ordinal
        }
        override fun bindItem(holder: RecyclerView.ViewHolder?, position: Int) {

            (holder as NativeAdViewHolder).setData(nativeAd, position)

        }

    }


    class MyItemView(var singleMedia: MusicModel) : BaseItem() {
        override fun itemType(): Int {
            return ItemType2.REAL_ITEM.ordinal
        }

        override fun bindItem(holder: RecyclerView.ViewHolder?, position: Int) {

            holder as MyViewHolder


            CoroutineScope(Dispatchers.Main).launch {
                holder.binding.root.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recycler_anim)
                holder.bindImageData(singleMedia,  position)
            }

        }
    }

    inner class MyViewHolder(val binding: RowAudioSongsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindImageData(
            singleMedia: MusicModel,
            position: Int
        ) {


            Log.e("TAG", "bindImageData: audio $singleMedia " )
            binding.apply {
                this.tvName.text = singleMedia.title
                this.tvAlbum.text = singleMedia.artist
                Glide.with(context).load(File(singleMedia.path)).placeholder(R.drawable.ic_audio_placeholder).into(this.ivSong)

                this.ivMore.setOnClickListener {
                    showMenu(it,singleMedia,position)
                }
            }


           itemView.setOnClickListener {
                callback(position)
            }


        }
    }
}