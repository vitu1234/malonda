package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.malonda.models.POS;

import java.util.List;

@Dao
public interface PosDao {
    @Query("SELECT *FROM pos")
    List<POS> getAllPos();

    @Query("SELECT *FROM pos GROUP BY product_id")
    List<POS> getAllPosGrouped();

    @Query("SELECT * FROM pos WHERE product_id = :id")
    POS findByPosId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertPos(POS pos);


    @Update
    void updatePos(POS pos);

    @Query("UPDATE pos set qty_id =:qty WHERE product_id =:id")
    void updatePosByProdID(int qty, int id);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deletePos(POS pos);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM pos")
    void deleteAllPos();

    @Query("DELETE FROM pos WHERE product_id =:id")
    void deletePosByProdID(int id);

    //count car
    @Query("SELECT * FROM pos WHERE product_id = :id")
    int getSinglePosCount(int id);

    @Query("SELECT SUM(qty_id) FROM pos WHERE product_id = :id")
    int totalProQtyPosCount(int id);


    //count all car
    @Query("SELECT COUNT(*) FROM pos ")
    int countAllPos();
}
