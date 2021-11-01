package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "business_info")
public class BusinessInfo {

    @PrimaryKey(autoGenerate = true)
    int room_business_info_id;

    @ColumnInfo(name = "business_id")
    int business_id; @ColumnInfo(name = "user_id")
    int user_id;

    @ColumnInfo(name = "business_name")
    String business_name;

    @ColumnInfo(name = "business_phone")
    String business_phone;

    @ColumnInfo(name = "business_address")
    String business_address;

    @ColumnInfo(name = "longtude")
    String longtude;

    @ColumnInfo(name = "latitude")
    String latitude;

    @ColumnInfo(name = "date_created")
    String date_created;

    @ColumnInfo(name = "date_updated")
    String date_updated;

    public BusinessInfo() {
    }

    public BusinessInfo(int business_id, int user_id, String business_name, String business_phone, String business_address, String longtude, String latitude, String date_created, String date_updated) {
        this.business_id = business_id;
        this.user_id = user_id;
        this.business_name = business_name;
        this.business_phone = business_phone;
        this.business_address = business_address;
        this.longtude = longtude;
        this.latitude = latitude;
        this.date_created = date_created;
        this.date_updated = date_updated;
    }

    public int getRoom_business_info_id() {
        return room_business_info_id;
    }

    public void setRoom_business_info_id(int room_business_info_id) {
        this.room_business_info_id = room_business_info_id;
    }

    public int getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_phone() {
        return business_phone;
    }

    public void setBusiness_phone(String business_phone) {
        this.business_phone = business_phone;
    }

    public String getBusiness_address() {
        return business_address;
    }

    public void setBusiness_address(String business_address) {
        this.business_address = business_address;
    }

    public String getLongtude() {
        return longtude;
    }

    public void setLongtude(String longtude) {
        this.longtude = longtude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }
}
