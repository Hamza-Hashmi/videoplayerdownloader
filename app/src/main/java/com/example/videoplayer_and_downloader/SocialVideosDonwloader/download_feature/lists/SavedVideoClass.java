package com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.lists;

import java.io.Serializable;

public class SavedVideoClass implements Serializable {
    public String name;
    public Boolean selected = false;
    public String link;
    public String page;

    public SavedVideoClass(){

    }
    public SavedVideoClass(String name, Boolean selected, String link, String page){
        this.name = name;
        this.selected = selected;
        this.link = link;
        this.page = page;
    }
}
