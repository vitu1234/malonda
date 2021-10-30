package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.malonda.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT *FROM category ORDER BY category_id DESC")
    List<Category> getAllCategorys();

    @Query("SELECT * FROM category WHERE category_id = :id")
    Category findByCategoryId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertCategory(Category category);


    @Update
    void updateCategory(Category category);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteCategory(Category category);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM category")
    void deleteAllCategorys();

    //count car
    @Query("SELECT * FROM category WHERE category_id = :id")
    int getSingleCategoryCount(int id);

    //count all car
    @Query("SELECT COUNT(*) FROM category ")
    int countAllCategorys();
}
