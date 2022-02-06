package com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.download_utils;

import static com.example.videodownload.appUtils.ExtFunctionsKt.PrepareDirectory;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.videodownload.appUtils.Constants;
import com.example.videodownload.datamodel.videoDetail;
import com.example.videodownload.download_feature.lists.DownloadQueues;
import com.example.videodownload.download_feature.lists.MyCompletedVideosClass;
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.lists.InactiveDownloadClass;
import com.example.videoplayer_and_downloader.Utils.MyApplication;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;


public class MyDownloadManagerClass extends IntentService {

    private static File downloadFile = null;
    private static long totalSize = 0;
    private static long downloadSpeed = 0;
    private static long prevDownloaded = 0;

    private static boolean chunked;
    private static ByteArrayOutputStream bytesOfChunk;
    private static DownloadNotifierClass downloadNotifier;
    private static String downloadFolder;
    private static boolean stop = false;
    private static Thread downloadThread;

    public MyDownloadManagerClass() {
        super("DownloadManager");
    }



    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(Constants.Companion.getLOG_TAG(), "onHandleIntent......." );
        stop = false;
        downloadThread = Thread.currentThread();
        downloadNotifier = new DownloadNotifierClass(intent);

        if (intent != null) {
            chunked = intent.getBooleanExtra("chunked", false);
            if (chunked) {
                downloadFile = null;
                totalSize = 0;
                prevDownloaded = 0;
                downloadSpeed = 0;
                handleChunkedDownload(intent);
            } else {
                prevDownloaded = 0;
                URLConnection connection;
                try {
                    totalSize = Long.parseLong(Objects.requireNonNull(intent.getStringExtra("size")));
                    connection = (new URL(intent.getStringExtra("link"))).openConnection();
                    String filename = intent.getStringExtra("name") + "." + intent.getStringExtra("type");
                    String link = intent.getStringExtra("link");
                    String page = intent.getStringExtra("page");

                    File directory = PrepareDirectory(MyApplication.getMyApplicationContext());
                    if (!directory.getAbsolutePath().endsWith("/")) {
                        downloadFolder = directory.getAbsolutePath() + "/";
                    } else {
                        downloadFolder = directory.getAbsolutePath();
                    }
                    boolean directoryExists = directory.exists() || directory.mkdir() || directory.createNewFile();
                    if (directoryExists) {
                        downloadNotifier.notifyDownloading();
                        downloadFile = new File(directory, filename);

                        Log.e(Constants.Companion.getLOG_TAG(), "onHandleIntent: DOWNLOAD FILE NAME -> "+ downloadFile);

                        if (connection != null) {
                            FileOutputStream out = null;
                            if (downloadFile.exists()) {
                                prevDownloaded = downloadFile.length();
                                connection.setRequestProperty("Range", "bytes=" + downloadFile.length
                                        () + "-");
                                connection.connect();

                                out = new FileOutputStream(downloadFile.getAbsolutePath(), true);
                            } else {
                                connection.connect();
                                if (downloadFile.createNewFile()) {
                                    out = new FileOutputStream(downloadFile.getAbsolutePath(), true);
                                }
                            }
                            if (out != null && downloadFile.exists()) {
                                InputStream in = connection.getInputStream();
                                ReadableByteChannel readableByteChannel = Channels.newChannel(in);
                                FileChannel fileChannel = out.getChannel();
                                while (downloadFile.length() < totalSize) {
                                    if (stop) return;
                                    fileChannel.transferFrom(readableByteChannel, 0, 1024);
                                    //if (downloadFile == null) return;
                                }
                                readableByteChannel.close();
                                in.close();
                                out.flush();
                                out.close();
                                fileChannel.close();
                                downloadFinished(filename, link, page);
                            }
                        }
                    } else {
                        Log.e(Constants.Companion.getLOG_TAG(), "onHandleIntent: Download directory not created  ");
                        onDownloadFailExceptionListener.onDownloadFailException("Download directory not created");
                       // showDownloadFailed();
                    }
                } catch (FileNotFoundException e) {
                    Log.e(Constants.Companion.getLOG_TAG(), "onHandleIntent: link not found exception -> "+ e.getMessage());
                    linkNotFound(intent);
                }  catch (IOException e) {

                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    //showDownloadFailed();
                    Log.e(Constants.Companion.getLOG_TAG(), "onHandleIntent: IOException -> "+ sw.toString());
                    onDownloadFailExceptionListener.onDownloadFailException(sw.toString());
                }catch (DownloadFailException e){
                    Log.e(Constants.Companion.getLOG_TAG(), "onHandleIntent: DownloadFailException -> "+ e.getMessage());

                    onDownloadFailExceptionListener.onDownloadFailException(e.getMessage());
                }catch (Exception e){
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));

                    Log.e(Constants.Companion.getLOG_TAG(), "Exception -> "+ e.getMessage());

                    onDownloadFailExceptionListener.onDownloadFailException(sw.toString());
                }
            }
        } else {
            Log.e(Constants.Companion.getLOG_TAG(), "Intent is null ");

        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (downloadNotifier != null) {
            downloadNotifier.cancel();
        }

        Log.e("Downloader_TAG", "onTaskRemoved");
    }

    private void downloadFinished(String filename, String link, String page) {

        Log.e(Constants.Companion.getLOG_TAG(), "Service Download finish call");

        downloadNotifier.notifyDownloadFinished();

        if (onDownloadFinishedListener != null) {

            onDownloadFinishedListener.onDownloadFinished();

        } else {

            DownloadQueues queues = DownloadQueues.Companion.load(MyApplication.getMyApplicationContext());
            queues.deleteTopVideo(MyApplication.getMyApplicationContext());
            MyCompletedVideosClass completedVideos = MyCompletedVideosClass.load(MyApplication.getMyApplicationContext());
            completedVideos.addVideo(MyApplication.getMyApplicationContext(), filename, link, page);

            videoDetail topVideo = queues.getTopVideo();
            if (topVideo != null) {
                Intent downloadService = MyApplication.getMyApplicationContext().getDownloadService();
                downloadService.putExtra("link", topVideo.component3());
                downloadService.putExtra("name", topVideo.component4());
                downloadService.putExtra("type", topVideo.component2());
                downloadService.putExtra("size", topVideo.component1());
                downloadService.putExtra("page", topVideo.component5());
                downloadService.putExtra("chunked", topVideo.component6());
                downloadService.putExtra("website", topVideo.component7());
                onHandleIntent(downloadService);
            }
        }
    }

    private void linkNotFound(Intent intent) {
        downloadNotifier.cancel();
        if (onLinkNotFoundListener != null) {
            onLinkNotFoundListener.onLinkNotFound();
        } else {
            DownloadQueues queues = DownloadQueues.Companion.load(MyApplication.getMyApplicationContext());
            queues.deleteTopVideo(this);
            videoDetail inactiveDownload = new videoDetail();
            inactiveDownload.setName(intent.getStringExtra("name"));
            inactiveDownload.setLink(intent.getStringExtra("link"));
            inactiveDownload.setType( intent.getStringExtra("type"));
            inactiveDownload.setSize( intent.getStringExtra("size"));
            inactiveDownload.setPage( intent.getStringExtra("page"));
            inactiveDownload.setWebsite( intent.getStringExtra("website"));
            inactiveDownload.setChunked( intent.getBooleanExtra("chunked", false));

            InactiveDownloadClass inactiveDownloads = InactiveDownloadClass.load(this);
            inactiveDownloads.add(this, inactiveDownload);

            videoDetail topVideo = queues.getTopVideo();
            if (topVideo != null) {
                Intent downloadService = MyApplication.Companion.getMyApplicationContext().getDownloadService();
                downloadService.putExtra("link", topVideo.component3());
                downloadService.putExtra("name", topVideo.component4());
                downloadService.putExtra("type", topVideo.component2());
                downloadService.putExtra("size", topVideo.component1());
                downloadService.putExtra("page", topVideo.component5());
                downloadService.putExtra("chunked", topVideo.component6());
                downloadService.putExtra("website", topVideo.component7());
                onHandleIntent(downloadService);
            }
        }
    }

    private void handleChunkedDownload(Intent intent) {
        try {
            String name = intent.getStringExtra("name");
            String type = intent.getStringExtra("type");
            String link = intent.getStringExtra("link");
            String page = intent.getStringExtra("page");


            File directory = PrepareDirectory(MyApplication.getMyApplicationContext());
            if (directory == null) {
                throw new DownloadFailException("Target Directory is null");
            }
            if (!directory.getAbsolutePath().endsWith("/")) {
                downloadFolder = directory.getAbsolutePath() + "/";
            } else {
                downloadFolder = directory.getAbsolutePath();
            }
            boolean directoryExists = directory.exists() || directory.mkdir() || directory
                    .createNewFile();
            if (directoryExists) {
                downloadNotifier.notifyDownloading();
                File progressFile = new File(getCacheDir(), name + ".dat");
                File videoFile = new File(directory, name + "." + type);
                long totalChunks = 0;
                if (progressFile.exists()) {
                    FileInputStream in = new FileInputStream(progressFile);
                    DataInputStream data = new DataInputStream(in);
                    totalChunks = data.readLong();
                    data.close();
                    in.close();

                    if (!videoFile.exists()) {
                        if (!videoFile.createNewFile()) {
                            throw new DownloadFailException("Can't create file to download.");
                        }
                    }
                } else if (videoFile.exists()) {
                    downloadFinished(name + "." + type, link, page);
                } else {
                    if (!videoFile.createNewFile()) {
                        throw new DownloadFailException("Can't create file to download.");
                    }
                    if (!progressFile.createNewFile()) {
                        throw new DownloadFailException("progress file not created");
                    }
                }

                if (videoFile.exists() && progressFile.exists()) {

                    while (true) {
                        prevDownloaded = 0;
                        String website = intent.getStringExtra("website");
                        String chunkUrl = null;
                        switch (website) {
                            case "dailymotion.com":
                                chunkUrl = getNextChunkForDailyMotion(intent, totalChunks);
                                break;
                            case "vimeo.com":
                                chunkUrl = getNextChunkWithVimeoRule(intent, totalChunks);
                                break;
                            case "twitter.com":
                            case "myspace.com":
                            case "metacafe.com":
                                chunkUrl = getNextChunkWithM3U8Rule(intent, totalChunks);
                                break;
                        }
                        if (chunkUrl == null) {
                            downloadFinished(name + "." + type, link, page);
                            break;
                        }
                        bytesOfChunk = new ByteArrayOutputStream();
                        try {
                            URLConnection uCon = new URL(chunkUrl).openConnection();
                            if (uCon != null) {
                                InputStream in = uCon.getInputStream();
                                ReadableByteChannel readableByteChannel = Channels.newChannel(in);
                                WritableByteChannel writableByteChannel = Channels.newChannel(bytesOfChunk);
                                int read;
                                while (true) {
                                    if (stop) return;
                                    //if (Thread.currentThread().isInterrupted()) return;
                                    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                                    read = readableByteChannel.read(buffer);
                                    if (read != -1) {
                                        buffer.flip();
                                        writableByteChannel.write(buffer);
                                    } else {
                                        FileOutputStream vAddChunk = new FileOutputStream
                                                (videoFile, true);
                                        vAddChunk.write(bytesOfChunk.toByteArray());
                                        FileOutputStream outputStream = new FileOutputStream
                                                (progressFile, false);
                                        DataOutputStream dataOutputStream = new DataOutputStream
                                                (outputStream);
                                        dataOutputStream.writeLong(++totalChunks);
                                        dataOutputStream.close();
                                        outputStream.close();
                                        break;
                                    }
                                }
                                readableByteChannel.close();
                                in.close();
                                bytesOfChunk.close();
                            }
                        } catch (FileNotFoundException e) {
                            downloadFinished(name + "." + type, link, page);
                            break;
                        } catch (IOException e) {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            //showDownloadFailed();
                            onDownloadFailExceptionListener.onDownloadFailException(sw.toString());
                            break;
                        }
                    }
                }
            } else {
                onDownloadFailExceptionListener.onDownloadFailException("Can't create Download directory.");
                //showDownloadFailed();
            }
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            onDownloadFailExceptionListener.onDownloadFailException(sw.toString());
            //showDownloadFailed();
        } catch (DownloadFailException e){
            onDownloadFailExceptionListener.onDownloadFailException(e.getMessage());
        }
        catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            onDownloadFailExceptionListener.onDownloadFailException(sw.toString());

        }
    }

    public static long getDownloadSpeed() {
        if (!chunked) {
            if (downloadFile != null) {
                long downloaded = downloadFile.length();
                downloadSpeed = downloaded - prevDownloaded;
                prevDownloaded = downloaded;
                return downloadSpeed;
            }
            return 0;
        } else {
            if (bytesOfChunk != null) {
                long downloaded = bytesOfChunk.size();
                downloadSpeed = downloaded - prevDownloaded;
                prevDownloaded = downloaded;
                return downloadSpeed;
            }
            return 0;
        }
    }

    private void showDownloadFailed() {
        Toast.makeText(this, "Download Failed , Try Again", Toast.LENGTH_SHORT).show();
    }

    private String getNextChunkForDailyMotion(Intent intent, long totalChunks) {
        String link = intent.getStringExtra("link");
        return Objects.requireNonNull(link).replaceAll("FRAGMENT", "frag(" + (totalChunks + 1) + ")");
    }

    private String getNextChunkWithVimeoRule(Intent intent, long totalChunks) {
        String link = intent.getStringExtra("link");
        return Objects.requireNonNull(link).replaceAll("SEGMENT", "segment-" + (totalChunks + 1));
    }

    private String getNextChunkWithM3U8Rule(Intent intent, long totalChunks) {
        String link = intent.getStringExtra("link");
        String website = intent.getStringExtra("website");
        String line = null;
        try {
            URLConnection m3u8Con = new URL(link).openConnection();
            InputStream in = m3u8Con.getInputStream();
            InputStreamReader inReader = new InputStreamReader(in);
            BufferedReader buffReader = new BufferedReader(inReader);
            while ((line = buffReader.readLine()) != null) {
                if ((Objects.requireNonNull(website).equals("twitter.com") || website.equals("myspace.com")) && line
                        .endsWith(".ts")) {
                    break;
                } else if (website.equals("metacafe.com") && line.endsWith(".mp4")) {
                    break;
                }
            }
            if (line != null) {
                long l = 1;
                while (l < (totalChunks + 1)) {
                    buffReader.readLine();
                    line = buffReader.readLine();
                    l++;
                }
            }
            buffReader.close();
            inReader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line != null) {
            String prefix;
            switch (Objects.requireNonNull(website)) {
                case "twitter.com":
                    return "https://video.twimg.com" + line;
                case "metacafe.com":
                case "myspace.com":
                    prefix = Objects.requireNonNull(link).substring(0, link.lastIndexOf("/") + 1);
                    return prefix + line;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public interface OnDownloadFinishedListener {
        void onDownloadFinished();
    }

    private static OnDownloadFinishedListener onDownloadFinishedListener;

    public static void setOnDownloadFinishedListener(OnDownloadFinishedListener listener) {
        onDownloadFinishedListener = listener;
    }

    public interface onDownloadFailExceptionListener {
        void onDownloadFailException(String message);
    }

    private static onDownloadFailExceptionListener onDownloadFailExceptionListener;

    public static void setOnDownloadFailExceptionListener(onDownloadFailExceptionListener listener) {
        onDownloadFailExceptionListener = listener;
    }


    public interface OnLinkNotFoundListener {
        void onLinkNotFound();
    }

    private static OnLinkNotFoundListener onLinkNotFoundListener;

    public static void setOnLinkNotFoundListener(OnLinkNotFoundListener listener) {
        onLinkNotFoundListener = listener;
    }

    @Override
    public void onDestroy() {
        Log.e("Downloader_TAG", "onDestroy: Servise is destroy ");
        /*downloadFile = null;
        Thread.currentThread().interrupt();*/

        super.onDestroy();
    }

    public static void stop() {
        if (downloadNotifier != null) {
            downloadNotifier.cancel();
        }
        Intent downloadService = MyApplication.getMyApplicationContext().getDownloadService();
        MyApplication.getMyApplicationContext().stopService(downloadService);
        forceStopIfNecessary();
    }

    public static void forceStopIfNecessary() {
        if(downloadThread != null){
            downloadThread = Thread.currentThread();
            if (downloadThread.isAlive()) {
                stop = true;
            }
        }
        /*if (downloadThread != null && downloadThread.isAlive()) {
            stop = true;
        }*/
    }

}
