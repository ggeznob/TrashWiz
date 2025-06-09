// This class defines the Room Entity for regions, storing the region ID and name for location-based garbage classification.

package com.example.trashwiz.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "region")
public class RegionEntity {
    @PrimaryKey
    private int region_id;

    @ColumnInfo(name = "name")
    private String name;

    public RegionEntity(int region_id,String name) {
        this.region_id = region_id;
        this.name = name;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
