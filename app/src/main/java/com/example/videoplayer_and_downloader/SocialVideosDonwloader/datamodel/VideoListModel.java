package com.example.videoplayer_and_downloader.SocialVideosDonwloader.datamodel;

import androidx.annotation.Keep;


import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features.VideoListClass;

import java.util.List;

@Keep
public class VideoListModel {
    public List<VideoListClass.VideoInfo> videos;
    public String currentName;

    public VideoListModel(List<VideoListClass.VideoInfo> videos, String currentName) {
        this.videos = videos;
        this.currentName = currentName;
    }
}
