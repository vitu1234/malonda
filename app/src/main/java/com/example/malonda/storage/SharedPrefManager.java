package com.example.malonda.storage;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.malonda.models.User;


public class SharedPrefManager {
    private static String SHARED_PREF_NAME = "USER_DATA";
    private static String SHARED_PREF_NAME1 = "USER_ACC";
    private static String SHARED_PREF_NAME2 = "USER_ACC";
    private Context context;
    private static SharedPrefManager mInstance;

    public SharedPrefManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //store user into the pref
    public void saveUser(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putInt("user_id", user.getUser_id());
        editor.putString("fname", user.getFname());
        editor.putString("lname", user.getLname());
        editor.putString("phone", user.getPhone());
        editor.putString("user_type", user.getUser_type());
        editor.putString("img_url", user.getImg_url());
        editor.putString("account_status", user.getAccount_status());
        editor.putString("date_created", user.getDate_created());
        editor.putString("date_updated", user.getDate_updated());
        editor.apply();
    }

    public void SaveAccountType(String type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME1, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putString("account_type", type);
        editor.apply();
    }

    public void saveLicense(boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("license", false);
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //if value = -1 that means not logged in
        if (sharedPreferences.getInt("user_id", -1) != -1) {
            return true;
        } else {
            return false;
        }
    }

    public String getAccountType() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME1, Context.MODE_PRIVATE);
        return sharedPreferences.getString("account_type", null);
    }

    public boolean hasLicense(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("license",false);
    }

    public User getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        User user = new User(
                sharedPreferences.getInt("user_id", -1),
                sharedPreferences.getString("fname", null),
                sharedPreferences.getString("lname", null),
                sharedPreferences.getString("phone", null),
                sharedPreferences.getString("user_type", null),
                sharedPreferences.getString("account_status", null),
                sharedPreferences.getString("img_url", null),
                sharedPreferences.getString("date_created", null),
                sharedPreferences.getString("date_updated", null)
        );
        return user;

    }

    public void logoutUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = context.getSharedPreferences(SHARED_PREF_NAME1, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = context.getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();

        editor.clear();
        editor1.clear();
        editor2.clear();

        editor.apply();
        editor1.apply();
        editor2.apply();
    }

}