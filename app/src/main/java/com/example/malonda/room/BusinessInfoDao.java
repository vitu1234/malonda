package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.malonda.models.BusinessInfo;

import java.util.List;
@Dao
public interface BusinessInfoDao {
    @Query("SELECT *FROM business_info")
    List<BusinessInfo> getAllBusinessInfo();

    @Query("SELECT * FROM business_info WHERE business_id = :id")
    BusinessInfo findByBusinessInfoId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertBusinessInfo(BusinessInfo business_info);


    @Update
    void updateBusinessInfo(BusinessInfo business_info);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteBusinessInfo(BusinessInfo business_info);

    // on below line we are making query to
    @Query("DELETE FROM business_info")
    void deleteAllBusinessInfo();

    //count car
    @Query("SELECT * FROM business_info WHERE business_id = :id")
    int getSingleBusinessInfoCount(int id);

    @Query("SELECT * FROM business_info WHERE user_id = :user_id")
    int getSingleBusinessInfoCountByBusinessInfoID(String user_id);

    //count all car
    @Query("SELECT COUNT(*) FROM business_info ")
    int countAllBusinessInfo();
}
