package com.example.bendaku.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bendaku.database.dao.ClaimDao;
import com.example.bendaku.database.dao.ItemDao;
import com.example.bendaku.database.entity.LocalClaim;
import com.example.bendaku.database.entity.LocalItem;

@Database(entities = {LocalItem.class, LocalClaim.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ItemDao itemDao();
    public abstract ClaimDao claimDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "bendaku_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

