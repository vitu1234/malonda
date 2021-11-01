package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pos")
public class POS {
    @PrimaryKey(autoGenerate = true)
    int room_pos_id;

    @ColumnInfo(name = "product_id")
    int product_id;
    @ColumnInfo(name = "qty_id")
    int qty;

    @ColumnInfo(name = "total")
    double total;

    private boolean showMenu = false;

    public POS(int product_id, int qty, double total, boolean showMenu) {
        this.product_id = product_id;
        this.qty = qty;
        this.total = total;
        this.showMenu = showMenu;
    }

    public POS() {
    }

    public int getRoom_pos_id() {
        return room_pos_id;
    }

    public void setRoom_pos_id(int room_pos_id) {
        this.room_pos_id = room_pos_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
}