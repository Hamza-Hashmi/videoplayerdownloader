package smartobject.videoplayer.supervideoplayer.hdvideoplayer.videoeditor.adaptters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.databinding.RowAudioSongsBinding
import com.example.videoplayer_and_downloader.models.MusicModel
import java.io.File

class DetailsAudioAdapter(val trackList:ArrayList<MusicModel>, val context: Context, val callback:(position:Int) ->Unit, val showMenu:(view: View, audioFile:MusicModel, pos:Int) ->Unit, val callFrom:String):RecyclerView.Adapter<DetailsAudioAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(RowAudioSongsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val audio = trackList[position]



        holder.binding.apply {
            Log.e("TAG", "onBindViewHolder: ${audio.title}" )
            Log.e("TAG", "onBindViewHolder: ${audio.artist}" )

            this.tvName.text = audio.title
            this.tvAlbum.text = audio.album
            Glide.with(context).load(File(audio.path)).placeholder(R.drawable.ic_audio_placeholder).into(this.ivSong)

            this.ivMore.setOnClickListener {
                showMenu(it,audio,position)
            }
        }


        holder.itemView.setOnClickListener {
            callback(position)
        }


    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    inner class MyViewHolder(val binding:RowAudioSongsBinding):RecyclerView.ViewHolder(binding.root)

}

