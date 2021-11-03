package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.malonda.models.Sale;

import java.util.List;

@Dao
public interface SaleDao {
    @Query("SELECT *FROM sales")
    List<Sale> getAllSale();

    @Query("SELECT *FROM sales WHERE sale_id = :sale_id")
    List<Sale> getAllSalesBySaleID(int sale_id);

    @Query("SELECT *FROM sales WHERE bus_user_id = :bus_user_id")
    List<Sale> getAllSalesByBusinessID(int bus_user_id);

    @Query("SELECT * FROM sales WHERE sale_id = :id")
    Sale findBySaleId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertSale(Sale sales);


    @Update
    void updateSale(Sale sales);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteSale(Sale sales);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM sales")
    void deleteAllSale();

    @Query("DELETE FROM sales WHERE sale_id =:id")
    void deleteSaleByProdID(int id);

    //count car
    @Query("SELECT COUNT(*) FROM sales WHERE sale_id = :id")
    int getSingleSaleCount(int id);

    @Query("SELECT COUNT(*) FROM sales WHERE bus_user_id = :id")
    int getSingleSaleBusCount(int id);


    //count all car
    @Query("SELECT COUNT(*) FROM sales ")
    int countAllSale();
}
