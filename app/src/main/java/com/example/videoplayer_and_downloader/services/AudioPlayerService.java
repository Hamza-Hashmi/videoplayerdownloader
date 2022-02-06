package com.example.videoplayer_and_downloader.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.videoplayer_and_downloader.R;
import com.example.videoplayer_and_downloader.UI.activites.AudioPlayerActivity;
import com.example.videoplayer_and_downloader.Utils.MyApplication;
import com.example.videoplayer_and_downloader.listeners.MusicPlayerActions;
import com.example.videoplayer_and_downloader.models.MusicModel;

import java.util.ArrayList;

public class AudioPlayerService extends Service {

    IBinder mBinder = new MyBinder();

    MediaPlayer mediaPlayer;

    public ArrayList<MusicModel> musicList = new ArrayList<>();

    int postion = -1;
    Uri uri;

    MediaSessionCompat mediaSessionCompat;

    MusicPlayerActions actionPlaying;
    Context context;
    public static Handler handler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.e("Bind", "Method" );
        return mBinder;
    }




    public class MyBinder extends Binder {

        public AudioPlayerService getService(){
            Log.e("TAG", "getService: ");
            return AudioPlayerService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent !=null){
            int myPosition = intent.getIntExtra("servicePosition",-1);

            //postion=myPosition;
            String actionName = intent.getStringExtra("ActionName");

            Log.e("intent", "onStartCommand: " + intent );
            Log.e("serviceposition", "onStartCommand: " + myPosition );

            if (myPosition != -1){


                playMedia(myPosition);
            }
            if (actionName != null){
                switch (actionName){
                    case "playPause":
                        if (actionPlaying != null){
                            actionPlaying.playPauseBtnClick();
                            Toast.makeText(this, "Play pause", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "next":
                        if (actionPlaying !=null){


                            actionPlaying.btnNextClicked();
                            Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "actionprevious":
                        if (actionPlaying != null){
                            actionPlaying.btnPreviousClicked();
                            Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "actionstop":
                        if (mediaPlayer !=null){

                            mediaPlayer.stop();
                            this.stopForeground(true);
                            stopSelfResult(startId);

                        }
                        break;
                }
            }

            Log.e("TAG", "onStartCommand: "+" mypostion "+postion );
            //showNotification(R.drawable.ic_pause);
        }
        else{
            stopForeground(true);
            stopSelf();
        }

        return START_NOT_STICKY;
    }



    public void start(){
        mediaPlayer.start();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public void stop(){
        mediaPlayer.stop();
    }
    public void release(){
        mediaPlayer.release();
    }
    public int getDuration()
    {
        if (mediaPlayer.getDuration()>1){

            return mediaPlayer.getDuration();
        }
        return 0;
    }
    public void seekTo(int position){
        try{

            mediaPlayer.seekTo(position);
        }catch (Exception e){}
    }

    public void createMediaPlayer(int positionInner){
        try{

            postion = positionInner;
            Log.e("pos", "createMediaPlayer: " + postion );
            Log.e("Inpostion", "createMediaPlayer: " + positionInner);
            uri = Uri.parse(musicList.get(postion).getPath());

            Log.e("TAG", "createMediaPlayer: path" + musicList.get(postion).getPath());
            Log.e("TAG", "createMediaPlayer: uri "+ uri );
            mediaPlayer = new MediaPlayer();

            mediaPlayer = MediaPlayer.create(getBaseContext(),uri);

            if(mediaPlayer !=null) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        Log.e("complete", "onCompletion main two before: ");

                        if (actionPlaying != null) {
                            actionPlaying.btnNextClicked();

                            Log.e("complete", "onCompletion1  main: two ");
                            if (mediaPlayer != null) {


                                //mediaPlayer.stop();
                                             /*  mediaPlayer.reset();
                                               mediaPlayer.release();

                                               createMediaPlayer(postion);*/
                                //mediaPlayer.start();
                            }
                        }
                    }
                });
            }

        }catch (Exception e){


        }




    }
    public int getCurrentPosition(){

        return mediaPlayer.getCurrentPosition();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    private void playMedia(int startPostion) {
        musicList = AudioPlayerActivity.listSong;
        postion = startPostion;

        Log.e("TAG", "playMedia: " + musicList.size());
        Log.e("start", "playMedia: " + startPostion);


        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();

            Log.e("2nd time", "playMedia: " );
            if (musicList != null){
                createMediaPlayer(postion);
                mediaPlayer.start();

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        actionPlaying.setSeekBarDuration(mp.getDuration());
                        mp.start();
                        seekUpdate();

                    }
                });



                seekUpdate();
            }
        }
        else{

            Log.e("firsttime", "playMedia: ");

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    Log.e("TAG", "run postion: " + postion );
                    createMediaPlayer(postion);

                    if (mediaPlayer !=null){
                        mediaPlayer.start();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                if(actionPlaying !=null){
                                    actionPlaying.setSeekBarDuration(mp.getDuration());
                                    mp.start();
                                    seekUpdate();

                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {

                                            Log.e("complete", "onCompletion in before: ");

                                            if (actionPlaying!=null){
                                                actionPlaying.btnNextClicked();

                                                Log.e("complete", "onCompletion1 in  main: ");
                                           /*if (mediaPlayer != null){

                                               createMediaPlayer(postion);

                                               mediaPlayer.stop();

                                               mediaPlayer.release();

                                               //mediaPlayer.start();
                                           }*/
                                            }
                                        }
                                    });
                                }



                            }
                        });


                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {

                                Log.e("complete", "onCompletion two before: ");

                                if (actionPlaying!=null){
                                    actionPlaying.btnNextClicked();

                                    Log.e("complete", "onCompletion1  two: ");
                                           /*if (mediaPlayer != null){

                                               createMediaPlayer(postion);

                                               mediaPlayer.stop();

                                               mediaPlayer.release();

                                               //mediaPlayer.start();
                                           }*/
                                }
                            }
                        });
                        seekUpdate();


                    }

                }
            },1000);


        }


    }

    public void onCompleted(){
        if (mediaPlayer !=null){
            if (isPlaying()){


                //    mediaPlayer.setOnCompletionListener(this);
            }
        }
        /*else{
            mediaPlayer.stop();
        }*/
    }


    public void setCallBack(MusicPlayerActions actionPlaying){
        Log.e("TAG", "setCallBack: " );
        this.actionPlaying = actionPlaying;
    }




    Runnable thread = new Runnable() {
        @Override
        public void run() {
            if (actionPlaying != null){

                seekUpdate();
            }
        }
    };

    public  void seekUpdate() {
        if (mediaPlayer != null) {
            int currentposition = mediaPlayer.getCurrentPosition();
            actionPlaying.updateSeekbar(currentposition);;

        }
        handler.postDelayed(thread, 0);
    }

    public void mute(){
        mediaPlayer.setVolume(0, 0);
    }
    public void unMute(){
        mediaPlayer.setVolume(0,1);
    }

    public int geSongTime(){

        if (mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void showNotification(int ic_playPause){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(
                    "CHANNEL_ID_1",
                    "dddjdjdjdjd",
                    NotificationManager.IMPORTANCE_DEFAULT);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);


        }


        Log.e("nofitication", "showNotification: ");
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        PendingIntent content = PendingIntent.getActivity(this,0,intent,0);

        Intent prevIntent = new Intent(this, NotificationReciver.class)
                .setAction(MyApplication.Companion.getACTION_PREV());
        PendingIntent prevPending = PendingIntent
                .getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pasueIntent = new Intent(this,NotificationReciver.class)
                .setAction(MyApplication.Companion.getACTION_PLALY());
        PendingIntent pausePending = PendingIntent
                .getBroadcast(this,0,pasueIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this,NotificationReciver.class)
                .setAction(MyApplication.Companion.getACTION_NEXT());
        PendingIntent nextPending = PendingIntent
                .getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this,NotificationReciver.class)
                .setAction(MyApplication.Companion.getACTION_STOP());
        PendingIntent stopPending = PendingIntent.
                getBroadcast(this,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);




        NotificationCompat.Builder notification = new NotificationCompat.Builder(this,"CHANNEL_ID_1");
        notification.setSmallIcon(R.drawable.ic_cross);
        // notification.setLargeIcon(R.drawable.app_icon);

        notification.setContentTitle(musicList.get(postion).getTitle());
        notification.setChannelId("CHANNEL_ID_1");
        notification.setCategory(Notification.CATEGORY_SERVICE);
        notification.setContentText(musicList.get(postion).getArtist());
        notification.addAction(R.drawable.ic_notification_prev,"Previous",prevPending);
        notification.addAction(ic_playPause,"Pause",pausePending);
        notification.addAction(R.drawable.ic_cross,"Stop",stopPending);
        notification.addAction(R.drawable.ic_notification_next,"Next",nextPending);
        notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionCompat.getSessionToken()));
        notification.setPriority(NotificationCompat.PRIORITY_HIGH);
        notification.setOnlyAlertOnce(true);
        notification.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        //.build();


        this.startForeground(22223,notification.build());
     /* NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);*/
    }

}

