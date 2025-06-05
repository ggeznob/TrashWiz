package com.example.trashwiz.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "classification_rule")
public class ClassificationRuleEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "region_id")
    private int region_id;
    @ColumnInfo(name = "item_id")
    private int item_id;

    @ColumnInfo(name = "category_id")
    private int category_id;

    public ClassificationRuleEntity(int region_id, int item_id, int category_id) {
        this.region_id = region_id;
        this.item_id = item_id;
        this.category_id = category_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
