package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    int room_user_id;

    @ColumnInfo(name = "user_id")
    int user_id;

    @ColumnInfo(name = "fname")
    String fname;
    @ColumnInfo(name = "lname")
    String lname;


    @ColumnInfo(name = "phone")
    String phone;


    @ColumnInfo(name = "user_type")
    String user_type;

    @ColumnInfo(name = "account_status")
    String account_status;

    @ColumnInfo(name = "img_url")
    String img_url;

    @ColumnInfo(name = "date_created")
    String date_created;
    @ColumnInfo(name = "date_updated")
    String date_updated;

    public User() {
    }

    public User(int user_id, String fname, String lname, String phone, String user_type, String account_status, String img_url, String date_created, String date_updated) {
        this.user_id = user_id;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.user_type = user_type;
        this.account_status = account_status;
        this.img_url = img_url;
        this.date_created = date_created;
        this.date_updated = date_updated;
    }

    public int getRoom_user_id() {
        return room_user_id;
    }

    public void setRoom_user_id(int room_user_id) {
        this.room_user_id = room_user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
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