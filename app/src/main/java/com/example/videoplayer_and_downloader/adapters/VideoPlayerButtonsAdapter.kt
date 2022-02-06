package com.example.videoplayer_and_downloader.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.databinding.VideoplayerBottomBarBinding
import com.videoplayertest.myhdvideo.mediaplayer.app_data.myMedia.BottomBarItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoPlayerButtonsAdapter(private val clickInterface: VideoPlayerItemClick, private val context: Context) :
    RecyclerView.Adapter<VideoPlayerButtonsAdapter.ViewHolder>() {

    private var itemList = ArrayList<BottomBarItem>()
    private var isPlaying = false
    private var isFvt = false

    fun getPortraitList(): ArrayList<BottomBarItem> {

/*
        val playItem = if (isPlaying)
            BottomBarItem(getStringHere(R.string.pause), R.drawable.exo_styled_controls_pause)
        else
            BottomBarItem(getStringHere(R.string.play), R.drawable.ic_play)
*/

        return arrayListOf(

            BottomBarItem(getStringHere(R.string.speed),R.drawable.ic_fast_forward),
            BottomBarItem(getStringHere(R.string.repeat),R.drawable.ic_repeat_video),
            BottomBarItem(getStringHere(R.string.previous_video),R.drawable.ic_prev_video),
           // BottomBarItem(R.drawable.ic_play),
            BottomBarItem(getStringHere(R.string.next_video),R.drawable.ic_next_video),
            BottomBarItem(getStringHere(R.string.orientation),R.drawable.ic_rotate)
        )
    }


    fun getLandscapeList(): ArrayList<BottomBarItem> {
        val playItem = if (isPlaying)
            BottomBarItem(getStringHere(R.string.pause), R.drawable.ic_pause_video)
        else
            BottomBarItem(getStringHere(R.string.play), R.drawable.ic_play)

        return arrayListOf(
            BottomBarItem(getStringHere(R.string.speed),R.drawable.ic_fast_forward),
            BottomBarItem(getStringHere(R.string.repeat),R.drawable.ic_repeat_video),
            BottomBarItem(getStringHere(R.string.previous_video),R.drawable.ic_prev_video),
            playItem,
            BottomBarItem(getStringHere(R.string.next_video),R.drawable.ic_next_video),
            BottomBarItem(getStringHere(R.string.shuffle_string),R.drawable.ic_shuffle),
            BottomBarItem(getStringHere(R.string.orientation),R.drawable.ic_rotate)

        )
    }

    private fun getStringHere(resId: Int): String {
        return context.resources.getString(resId)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = VideoplayerBottomBarBinding.bind(itemView)
        fun bind(
            item: BottomBarItem,
            clickInterface: VideoPlayerItemClick,
            context: Context
        ) {

            itemView.setOnClickListener {
                clickInterface.onItemClick(item)
            }
            binding.apply {
                image.setImageResource(item.icon)
                /*title.text = item.title
                when (item.title) {
                    context.resources.getString(R.string.play), context.resources.getString(R.string.pause) -> {
                        image.layoutParams.width =
                            context.resources.getDimensionPixelSize(R.dimen._35sdp)
                        image.layoutParams.height =
                            context.resources.getDimensionPixelSize(R.dimen._35sdp)
                    }
                    context.resources.getString(R.string.previous_video), context.resources.getString(
                        R.string.next_video
                    ) -> {
                        image.layoutParams.width =
                            context.resources.getDimensionPixelSize(R.dimen._18sdp)
                        image.layoutParams.height =
                            context.resources.getDimensionPixelSize(R.dimen._18sdp)
                    }
                    else -> {
                        image.layoutParams.width =
                            context.resources.getDimensionPixelSize(R.dimen._15sdp)
                        image.layoutParams.height =
                            context.resources.getDimensionPixelSize(R.dimen._15sdp)
                    }
                }*/
                image.requestLayout()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.videoplayer_bottom_bar, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], clickInterface, context)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    interface VideoPlayerItemClick {
        fun onItemClick(item: BottomBarItem)
    }

    fun updateList(isLandscape: Boolean, isPlaying: Boolean) {
        this.isPlaying = isPlaying
        this.isFvt = isFvt
        CoroutineScope(Dispatchers.Main).launch {
            itemList = if (isLandscape)
                getLandscapeList()
            else
                getPortraitList()
        }.invokeOnCompletion {
            notifyDataSetChanged()
        }
    }


    /*fun updateFvt(isFvt: Boolean) {
        this.isFvt = isFvt
        val iconToShow = if (isFvt)
            R.drawable.ic_fav_select
        else
            R.drawable.ic_fav_un_select
        this.itemList[4] = BottomBarItem(getStringHere(R.string.favorite), iconToShow)
        notifyItemChanged(4)
    }*/


}

