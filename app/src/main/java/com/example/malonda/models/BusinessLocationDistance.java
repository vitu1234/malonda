package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "business_location_distance")
public class BusinessLocationDistance {
    @PrimaryKey(autoGenerate = true)
    int business_location_distance_id;

    @ColumnInfo(name = "business_id")
    int business_id;

    @ColumnInfo(name = "km_from_me")
    double km_from_me;

    public BusinessLocationDistance() {
    }

    public BusinessLocationDistance(int business_id, double km_from_me) {
        this.business_id = business_id;
        this.km_from_me = km_from_me;
    }

    public int getBusiness_location_distance_id() {
        return business_location_distance_id;
    }

    public void setBusiness_location_distance_id(int business_location_distance_id) {
        this.business_location_distance_id = business_location_distance_id;
    }

    public int getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }

    public double getKm_from_me() {
        return km_from_me;
    }

    public void setKm_from_me(double km_from_me) {
        this.km_from_me = km_from_me;
    }
}
