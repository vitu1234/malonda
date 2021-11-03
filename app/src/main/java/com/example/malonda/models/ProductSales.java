package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product_sales")
public class ProductSales {
    @PrimaryKey(autoGenerate = true)
    int room_product_sale_id;

    @ColumnInfo(name = "product_sale_id")
    int product_sale_id;

    @ColumnInfo(name = "sale_id")
    int sale_id;

    @ColumnInfo(name = "product_id")
    int product_id;

    @ColumnInfo(name = "qty")
    int qty;

    @ColumnInfo(name = "date_created")
    String date_created;
    @ColumnInfo(name = "date_updated")
    String date_updated;

    private boolean showMenu = false;

    public ProductSales(int product_sale_id, int sale_id, int product_id, int qty, String date_created, String date_updated, boolean showMenu) {
        this.product_sale_id = product_sale_id;
        this.sale_id = sale_id;
        this.product_id = product_id;
        this.qty = qty;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.showMenu = showMenu;
    }

    public ProductSales() {
    }

    public int getRoom_product_sale_id() {
        return room_product_sale_id;
    }

    public void setRoom_product_sale_id(int room_product_sale_id) {
        this.room_product_sale_id = room_product_sale_id;
    }

    public int getProduct_sale_id() {
        return product_sale_id;
    }

    public void setProduct_sale_id(int product_sale_id) {
        this.product_sale_id = product_sale_id;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
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

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
}
