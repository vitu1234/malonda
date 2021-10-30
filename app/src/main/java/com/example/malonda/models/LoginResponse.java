package com.example.malonda.models;

import java.util.List;

public class LoginResponse {
    private boolean error;
    private String message;
    private User user;

    private List<User> users;
    private List<Category>categories;
    private List<BusinessInfo>business_info;
    private List<Product>products;
    private List<Unit>units;

    public LoginResponse(boolean error, String message, User user, List<User> users, List<Category> categories, List<BusinessInfo> business_info, List<Product> products, List<Unit> units) {
        this.error = error;
        this.message = message;
        this.user = user;
        this.users = users;
        this.categories = categories;
        this.business_info = business_info;
        this.products = products;
        this.units = units;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<BusinessInfo> getBusiness_info() {
        return business_info;
    }

    public void setBusiness_info(List<BusinessInfo> business_info) {
        this.business_info = business_info;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }
}
