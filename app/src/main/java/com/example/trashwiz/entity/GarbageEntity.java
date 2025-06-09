// This class defines the Room Entity for garbage items, including item ID, name, and an auto-generated tag for lookup.

package com.example.trashwiz.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "garbage_items")
public class GarbageEntity {
    @PrimaryKey
    private int item_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "tag")
    private String tag;

    public GarbageEntity(int item_id, String name) {
        this.item_id = item_id;
        this.name = name;
        this.tag = name.replace(" ", "");

    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "GarbageEntity{" +
                "item_id=" + item_id +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
