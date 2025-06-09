// This class defines the Room database setup for the app, including DAOs and entities for waste classification data.

package com.example.trashwiz.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trashwiz.dao.CategoriesDao;
import com.example.trashwiz.dao.ClassificationRuleDao;
import com.example.trashwiz.dao.GarbageDao;
import com.example.trashwiz.dao.RegionDao;
import com.example.trashwiz.entity.CategoriesEntity;
import com.example.trashwiz.entity.ClassificationRuleEntity;
import com.example.trashwiz.entity.GarbageEntity;
import com.example.trashwiz.entity.RegionEntity;

@Database(entities = {CategoriesEntity.class,ClassificationRuleEntity.class, GarbageEntity.class, RegionEntity.class}, version = 1, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CategoriesDao categoriesDao();

    public abstract ClassificationRuleDao classificationRuleDao();

    public abstract GarbageDao garbageDao();
    public abstract RegionDao regionDao();
 
    private static volatile AppDatabase INSTANCE;
 
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "data")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}