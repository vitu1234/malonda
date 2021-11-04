package com.example.malonda.api;

import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.LoginResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    //login user
    @FormUrlEncoded
    @POST("createuser")
    //what kind of response? use ResponseBody if you don't know the kind of response that you will get
    Call<AllDataResponse> register(
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("phone") String phone,
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

    //add business info
    @FormUrlEncoded
    @POST("add_business_info")
    Call<AllDataResponse> add_business_info(
            @Field("user_id") int user_id,
            @Field("business_name") String business_name,
            @Field("business_phone") String business_phone,
            @Field("business_address") String business_address,
            @Field("longtude") String longtude,
            @Field("latitude") String latitude
    );

    //add sale phone, name, payment_method, product_id, qty, total_amount
    @FormUrlEncoded
    @POST("add_sale")
    Call<AllDataResponse> add_sale(
            @Field("phone") String phone,
            @Field("name") String name,
            @Field("payment_method") String payment_method,
            @Field("product_id[]") List<Integer> product_id,
            @Field("qty[]") List<Integer> qty,
            @Field("total") double total,
            @Field("bus_user_id") double bus_user_id

    );

    @FormUrlEncoded
    @POST("convert_currency")
        //what kind of response? use ResponseBody if you don't know the kind of response that you will get
    Call<AllDataResponse> convertCurrency(
            @Field("amount") String amount
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

    //update business info
    @FormUrlEncoded
    @POST("update_business_info")
    Call<AllDataResponse> update_business_info(
            @Field("user_id") int user_id,
            @Field("business_id") int business_id,
            @Field("business_name") String business_name,
            @Field("business_phone") String business_phone,
            @Field("business_address") String business_address,
            @Field("longtude") String longtude,
            @Field("latitude") String latitude
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

    //delete supplier
    @DELETE("delete_business/{business_id}")
    Call<AllDataResponse> deleteBusiness(
            @Path("business_id") int business_id
    );
}
