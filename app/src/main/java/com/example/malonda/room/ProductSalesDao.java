package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.malonda.models.ProductSales;

import java.util.List;

@Dao
public interface ProductSalesDao {
    @Query("SELECT *FROM product_sales")
    List<ProductSales> getAllProductSales();

    @Query("SELECT *FROM product_sales WHERE sale_id = :sale_id")
    List<ProductSales> getAllProductSalesBySaleID(int sale_id);

    @Query("SELECT * FROM product_sales WHERE sale_id = :sale_id")
    ProductSales findByProductSalesId(int sale_id);

    @Query("SELECT * FROM product_sales WHERE product_sale_id = :product_sale_id")
    ProductSales findByBusinessByUserId(int product_sale_id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertProductSales(ProductSales product_sales);


    @Update
    void updateProductSales(ProductSales product_sales);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteProductSales(ProductSales product_sales);

    // on below line we are making query to
    @Query("DELETE FROM product_sales")
    void deleteAllProductSales();

    //count car
    @Query("SELECT COUNT(*) FROM product_sales WHERE sale_id = :id")
    int getSingleProductSalesCount(int id);

    @Query("SELECT COUNT(*) FROM product_sales WHERE product_sale_id = :user_id")
    int getSingleProductSalesCountByProductSalesID(int user_id);

    //count all car
    @Query("SELECT COUNT(*) FROM product_sales ")
    int countAllProductSales();
}

