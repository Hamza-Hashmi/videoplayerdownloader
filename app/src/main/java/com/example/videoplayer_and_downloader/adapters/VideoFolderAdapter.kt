package com.example.videoplayer_and_downloader.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.audio
import com.example.videoplayer_and_downloader.Utils.audios
import com.example.videoplayer_and_downloader.Utils.video
import com.example.videoplayer_and_downloader.Utils.videos
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.BaseItem
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.NativeAdViewHolder
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.NativeAdsCallBack
import com.example.videoplayer_and_downloader.databinding.FolderGridRowBinding
import com.example.videoplayer_and_downloader.databinding.FolderRowBinding
import com.example.videoplayer_and_downloader.databinding.LayoutAdContainerListBinding
import com.example.videoplayer_and_downloader.listeners.onFolderClick
import com.example.videoplayer_and_downloader.models.Folder
import com.videoplayertest.myhdvideo.mediaplayer.enums.ItemType2
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class VideoFolderAdapter( private val clickInterface: onFolderClick,
private val videoFolders: Boolean,
itemLayout1: Int,var nativeAdId: String, var fbNative: String, var keyPriority: Int = 3
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    NativeAdsCallBack {
    private var imageEntities: MutableList<BaseItem> = ArrayList()
    var adsHashMap = HashMap<Int, Any>()
    private var itemLayout = itemLayout1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType2.REAL_ITEM.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
                MyViewHolder(view)
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
                Log.e("TAG", "onBindViewHolder:  set item", )
                item.bindItem(holder, position)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (imageEntities[position].itemType() == ItemType2.REAL_ITEM.ordinal) {
            ItemType2.REAL_ITEM.ordinal
        } else {
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


    fun setData(
        packsList: MutableList<Folder>,
        isNativeAdEnable:Boolean,
    ) {
        imageEntities.clear()

        packsList.forEachIndexed { index, currentFolder ->
            Log.e("TAG", "setData:  data ${index}", )
            Log.e("TAG", "setData:  data ${currentFolder.name}", )

            if (isNativeAdEnable  && index != 0 && (index == 2 || (index-2) % 6 == 0)) {

                Log.e("TAG", "setData: ", )
                Log.e("TAG", "setData: Ad  index  ${index}", )
                imageEntities.add(NativeItem(null))
            }
            imageEntities.add(MyItemView(currentFolder))


            Log.e("TAG", "setData:  item_ad size ${imageEntities.size}", )
        }

    }
    inner class MyViewHolder(var myItemView: View) :
        RecyclerView.ViewHolder(myItemView) {
        private var folderItemGridBinding: FolderGridRowBinding? = null
        private var folderItemListLayoutBinding: FolderRowBinding? = null

        fun bindImageData(
            currentFolder: Folder,
            position: Int
        ) {
            val folderFile = File(currentFolder.path)
            when (itemLayout) {
                R.layout.folder_grid_row ->
                {
                    folderItemGridBinding =
                        FolderGridRowBinding.bind(myItemView)
                    folderItemGridBinding?.let { binding->
                        val folderName = when (folderFile.name) {
                            "0" -> binding.root.context.resources.getString(R.string.root)
                            else -> folderFile.name
                        }
                        val numVideoItems = currentFolder.videoFiles.size
                        val numAudioItems = currentFolder.audioFiles.size
                        Log.e("TAG", "bindImageData: $currentFolder.name" )

                        val numVideoText = if (numVideoItems == 1) video else videos

                        val numAudioText = if (numVideoItems == 1) audio else audios

                        val numItems = if (videoFolders)
                            binding.root.context.resources.getString(
                                R.string.num_items,
                                numVideoItems,
                                numVideoText
                            )
                        else
                            binding.root.context.resources.getString(
                                R.string.num_items,
                                numAudioItems,
                                numAudioText
                            )
                        binding.apply {
                            if(folderName.contains("WhatsApp")){
                                Log.e("TAG", "bindImageData: whatsapp" )
                                this.itemIv.setImageDrawable(itemIv.context.resources.getDrawable(R.drawable.ic_whatsapp_videos))
                            }else if(folderName.toLowerCase().contains("Camera")){
                                Log.e("TAG", "bindImageData: camera" )

                                this.itemIv.setImageDrawable(itemIv.context.resources.getDrawable(R.drawable.ic_camera_videos))

                            }
                            tvFolderTitle.text = folderName
                            root.setOnClickListener {
                                clickInterface.onItemClick(position, currentFolder)
                            }
                        }
                    }
                }
                else ->
                {

                    folderItemListLayoutBinding = FolderRowBinding.bind(myItemView)
                    folderItemListLayoutBinding?.let { binding->

                        val folderName = when (folderFile.name) {
                            "0" -> binding.root.context.resources.getString(R.string.root)
                            else -> folderFile.name
                        }
                        val numVideoItems = currentFolder.videoFiles.size
                        val numAudioItems = currentFolder.audioFiles.size

                        val numVideoText = if (numVideoItems == 1) video else videos
                        val numAudioText = if (numVideoItems == 1) audio else audios

                        val numItems = if (videoFolders)
                            binding.root.context.resources.getString(
                                R.string.num_items,
                                numVideoItems,
                                numVideoText
                            )
                        else
                            binding.root.context.resources.getString(
                                R.string.num_items,
                                numAudioItems,
                                numAudioText
                            )
                        Log.e("TAG", "bindImageData:  ${numItems}", )
                        binding.apply {
                            tvFolderTitle.text = folderName
                            videoCount.text = numItems
                            root.setOnClickListener {
                                clickInterface.onItemClick(position, currentFolder)
                            }
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

    }

    class MyItemView(var currentFolder: Folder) : BaseItem() {
        override fun itemType(): Int {
            return ItemType2.REAL_ITEM.ordinal
        }

        override fun bindItem(holder: RecyclerView.ViewHolder?, position: Int) {
            holder as MyViewHolder
            Log.e("TAG", "bindItem: call ", )
            holder.bindImageData(currentFolder, position)

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