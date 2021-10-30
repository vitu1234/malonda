package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class Category {

    @PrimaryKey(autoGenerate = true)
    int room_category_id;

    @ColumnInfo(name = "category_id")
    int category_id;

    @ColumnInfo(name = "category_name")
    String category_name;

    @ColumnInfo(name = "date_created")
    String date_created;

    @ColumnInfo(name = "date_updated")
    String date_updated;

    public Category() {
    }

    public Category(int category_id, String category_name, String date_created, String date_updated) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.date_created = date_created;
        this.date_updated = date_updated;
    }

    public int getRoom_category_id() {
        return room_category_id;
    }

    public void setRoom_category_id(int room_category_id) {
        this.room_category_id = room_category_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
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
