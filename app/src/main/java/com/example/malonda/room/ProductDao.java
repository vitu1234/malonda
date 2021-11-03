package com.example.malonda.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.malonda.models.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT *FROM product")
    List<Product> getAllProducts();
    @Query("SELECT * FROM product WHERE product_id = :id")
    Product findByProductId(int id);

    @Query("SELECT *FROM product WHERE user_id =:user_id  ")
    List<Product> getAllUserProducts(int user_id);

    //sorting and filtering
    @Query("SELECT *FROM product WHERE qty >0 ORDER BY price ASC")
    List<Product> getAllProductsAvailablePriceAsc();

    @Query("SELECT *FROM product WHERE qty >0 AND user_id =:user_id ORDER BY price ASC")
    List<Product> getAllUserProductsAvailablePriceAsc(int user_id);

    @Query("SELECT *FROM product WHERE qty >0 ORDER BY price DESC")
    List<Product> getAllProductsAvailablePriceDesc();

    @Query("SELECT *FROM product WHERE qty >0 AND user_id =:user_id ORDER BY price DESC")
    List<Product> getAllUserProductsAvailablePriceDesc(int user_id);

    //SORT WITH USER ID
    @Query("SELECT *FROM product WHERE user_id=:user_id AND qty >0 ORDER BY price ASC")
    List<Product> getAllProductsAvailablePriceAsc(int user_id);

    @Query("SELECT *FROM product WHERE user_id=:user_id AND qty >0 ORDER BY price DESC")
    List<Product> getAllProductsAvailablePriceDesc(int user_id);


    @Query("SELECT *FROM product WHERE qty >0 ORDER BY product_name ASC")
    List<Product> getAllProductsAvailable();


    @Query("SELECT *FROM product WHERE qty >0 AND user_id =:user_id ORDER BY product_name ASC")
    List<Product> getAllUserProductsAvailable(int user_id);

    @Query("SELECT *FROM product WHERE qty >0  ORDER BY product_name DESC")
    List<Product> getAllProductsAvailableNameDesc();

    @Query("SELECT *FROM product WHERE qty >0 AND user_id =:user_id ORDER BY product_name DESC")
    List<Product> getAllProductsAvailableNameDesc(int user_id);



    //VIEW PRODUCTS BY CATEGORY BUT WITHOUT USER ID
    @Query("SELECT *FROM product WHERE category_id =:category_id AND qty >0 ")
    List<Product> getAllProductsAvailableCAT(int category_id );

    @Query("SELECT *FROM product WHERE category_id =:category_id  AND qty >0 ORDER BY price ASC")
    List<Product> getAllProductsAvailablePriceAscCAT(int category_id);

    @Query("SELECT *FROM product WHERE category_id =:category_id  AND qty >0 ORDER BY price DESC")
    List<Product> getAllProductsAvailablePriceDescCAT(int category_id);

    @Query("SELECT *FROM product WHERE category_id =:category_id AND qty >0  ORDER BY product_name ASC")
    List<Product> getAllUserProductsAvailableNameAscCAT(int category_id);

    @Query("SELECT *FROM product WHERE category_id =:category_id AND qty >0  ORDER BY product_name DESC")
    List<Product> getAllProductsAvailableNameDescCAT(int category_id );






    //VIEW PRODUCTS BY CATEGORY BUT WITH USER ID


    @Query("SELECT *FROM product WHERE category_id =:category_id AND user_id=:user_id AND qty >0 ")
    List<Product> getAllProductsAvailableCAT(int category_id,int user_id );

    @Query("SELECT *FROM product WHERE category_id =:category_id AND user_id=:user_id AND qty >0 ORDER BY price ASC")
    List<Product> getAllProductsAvailablePriceAscCAT(int category_id, int user_id);

    @Query("SELECT *FROM product WHERE category_id =:category_id AND user_id=:user_id AND qty >0 ORDER BY price DESC")
    List<Product> getAllProductsAvailablePriceDescCAT(int category_id, int user_id);

    @Query("SELECT *FROM product WHERE category_id =:category_id AND qty >0 AND user_id =:user_id ORDER BY product_name ASC")
    List<Product> getAllUserProductsAvailableNameAscCAT(int category_id, int user_id);

    @Query("SELECT *FROM product WHERE category_id =:category_id AND user_id =:user_id AND qty >0  ORDER BY product_name DESC")
    List<Product> getAllProductsAvailableNameDescCAT(int category_id,int user_id );



    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertProduct(Product product);


    @Update
    void updateProduct(Product product);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteProduct(Product product);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM product")
    void deleteAllProducts();

    //count car
    @Query("SELECT COUNT(*) FROM product WHERE product_id = :id")
    int getSingleProductCount(int id);

    @Query("SELECT COUNT(*) FROM product WHERE user_id = :id")
    int getUserProductCount(int id);

    @Query("SELECT COUNT(*) FROM product WHERE qty >0 AND user_id = :id")
    int getUserAvailableProductCount(int id);


    //count all car
    @Query("SELECT COUNT(*) FROM product ")
    int countAllProducts();
}