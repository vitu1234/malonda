package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "unit")
public class Unit {

    @PrimaryKey(autoGenerate = true)
    int room_unit_id;

    @ColumnInfo(name = "unit_id")
    int unit_id;

    @ColumnInfo(name = "unit_name")
    String unit_name;

    @ColumnInfo(name = "unit_symbol")
    String unit_symbol;

    @ColumnInfo(name = "date_created")
    String date_created;

    @ColumnInfo(name = "date_updated")
    String date_updated;

    public Unit() {
    }

    public Unit(int unit_id, String unit_name, String unit_symbol, String date_created, String date_updated) {
        this.unit_id = unit_id;
        this.unit_name = unit_name;
        this.unit_symbol = unit_symbol;
        this.date_created = date_created;
        this.date_updated = date_updated;
    }

    public int getRoom_unit_id() {
        return room_unit_id;
    }

    public void setRoom_unit_id(int room_unit_id) {
        this.room_unit_id = room_unit_id;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getUnit_symbol() {
        return unit_symbol;
    }

    public void setUnit_symbol(String unit_symbol) {
        this.unit_symbol = unit_symbol;
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
