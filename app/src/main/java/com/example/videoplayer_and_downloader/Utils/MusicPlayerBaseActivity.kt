package com.example.videoplayer_and_downloader.Utils

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

 abstract class MusicPlayerBaseActivity <VB : ViewBinding>(val bindingInflater: (LayoutInflater) -> VB) :
    Base() {


    private  val TAG = "PlayerBaseActivity"
    var _binding: ViewBinding? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB get() = _binding as VB

    val audioDataViewModel: AudioSongsViewModel by viewModel()

    public val listLayoutManager: LinearLayoutManager
        get() {
            return LinearLayoutManager(this@MusicPlayerBaseActivity, RecyclerView.VERTICAL, false)
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)

        initViews()
        setListeners()

        setObservers()
        setSplashDelly()
        setUpDataLogics()
    }

    abstract fun initViews()

    abstract fun setListeners()

    open fun setObservers(){}
    open fun setUpDataLogics(){}
    open fun setSplashDelly(){}


}