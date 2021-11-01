package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.malonda.models.BusinessLocationDistance;

import java.util.List;

@Dao
public interface BusinessLocationDistanceDao {
    @Query("SELECT *FROM business_location_distance ORDER BY km_from_me ASC LIMIT 30")
    List<BusinessLocationDistance> getAllBusinessInfoOrderByDistance();

    @Query("SELECT * FROM business_location_distance WHERE business_id = :id")
    BusinessLocationDistance findByBusinessInfoId(int id);


    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertBusinessInfo(BusinessLocationDistance business_location_distance);


    @Update
    void updateBusinessInfo(BusinessLocationDistance business_location_distance);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteBusinessInfo(BusinessLocationDistance business_location_distance);

    // on below line we are making query to
    @Query("DELETE FROM business_location_distance")
    void deleteAllBusinessInfo();

    //count car
    @Query("SELECT * FROM business_location_distance WHERE business_id = :id")
    int getSingleBusinessInfoCount(int id);


    //count all car
    @Query("SELECT COUNT(*) FROM business_location_distance ")
    int countAllBusinessInfo();
}
