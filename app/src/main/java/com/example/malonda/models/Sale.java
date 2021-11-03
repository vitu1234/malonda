package com.example.malonda.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sales")
public class Sale {
    @PrimaryKey(autoGenerate = true)
    int room_sale_id;

    @ColumnInfo(name = "sale_id")
    int sale_id;

    @ColumnInfo(name = "bus_user_id")
    int bus_user_id;

    @ColumnInfo(name = "uniqid")
    String uniqid;

    @ColumnInfo(name = "customer_name")
    String customer_name;

    @ColumnInfo(name = "customer_phone")
    String customer_phone;


    @ColumnInfo(name = "total_amount")
    String total_amount;

    @ColumnInfo(name = "payment_method")
    String payment_method;
    @ColumnInfo(name = "date_created")
    String date_created;
    @ColumnInfo(name = "date_updated")
    String date_updated;
    private boolean showMenu = false;

    public Sale(int sale_id, int bus_user_id, String uniqid, String customer_name, String customer_phone, String total_amount, String payment_method, String date_created, String date_updated, boolean showMenu) {
        this.sale_id = sale_id;
        this.bus_user_id = bus_user_id;
        this.uniqid = uniqid;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.total_amount = total_amount;
        this.payment_method = payment_method;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.showMenu = showMenu;
    }

    public Sale() {
    }

    public int getRoom_sale_id() {
        return room_sale_id;
    }

    public void setRoom_sale_id(int room_sale_id) {
        this.room_sale_id = room_sale_id;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public int getBus_user_id() {
        return bus_user_id;
    }

    public void setBus_user_id(int bus_user_id) {
        this.bus_user_id = bus_user_id;
    }

    public String getUniqid() {
        return uniqid;
    }

    public void setUniqid(String uniqid) {
        this.uniqid = uniqid;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
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
