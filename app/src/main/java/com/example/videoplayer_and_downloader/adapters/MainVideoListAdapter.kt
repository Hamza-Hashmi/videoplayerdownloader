package com.example.videoplayer_and_downloader.adapters

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.BaseItem
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.NativeAdViewHolder
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.NativeAdsCallBack
import com.example.videoplayer_and_downloader.databinding.LayoutAdContainerListBinding
import com.example.videoplayer_and_downloader.databinding.RowVideosBinding
import com.example.videoplayer_and_downloader.listeners.onMediaClick
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo
import com.videoplayertest.myhdvideo.mediaplayer.enums.ItemType2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class MainVideoListAdapter(val clickInterface: onMediaClick,var nativeAdId: String, var fbNative: String, var keyPriority: Int = 3 ): RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    NativeAdsCallBack {

    private var imageEntities: MutableList<BaseItem> = ArrayList()
    private var categoryList: MutableList<MediaModel>? = null
    var adsHashMap = HashMap<Int, Any>()


    init {
        this.categoryList = categoryList

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            ItemType2.REAL_ITEM.ordinal -> {
                val binding= RowVideosBinding
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



    override fun getItemViewType(position: Int): Int {
        return if (imageEntities[position].itemType() == ItemType2.REAL_ITEM.ordinal) {
            ItemType2.REAL_ITEM.ordinal
        }
        else {
            ItemType2.NATIVE_AD.ordinal
        }

    }
    override fun getItemCount(): Int {
        return if (imageEntities.isEmpty())
            return 0
        else {
            imageEntities.size
        }
    }


    fun setData( packsList: MutableList<MediaModel>, isNetworkAvilable: Boolean) {

        imageEntities.clear()
        categoryList = packsList
        packsList.forEachIndexed { index, imageEntity ->

            if (isNetworkAvilable ) {
                if (index == 2) {
                    imageEntities.add(NativeItem(null))
                } else if (index != 0 && (index == 2 || (index-2) % 6 == 0))
                    imageEntities.add(NativeItem(null))
                imageEntities.add(MyItemView(imageEntity))
            } else
                imageEntities.add(MyItemView(imageEntity))


        }
    }

    inner class MyViewHolder(val binding: RowVideosBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindImageData(
            media: MediaModel,
            position: Int
        ) {
        val info=   SelectedListInfo(
                listType = deviceItemVideo,
                selectedParent = none,
                media.isVideo
            )

            binding.root.context.let {context->

                val file = File(media.realPath)
                val fileSize = Formatter.formatFileSize(context, file.length())
                val parentFolderName = when {
                    media.isPlaylist -> media.playlistName
                    media.isHistory -> history
                    else -> {
                        val rootName = context.resources.getString(R.string.root)
                        file.parentFile?.let {
                            when {
                                it.exists() -> {
                                    when (it.name) {
                                        "0" -> rootName
                                        else -> it.name
                                    }
                                }
                                else -> rootName
                            }

                        } ?: rootName
                    }
                }
                with(binding)
                {
                    tvName.text = file.name
                    tvSizeQuality.text = fileSize

                        Glide.with(context).load(getUriPath(media.uri))
                            .placeholder(R.drawable.ic_folder_default)
                            .into(ivThumbnail)

                    binding.root.setOnClickListener {
                        clickInterface.onMediaPlayed(
                            position,
                            media,
                            info
                        )
                    }

                    ivMore.setOnClickListener { v ->
                        val wrapper: Context = ContextThemeWrapper(context, R.style.Widget_AppCompat_PopupMenu)
                        val menu = PopupMenu(wrapper, v)
                        menu.inflate(R.menu.video_popup_menu)
                        val playlistMenuItem = menu.menu.findItem(R.id.add_to_playlist)
                        val historyMenuItem = menu.menu.findItem(R.id.remove_from_history)
//                        val favoriteMenuItem = menu.menu.findItem(R.id.add_to_favorites)
                        val renameMenuItem = menu.menu.findItem(R.id.rename)
                        renameMenuItem.isVisible = !androidVersionIs10OrAbove()
                        if (media.isPlaylist)
                            playlistMenuItem.title = context.resources.getString(R.string.remove_from_playlist)
                        else
                            playlistMenuItem.title = context.resources.getString(R.string.add_to_playlist)

//                        if (singleMedia.isFvt)
//                            favoriteMenuItem.title = context.resources.getString(R.string.remove_from_favorites)
//                        else
//                            favoriteMenuItem.title = context.resources.getString(R.string.add_to_favorites)

                        historyMenuItem.isVisible = media.isHistory


                        menu.show()
                        menu.setOnMenuItemClickListener { item ->
                            item?.let {
                                when (it.title) {
                                    context.resources.getString(R.string.play) -> {
                                        clickInterface.onMediaPlayed(
                                            position,
                                            media,
                                            info
                                        )
                                        true
                                    }
                                    context.resources.getString(R.string.add_to_playlist) -> {
                                        clickInterface.onMediaAddedToPlaylist(
                                            position,
                                            media
                                        )
                                        true
                                    }
                                    context.resources.getString(R.string.remove_from_playlist) -> {
                                        clickInterface.onMediaRemovedFromPlaylist(
                                            position,
                                            media,
                                            selectedListInfo = info
                                        )
                                        true
                                    }
                                    context.resources.getString(R.string.remove_from_history) -> {
                                        clickInterface.onMediaRemovedFromHistory(
                                            position,
                                            media,
                                            selectedListInfo = info
                                        )
                                        true
                                    }
                                    context.resources.getString(R.string.rename_file) -> {
                                        clickInterface.onMediaRename(
                                            position,
                                            media,
                                            selectedListInfo = info
                                        )
                                        true
                                    }
                                    context.resources.getString(R.string.delete_file) -> {
                                        clickInterface.onMediaDelete(
                                            position,
                                            media,
                                            selectedListInfo = info
                                        )
                                        true
                                    }
                                    context.resources.getString(R.string.share) -> {
                                        clickInterface.onMediaShare(
                                            position,
                                            media
                                        )
                                        true
                                    }
                                    else -> false
                                }
                            } ?: false
                        }
                    }

                }
            }








        }
    }

    private class NativeItem(var nativeAd: Any?) : BaseItem() {
        override fun itemType(): Int {
            return ItemType2.NATIVE_AD.ordinal
        }
        override fun bindItem(holder: RecyclerView.ViewHolder?, position: Int) {

            (holder as NativeAdViewHolder).setData(nativeAd, position)

        }
        var pos: Int = 0
        public fun setNewPos(mPos: Int) {
            this.pos = mPos
        }
    }



    class MyItemView(var media: MediaModel) : BaseItem() {
        override fun itemType(): Int {
            return ItemType2.REAL_ITEM.ordinal
        }

        override fun bindItem(holder: RecyclerView.ViewHolder?, position: Int) {

            holder as MyViewHolder


            CoroutineScope(Dispatchers.Main).launch {
                holder.binding.root.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.recycler_anim)
                holder.bindImageData(media,  position)
            }

        }
    }

    override fun onNewAdLoaded(nativeAd: Any, position: Int) {
        adsHashMap[position] = nativeAd
    }

    override fun onAdClicked(position: Int) {
        adsHashMap.remove(position)
        notifyDataSetChanged()
    }


}