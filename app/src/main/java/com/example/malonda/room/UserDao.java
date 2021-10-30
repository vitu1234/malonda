package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.malonda.models.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT *FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE user_id = :id")
    User findByUserId(int id);

    @Query("SELECT * FROM user WHERE phone = :phone")
    User findByUserEmail(String phone);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertUser(User user);


    @Update
    void updateUser(User user);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteUser(User user);

    // on below line we are making query to
    @Query("DELETE FROM user")
    void deleteAllUsers();

    //count car
    @Query("SELECT * FROM user WHERE user_id = :id")
    int getSingleUserCount(int id);

    @Query("SELECT * FROM user WHERE phone = :phone")
    int getSingleUserCountByEmail(String phone);

    //count all car
    @Query("SELECT COUNT(*) FROM user ")
    int countAllUsers();
}
