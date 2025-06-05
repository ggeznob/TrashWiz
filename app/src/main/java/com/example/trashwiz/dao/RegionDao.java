package com.example.trashwiz.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trashwiz.entity.GarbageEntity;
import com.example.trashwiz.entity.RegionEntity;

@Dao
public interface RegionDao {
    @Insert
    void insert(RegionEntity regionEntity);
 
    @Update
    void update(RegionEntity regionEntity);
 
    @Delete
    void delete(RegionEntity regionEntity);
 
    @Query("SELECT * FROM region WHERE region_id= :regionId")
    LiveData<RegionEntity> getByRegionId(int regionId);

    @Query("SELECT * FROM region WHERE name= :regionName")
    LiveData<RegionEntity> getByRegionName(String regionName);


}