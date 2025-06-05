package com.example.trashwiz.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trashwiz.entity.CategoriesEntity;

import java.util.List;

@Dao
public interface CategoriesDao {
    @Insert
    void insert(CategoriesEntity categoriesEntity);
 
    @Update
    void update(CategoriesEntity categoriesEntity);
 
    @Delete
    void delete(CategoriesEntity categoriesEntity);
 
    @Query("SELECT * FROM categories WHERE category_id= :cateId")
    LiveData<CategoriesEntity> getByCateId(int cateId);

    @Query("SELECT * FROM categories WHERE tag= :keyword")
    LiveData<CategoriesEntity> getByKeyword(String keyword);

    @Query("SELECT * FROM categories")
    LiveData<List<CategoriesEntity>> getCates();
}