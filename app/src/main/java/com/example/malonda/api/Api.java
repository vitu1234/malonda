package com.example.malonda.api;

import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.LoginResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface Api {

    //POST METHODS
    //login user
    @FormUrlEncoded
    @POST("userlogin")
    //what kind of response? use ResponseBody if you don't know the kind of response that you will get
    Call<LoginResponse> loginUser(
            @Field("phone") String email,
            @Field("password") String password
    );
    //add product with picture
    @Multipart
    @POST("add_product")
    Call<AllDataResponse> addProductWithPic(
            @Part MultipartBody.Part file,
            @Part("user_id") int user_id,
            @Part("category_id") int category_id,
            @Part("unit_id") int unit_id,
            @Part("product_name") String product_name,
            @Part("product_price") String product_price,
            @Part("product_quantity") String product_quantity,
            @Part("product_threshold") String product_threshold,
            @Part("product_description") String product_description

    );

    //edit product without picture
    @FormUrlEncoded
    @POST("add_product")
    Call<AllDataResponse> addProduct(
            @Field("user_id") int user_id,
            @Field("category_id") int category_id,
            @Field("unit_id") int unit_id,
            @Field("product_name") String product_name,
            @Field("product_price") String product_price,
            @Field("product_quantity") String product_quantity,
            @Field("product_threshold") String product_threshold,
            @Field("product_description") String product_description

    );

    //add sale
    @FormUrlEncoded
    @POST("add_sale")
    Call<AllDataResponse> add_sale(
            @Field("product_id[]") List<Integer> product_id,
            @Field("qty[]") List<Integer> qty,
            @Field("total") double total,
            @Field("paid_amount") double paid_amount,
            @Field("change") double change,
            @Field("discount") double discount,
            @Field("tax") double tax

    );


    //GET METHODS

    //get all data
    @GET("all_data")
    Call<AllDataResponse> getAllData(
    );


    //PUT/UPDATE METHODS

    //edit product with picture
    @Multipart
    @PUT("update_product")
    Call<AllDataResponse> editProductWithPic(
            @Part MultipartBody.Part file,
            @Part("product_id") int product_id,
            @Field("category_id") int category_id,
            @Field("unit_id") int unit_id,
            @Field("product_name") String product_name,
            @Field("product_price") String product_price,
            @Field("product_quantity") String product_quantity,
            @Field("product_threshold") String product_threshold,
            @Field("product_description") String product_description

    );

    //edit product without picture
    @FormUrlEncoded
    @PUT("update_product")
    Call<AllDataResponse> editProduct(
            @Field("product_id") int product_id,
            @Field("category_id") int category_id,
            @Field("unit_id") int unit_id,
            @Field("product_name") String product_name,
            @Field("product_price") String product_price,
            @Field("product_quantity") String product_quantity,
            @Field("product_threshold") String product_threshold,
            @Field("product_description") String product_description

    );

    //update inventory qty to server
    @FormUrlEncoded
    @PUT("update_inventory")
    Call<AllDataResponse> update_inventory(
            @Field("product_id[]") List<Integer> product_id,
            @Field("qty[]") List<Integer> qty

    );


    //change product picture
    @Multipart
    @POST("update_product_image")
    Call<AllDataResponse> changeProductImage(
            @Part MultipartBody.Part file,
            @Part("product_id") int product_id
    );


    //DELETE METHODS

    //delete supplier
    @DELETE("delete_product/{product_id}")
    Call<AllDataResponse> deleteProduct(
            @Path("product_id") int product_id
    );
}
