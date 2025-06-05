package com.example.trashwiz.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trashwiz.entity.ClassificationRuleEntity;
import com.example.trashwiz.entity.GarbageEntity;

import java.util.List;

@Dao
public interface GarbageDao {
    @Insert
    void insert(GarbageEntity garbageEntity);
 
    @Update
    void update(GarbageEntity garbageEntity);
 
    @Delete
    void delete(GarbageEntity garbageEntity);
 
    @Query("SELECT * FROM garbage_items WHERE item_id= :itemId")
    LiveData<GarbageEntity> getByItemId(int itemId);

    @Query("SELECT * FROM garbage_items WHERE tag= :keyword")
    LiveData<GarbageEntity> getByKeyword(String keyword);

    @Query("SELECT * FROM garbage_items")
    LiveData<List<GarbageEntity>> get();
}