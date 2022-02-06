package com.example.videoplayer_and_downloader.SocialVideosDonwloader.AdBlockers;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
final public class AdFilter {
    @PrimaryKey(autoGenerate = true)
    int uid;
    @ColumnInfo(name = "filter_str")
    String filterStr = null;
    @ColumnInfo(name = "starts_with")
    String startsWith = "";
    @ColumnInfo(name = "start_with_domain")
    String startsWithDomain = "";
    @ColumnInfo(name = "contains")
    List<String> contains = new ArrayList<>();
    @ColumnInfo(name = "ends_with_last")
    boolean endsWithLast = false;
    @ColumnInfo(name = "is_exception")
    boolean isException = false;
    @ColumnInfo(name = "domains")
    List<String> domains = new ArrayList<>();
    @ColumnInfo(name = "excluded_domains")
    List<String> excDomains = new ArrayList<>();

    @Override
    public String toString() {
        return "{ fiterStr: " + filterStr + "\nstartsWith: " + startsWith + "\nstartsWithDomain: "
                + startsWithDomain + "\ncontains: " + contains + "\nendsWidthLast: " + endsWithLast
                + "\nisException: " + isException + "\ndomains: " + domains + "\nexcDomains" +
                excDomains + " }";
    }
}