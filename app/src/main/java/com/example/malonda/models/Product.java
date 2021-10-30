package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product")
public class Product {

    @PrimaryKey(autoGenerate = true)
    int room_product_id;

    @ColumnInfo(name = "product_id")
    int product_id;
    @ColumnInfo(name = "user_id")
    int user_id;
    @ColumnInfo(name = "category_id")
    int category_id;

    @ColumnInfo(name = "unit_id")
    int unit_id;

    @ColumnInfo(name = "product_name")
    String product_name;
    @ColumnInfo(name = "price")
    String price;

    @ColumnInfo(name = "qty")
    int qty;
@ColumnInfo(name = "threshold")
    int threshold;

    @ColumnInfo(name = "img_url")
    String img_url;

    @ColumnInfo(name = "description")
    String description;
    @ColumnInfo(name = "date_created")
    String date_created;
    @ColumnInfo(name = "date_updated")
    String date_updated;
    private boolean showMenu = false;
    public Product() {
    }

    public Product(int product_id, int user_id, int category_id, int unit_id, String product_name, String price, int qty, int threshold, String img_url, String description, String date_created, String date_updated, boolean showMenu) {
        this.product_id = product_id;
        this.user_id = user_id;
        this.category_id = category_id;
        this.unit_id = unit_id;
        this.product_name = product_name;
        this.price = price;
        this.qty = qty;
        this.threshold = threshold;
        this.img_url = img_url;
        this.description = description;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.showMenu = showMenu;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getRoom_product_id() {
        return room_product_id;
    }

    public void setRoom_product_id(int room_product_id) {
        this.room_product_id = room_product_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(int unit_id) {
        this.unit_id = unit_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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