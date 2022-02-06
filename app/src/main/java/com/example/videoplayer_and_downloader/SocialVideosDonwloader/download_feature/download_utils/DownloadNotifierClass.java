package com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils;

import static com.example.videodownload.appUtils.ExtFunctionsKt.getMainStoragePath;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.example.videodownload.appUtils.Constants;
import com.example.videoplayer_and_downloader.R;
import com.example.videoplayer_and_downloader.Utils.MyApplication;

import java.io.File;

public class DownloadNotifierClass {

    //private final int ID = 88999;
    private Intent downloadServiceIntent;
    private Handler handler;
    private NotificationManager notificationManager;
    private DownloadingRunnableHandler downloadingRunnable;

    private class DownloadingRunnableHandler implements Runnable {
        @Override
        public void run() {

            final String downloadFolder = getMainStoragePath(MyApplication.getMyApplicationContext().getApplicationContext());

            if(downloadFolder != null){

                String filename = downloadServiceIntent.getStringExtra("name") + "." +
                        downloadServiceIntent.getStringExtra("type");
                Notification.Builder NB;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    NB = new Notification.Builder(MyApplication.getMyApplicationContext().getApplicationContext(), Constants.Companion.getChannelId()).setSound(null, null)
                            .setStyle(new Notification.BigTextStyle());
                } else {
                    NB = new Notification.Builder(MyApplication.getMyApplicationContext().getApplicationContext()).setSound(null);
                }
                NB.setContentTitle("Downloading..." + filename)
                        .setSmallIcon(R.drawable.facebook)
                        .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getMyApplicationContext()
                                .getApplicationContext().getResources(), R.drawable.facebook))
                        .setSound(null, null)
                        .setColor(ContextCompat.getColor(MyApplication.getMyApplicationContext().getApplicationContext(), R.color.primary_color))
                        .setOngoing(true);

                if (downloadServiceIntent.getBooleanExtra("chunked", false)) {
                    File file = new File(downloadFolder, filename);
                    String downloadedfilelength;
                    if (file.exists()) {
                        downloadedfilelength = android.text.format.Formatter.formatFileSize(MyApplication.getMyApplicationContext().getApplicationContext(), file.length());
                    } else {
                        downloadedfilelength = "0KB";
                    }
                    NB.setProgress(100, 0, true).setContentText(downloadedfilelength)
                            .setSound(null,null)
                    .setColor(ContextCompat.getColor(MyApplication.getMyApplicationContext().getApplicationContext(), R.color.primary_color));
                    notificationManager.notify(7777, NB.build());
                    handler.postDelayed(this, 1000);
                } else {
                    File file = new File(downloadFolder, filename);
                    String sizeString = downloadServiceIntent.getStringExtra("size");
                    int progress = (int) Math.ceil(((double) file.length() / (double) Long.parseLong
                            (sizeString)) * 100);
                    progress = progress >= 100 ? 100 : progress;
                    String downloaded = android.text.format.Formatter.formatFileSize(MyApplication
                            .getMyApplicationContext().getApplicationContext(), file.length());
                    String total = android.text.format.Formatter.formatFileSize(MyApplication.getMyApplicationContext()
                            .getApplicationContext(), Long.parseLong
                            (sizeString));
                    NB.setProgress(100, progress, false)
                            .setContentText(downloaded + "/" + total + "   " + progress + "%").setSound(null, null)
                            .setColor(ContextCompat.getColor(MyApplication.getMyApplicationContext().getApplicationContext(), R.color.primary_color));

                    notificationManager.notify(7777, NB.build());
                    handler.postDelayed(this, 1000);
                    if(progress >= 100){
                        notifyDownloadFinished();
                    }
                }
            }
            else{
                Toast.makeText(MyApplication.getMyApplicationContext().getApplicationContext(), "Download folder is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    DownloadNotifierClass(Intent downloadServiceIntent) {
        this.downloadServiceIntent = downloadServiceIntent;
        notificationManager = (NotificationManager) MyApplication.getMyApplicationContext().getApplicationContext().getSystemService
                (Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(Constants.Companion.getChannelId(),
                    "Download Notification", NotificationManager.IMPORTANCE_LOW));
            notificationManager.createNotificationChannel(new NotificationChannel(Constants.Companion.getChannelId2(),
                    "Download Notification", NotificationManager.IMPORTANCE_LOW));
        }
        HandlerThread thread = new HandlerThread("downloadNotificationThread");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    void notifyDownloading() {
        downloadingRunnable = new DownloadingRunnableHandler();
        downloadingRunnable.run();
    }

    void notifyDownloadFinished() {

        handler.removeCallbacks(downloadingRunnable);
        notificationManager.cancel(7777);

        String filename = downloadServiceIntent.getStringExtra("name") + "." +
                downloadServiceIntent.getStringExtra("type");
        Notification.Builder NB;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NB = new Notification.Builder(MyApplication.getMyApplicationContext().getApplicationContext(), Constants.Companion.getChannelId2())
                    .setTimeoutAfter(1000)
                    .setOngoing(false)
                    .setContentTitle("Video Downloaded Successfully")
                    .setContentText(filename)
                    .setSmallIcon(R.drawable.facebook)
                    .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getMyApplicationContext().getApplicationContext().getResources(),
                            R.drawable.facebook));
            notificationManager.notify(7777, NB.build());
        } else {
            NB = new Notification.Builder(MyApplication.getMyApplicationContext().getApplicationContext())
                    .setTicker("Video Downloaded Successfully")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(false)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.facebook)
                    .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getMyApplicationContext().getApplicationContext().getResources(),
                            R.drawable.facebook));
            notificationManager.notify(7777, NB.build());
            notificationManager.cancel(7777);
        }
    }

    void cancel() {
        if (downloadingRunnable != null) {
            handler.removeCallbacks(downloadingRunnable);
        }
        notificationManager.cancel(7777);
    }
}
