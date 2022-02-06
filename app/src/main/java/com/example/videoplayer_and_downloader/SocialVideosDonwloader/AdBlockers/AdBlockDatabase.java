package com.example.videoplayer_and_downloader.SocialVideosDonwloader.AdBlockers;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {AdFilter.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AdBlockDatabase extends RoomDatabase {
    public abstract AdFilterDao adFilterDao();
}
