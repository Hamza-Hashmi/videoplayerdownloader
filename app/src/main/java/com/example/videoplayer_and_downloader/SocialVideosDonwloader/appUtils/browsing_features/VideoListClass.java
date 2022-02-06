package com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.browsing_features;

import android.app.Activity;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.videodownload.appUtils.Utils;
import com.example.videodownload.download_feature.lists.DownloadQueues;
import com.example.videoplayer_and_downloader.R;


import java.util.ArrayList;
import java.util.List;

public abstract class VideoListClass {

    public Activity activity;
    public List<VideoInfo> videoInfos;
    public String currentName = "";

    public void clearAll() {
        videoInfos.clear();
    }

    public static class VideoInfo {
        public String size, type, link, name, page, website, quality = "Unknown Quality";
        public boolean chunked = false, checked = false;
        public String downloadUrl;
    }

    public VideoListClass(Activity activity) {
        this.activity = activity;
        //view.setLayoutManager(new LinearLayoutManager(activity));
        //view.addItemDecoration(Utils.createDivider(activity));
        //view.setHasFixedSize(true);
        videoInfos = new ArrayList<>();
    }


    public void recreateVideoList() {
        //view.setLayoutManager(new LinearLayoutManager(activity));
        //view.addItemDecoration(Utils.createDivider(activity));
        //view.setHasFixedSize(true);
    }

    public void addItem(@Nullable String size, String type, String link, String name, String page,
                 boolean chunked, String website, String url, String quality) {
        VideoInfo video = new VideoInfo();
        video.size = size;
        video.type = type;
        video.link = link;
        video.name = name;
        video.page = page;
        video.chunked = chunked;
        video.website = website;
        video.downloadUrl = url;
        video.quality = quality;


        boolean duplicate = false;

        if (video.page.contains(Utils.Companion.facebookUrl(activity))) {
            videoInfos.clear();
            video.name = "Facebook " + name;
            video.quality = activity.getResources().getString(R.string.p360);
            videoInfos.add(video);
            currentName = video.name;
            return;
        }
        if (video.page.contains(Utils.Companion.instagramUrl(activity)) || video.page.contains(Utils.Companion.likeeUrl(activity))) {
            videoInfos.clear();
            video.quality = activity.getResources().getString(R.string.p360);
            videoInfos.add(video);
            currentName = video.name;
            return;
        }

        if (!currentName.equals(video.name))
            videoInfos.clear();


        for (VideoInfo v : videoInfos) {
            if (v.link.equals(video.link)) {
                duplicate = true;
                break;
            }
        }


        if (page.contains(Utils.Companion.vimeoUrl(activity))) {
            for (VideoInfo v : videoInfos) {
                if (v.quality.equals(video.quality)) {
                    duplicate = true;
                    break;
                }
            }
        }


        if (!duplicate) {
            videoInfos.add(video);
            if (page.contains(Utils.Companion.tiktokUrl(activity))) {
                videoInfos.remove(0);
            }
        }
        currentName = video.name;
    }



    public int getSize() {
        return videoInfos.size();
    }

    public void deleteCheckedItems() {
        for (int i = 0; i < videoInfos.size(); ) {
            if (videoInfos.get(i).checked) {
                videoInfos.remove(i);
            } else i++;
        }
    }


    public void saveCheckedItemsForDownloading() {
        DownloadQueues queues = DownloadQueues.Companion.load(activity);
        for (VideoInfo videoInfo : videoInfos) {
            if (videoInfo.checked) {
                queues.add(videoInfo.size, videoInfo.type, videoInfo.link, videoInfo.name, videoInfo.page, videoInfo
                        .chunked, videoInfo.website);
            }
        }

        queues.save(activity);
        Toast.makeText(activity, "Selected videos are queued for downloading. Go to Downloads " +
                "panel to start downloading videos", Toast.LENGTH_LONG).show();
    }

}
