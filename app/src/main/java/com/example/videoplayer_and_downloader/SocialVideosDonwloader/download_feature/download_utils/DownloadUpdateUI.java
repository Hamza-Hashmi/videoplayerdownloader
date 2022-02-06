package com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils;

import android.os.Handler;
import android.os.Looper;

public class DownloadUpdateUI {

    private Handler mHandler = new Handler(Looper.getMainLooper());

        private Runnable videoStatusChecker;

        public DownloadUpdateUI(final Runnable uiUpdater) {

            videoStatusChecker = new Runnable() {
                @Override
                public void run() {

                    uiUpdater.run();
                    mHandler.postDelayed(this, 1000);
                }
            };
        }

    public synchronized void startUpdates(){
            videoStatusChecker.run();
        }

        public synchronized void stopUpdates(){
            mHandler.removeCallbacks(videoStatusChecker);
        }
}