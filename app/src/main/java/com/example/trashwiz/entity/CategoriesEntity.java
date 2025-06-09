// This class defines the Room Entity for waste categories, including ID, name, description, and an auto-generated tag.

package com.example.trashwiz.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class CategoriesEntity {
    @PrimaryKey
    private int  category_id;
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "tag")
    private String tag;

    public CategoriesEntity(int category_id, String name, String description) {
        this.category_id = category_id;
        this.name = name;
        this.description = description;
        this.tag = name.replace(" ","");
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "CategoriesEntity{" +
                "category_id=" + category_id +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
