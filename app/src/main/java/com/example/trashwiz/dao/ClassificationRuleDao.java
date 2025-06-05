package com.example.trashwiz.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trashwiz.entity.CategoriesEntity;
import com.example.trashwiz.entity.ClassificationRuleEntity;

@Dao
public interface ClassificationRuleDao {
    @Insert
    void insert(ClassificationRuleEntity classificationRuleEntity);
 
    @Update
    void update(ClassificationRuleEntity classificationRuleEntity);
 
    @Delete
    void delete(ClassificationRuleEntity classificationRuleEntity);
 
    @Query("SELECT * FROM classification_rule WHERE item_id= :itemId and region_id=:regionId")
    LiveData<ClassificationRuleEntity> getByItemId(int itemId,int regionId);
}