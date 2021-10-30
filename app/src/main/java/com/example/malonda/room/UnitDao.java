package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.malonda.models.Unit;

import java.util.List;


@Dao
public interface UnitDao {
    @Query("SELECT *FROM unit ORDER BY unit_id DESC")
    List<Unit> getAllUnits();

    @Query("SELECT * FROM unit WHERE unit_id = :id")
    Unit findByUnitId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertUnit(Unit unit);


    @Update
    void updateUnit(Unit unit);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteUnit(Unit unit);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM unit")
    void deleteAllUnits();

    //count car
    @Query("SELECT * FROM unit WHERE unit_id = :id")
    int getSingleUnitCount(int id);

    //count all car
    @Query("SELECT COUNT(*) FROM unit ")
    int countAllUnits();
}