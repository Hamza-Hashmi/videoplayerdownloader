package com.example.videoplayer_and_downloader.SocialVideosDonwloader.AdBlockers;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

class Converters {
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        return gson.toJson(list);
    }
}
