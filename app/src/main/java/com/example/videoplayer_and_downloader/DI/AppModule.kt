package com.example.videoplayer_and_downloader.DI

import com.example.videodownload.allViewModel.DownloadedVideosFragmentViewModel
import com.example.videodownload.allViewModel.MainViewModel
import com.example.videodownload.allViewModel.WhatsappStatusViewModel
import com.example.videodownload.repository.DataRepo
import com.example.videoplayer_and_downloader.DataSource.AudiosDataRepo
import com.example.videoplayer_and_downloader.DataSource.DatabaseRepo
import com.example.videoplayer_and_downloader.DataSource.TrendingDataRepo
import com.example.videoplayer_and_downloader.DataSource.VideosDataRepository
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.TinyDB
import com.example.videoplayer_and_downloader.Utils.Constants
import com.example.videoplayer_and_downloader.Utils.MySharedPreferences
import com.example.videoplayer_and_downloader.Utils.SharedPreferenceData
import com.example.videoplayer_and_downloader.models.StorageMediaHelper
import com.example.videoplayer_and_downloader.viewModels.AudioSongsViewModel
import com.example.videoplayer_and_downloader.viewModels.DatabaseViewModel
import com.example.videoplayer_and_downloader.viewModels.TrendingsViewModel
import com.example.videoplayer_and_downloader.viewModels.VideosDataViewModel
import com.example.videoplayer_and_downloader.database.MyAppDataBase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {
    val getModule = module {
        single { SharedPreferenceData(get()) }
        single { MySharedPreferences(get()) }
        single { Constants(get()) }
       single { MyAppDataBase(get()) }
        single { DatabaseRepo(get()) }
        single { StorageMediaHelper(get()) }
        single { VideosDataRepository(get()) }
        single { AudiosDataRepo(get()) }
        single{TrendingDataRepo(get())}
        single { DataRepo(context = get()) }
        single { TinyDB.getInstance(get()) }

        //single { TabsRepository() }
        viewModel { VideosDataViewModel(get(),get()) }
        viewModel{ AudioSongsViewModel(get()) }
        viewModel { DatabaseViewModel(get(),get()) }
        viewModel{TrendingsViewModel(get())}
        viewModel { MainViewModel(dataRepo = get()) }
        viewModel { DownloadedVideosFragmentViewModel() }
        viewModel { WhatsappStatusViewModel() }


    }
}