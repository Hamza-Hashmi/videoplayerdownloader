package com.example.videoplayer_and_downloader.viewHolders

import android.content.Context
import android.text.format.Formatter
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.databinding.RowAudioSongsBinding
import com.example.videoplayer_and_downloader.databinding.RowNativeAdBinding
import com.example.videoplayer_and_downloader.databinding.RowVideosBinding
import com.example.videoplayer_and_downloader.listeners.onMediaClick
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo
import com.google.android.gms.ads.nativead.NativeAd
import java.io.File

class MyVideoViewHolder(
    itemView: View,
    private val itemLayout: Int
) :
    RecyclerView.ViewHolder(itemView) {

/*
    private var videoItemHorizontalListBinding: VideoItemHorizontalListBinding? = null
    private var videoItemGridBinding: VideoItemGridBinding? = null
*/
    private var videoRowBinding: RowVideosBinding? = null
    private var rowNativeAdBinding: RowNativeAdBinding? = null
    private var songsItemBinding: RowAudioSongsBinding? = null

    /*   private var videoItemPlayerListBinding: VideoItemPlayerListBinding? = null
    private var songsItemPlayerListBinding: SongsItemPlayerListBinding? = null
*/
    fun bind(
     media: MediaModel,
     context: Context,
     clickInterface: onMediaClick,
     position: Int, info: SelectedListInfo, size: Int, nativeAd: NativeAd?
    ) {

        if (media.name != adItem) {
            itemView.setOnClickListener {
                clickInterface.onMediaPlayed(position, media, info)
            }
        }
        when (itemLayout) {
        /*    R.layout.video_item_horizontal_list -> {
                videoItemHorizontalListBinding = VideoItemHorizontalListBinding.bind(itemView)
            }
            R.layout.video_item_grid -> {
                videoItemGridBinding = VideoItemGridBinding.bind(itemView)
            }*/
            R.layout.row_videos -> {
                videoRowBinding = RowVideosBinding.bind(itemView)
            }
            R.layout.row_audio_songs -> {
                songsItemBinding = RowAudioSongsBinding.bind(itemView)
            }
            R.layout.row_native_ad -> {
                rowNativeAdBinding = RowNativeAdBinding.bind(itemView)
            }
           /* R.layout.video_item_player_list -> {
                videoItemPlayerListBinding = VideoItemPlayerListBinding.bind(itemView)
            }

            R.layout.songs_item_player_list -> {
                songsItemPlayerListBinding = SongsItemPlayerListBinding.bind(itemView)
            }
          */
        }
     /*   videoItemHorizontalListBinding?.let {
            bindMyView(
                tvName = it.tvName,
                ivThumbnail = it.ivThumbnail,
                tvSizeQuality = it.tvSizeQuality,
                ivMore = it.ivMore,
                context = context,
                singleMedia = singleMedia,
                clickInterface = clickInterface,
                position = position,
                info = info
            )
        }
        videoItemGridBinding?.let {

            if (position == size - 1)
                it.ivMargin.visibility = View.VISIBLE
            else
                it.ivMargin.visibility = View.GONE


            bindMyView(
                tvName = it.tvName,
                ivThumbnail = it.ivThumbnail,
                tvSizeQuality = it.tvSizeQuality,
                ivMore = it.ivMore,
                ivMargin = it.ivMargin,
                context = context,
                singleMedia = singleMedia,
                clickInterface = clickInterface,
                position = position,
                info = info,
                size = size
            )
        }*/
        videoRowBinding?.let {

            if (position == size - 1)
                it.ivMargin.visibility = View.VISIBLE
            else
                it.ivMargin.visibility = View.GONE


            bindMyView(
                tvName = it.tvName,
                ivThumbnail = it.ivThumbnail,
                tvSizeQuality = it.tvSizeQuality,
                ivMore = it.ivMore,
                ivMargin = it.ivMargin,
                context = context,
                media = media,
                clickInterface = clickInterface,
                position = position,
                info = info,
                size = size
            )
        }

    /* videoItemPlayerListBinding?.let {
            if (position == size - 1)
                it.ivMargin.visibility = View.VISIBLE
            else
                it.ivMargin.visibility = View.GONE


            it.ivMore.visibility = View.GONE
            bindMyView(
                tvName = it.tvName,
                ivThumbnail = it.ivThumbnail,
                tvSizeQuality = it.tvSizeQuality,
                ivMore = it.ivMore,
                context = context,
                singleMedia = singleMedia,
                clickInterface = clickInterface,
                position = position,
                info = info
            )
        }

    */
        songsItemBinding?.let {
         /*   if (position == size - 1)
                it.ivMargin.visibility = View.VISIBLE
            else
                it.ivMargin.visibility = View.GONE
*/

            if (position == size - 1)
                //it.separator.visibility = View.GONE

            bindMyView(
                tvName = it.tvName,
                ivMore = it.ivMore,
                context = context,
                media = media,
                clickInterface = clickInterface,
                position = position,
                info = info
            )
        }

     /*   songsItemPlayerListBinding?.let {


            if (position == size - 1)
                it.ivMargin.visibility = View.VISIBLE
            else
                it.ivMargin.visibility = View.GONE

            if (position == size - 1)
                it.separator.visibility = View.GONE

            bindMyView(
                it.tvName,
                ivMore = it.ivMore,
                context = context,
                singleMedia = singleMedia,
                clickInterface = clickInterface,
                position = position,
                info = info
            )
        }
*/
        rowNativeAdBinding?.let {
            if (position == size - 1)
                it.ivMargin.visibility = View.VISIBLE
            else
                it.ivMargin.visibility = View.GONE
            nativeAd?.let { ad ->
              /*  showLoadedNativeAd(
                    context,
                    it.listNativeAdHolder,
                    R.layout.native_ad_layout,
                    ad
                )*/
            }
        }


    }

    private fun bindMyView(
        tvName: TextView,
        ivThumbnail: ImageView? = null,
        tvSizeQuality: TextView? = null,
        ivMore: ImageView,
        ivMargin: ImageView? = null,
        context: Context,
        media: MediaModel,
        clickInterface: onMediaClick,
        position: Int,
        info: SelectedListInfo,
        size: Int? = null
    ) {
        size?.let { totalSize ->
            ivMargin?.let { ivGap ->
                if (position == totalSize - 1)
                    ivGap.visibility = View.VISIBLE
                else
                    ivGap.visibility = View.GONE
            }
        }

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

        val quality = "360p"
        tvName.text = file.name
        tvSizeQuality?.text =
            context.resources.getString(R.string.real_size_quality, quality, fileSize)
        ivThumbnail?.let {
            Glide.with(context).load(getUriPath(media.uri))
                .placeholder(R.drawable.iv_video_place_holder)
                .into(it)
        }
        ivMore.setOnClickListener { v ->
            val wrapper: Context = ContextThemeWrapper(context, R.style.Widget_AppCompat_PopupMenu)
            val menu = PopupMenu(wrapper, v)
            menu.inflate(R.menu.video_popup_menu)
            val playlistMenuItem = menu.menu.findItem(R.id.add_to_playlist)
            val historyMenuItem = menu.menu.findItem(R.id.remove_from_history)
            val renameMenuItem = menu.menu.findItem(R.id.rename)
            renameMenuItem.isVisible = !androidVersionIs10OrAbove()
            if (media.isPlaylist)
                playlistMenuItem.title = context.resources.getString(R.string.remove_from_playlist)
            else
                playlistMenuItem.title = context.resources.getString(R.string.add_to_playlist)


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

        if (MySharedPreferences(context).getShowVideoNameStatus())
            tvName.visibility = View.VISIBLE
        else
            tvName.visibility = View.INVISIBLE
    }


}
