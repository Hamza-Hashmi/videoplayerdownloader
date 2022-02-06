package com.example.videoplayer_and_downloader.SocialVideosDonwloader.download_feature.lists;

import android.content.Context;

import com.example.videodownload.datamodel.videoDetail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InactiveDownloadClass implements Serializable {

    private List<videoDetail> inactiveDownloads;

    public InactiveDownloadClass() {
        inactiveDownloads = new ArrayList<>();
    }

    public void add(Context context, videoDetail inactiveDownload) {
        inactiveDownloads.add(inactiveDownload);
        save(context);
    }

    public static InactiveDownloadClass load(Context context) {
        File file = new File(context.getFilesDir(), "inactive.dat");
        InactiveDownloadClass inactiveDownloads = new InactiveDownloadClass();
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                inactiveDownloads = (InactiveDownloadClass) objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return inactiveDownloads;
    }

    public void save(Context context) {
        try {
            File file = new File(context.getFilesDir(), "inactive.dat");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
