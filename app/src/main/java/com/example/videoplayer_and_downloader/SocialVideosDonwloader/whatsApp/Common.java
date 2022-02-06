package com.example.videoplayer_and_downloader.SocialVideosDonwloader.whatsApp;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.File;

public class Common {

    private static final String CHANNEL_NAME = "GAUTHAM";

    public static final File STATUS_DIRECTORY = new File(Environment.getExternalStorageDirectory() +
            File.separator + "WhatsApp/Media/.Statuses");

    public static final File STATUS_DIRECTORY_NEW = new File(Environment.getExternalStorageDirectory() +
            File.separator + "Android/media/com.whatsapp/WhatsApp/Media/.Statuses");

    public static String APP_DIR;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void makeNotificationChannel(Context context) {

        NotificationChannel channel = new NotificationChannel(Common.CHANNEL_NAME, "Saved", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setShowBadge(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

}