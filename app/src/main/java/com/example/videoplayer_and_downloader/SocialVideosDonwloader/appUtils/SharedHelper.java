package com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {


    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static  String isUser="isUser";

    public static String putKey(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.commit();
        return Key;
    }

    public static String getKey(Context contextGetKey, String Key) {
        sharedPreferences = contextGetKey.getSharedPreferences("Cache", Context.MODE_PRIVATE);
        String Value = sharedPreferences.getString(Key, "");
        return Value;

    }


    public static void deleteAllSharedPrefs(){
        sharedPreferences.edit().clear().commit();
    }

}
