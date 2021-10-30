package com.example.malonda.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.Product;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;


@Database(entities = {User.class, BusinessInfo.class, Category.class, Product.class, Unit.class}, exportSchema = false, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract BusinessInfoDao businessInfoDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract UnitDao unitDao();


    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {
        String DB_NAME = "app_room_db";
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
