package com.example.malonda.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.malonda.R;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.buyer.activities.BuyerDashboardActivity;
import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.Product;
import com.example.malonda.models.ProductSales;
import com.example.malonda.models.Sale;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.supplier.activities.SupplierDashboardActivity;
import com.example.malonda.utils.CheckInternet;
import com.mrntlu.toastie.Toastie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashscreenActivity extends AppCompatActivity {

    private static int SLIDE_TIMER = 3000;

    SharedPreferences sharedPreferencesOnboardingScreen;
    AppDatabase room_db; //room database instance
    CheckInternet checkInternet;

    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<BusinessInfo> businessInfoList;
    private List<Product> productList;
    private List<Unit> unitList;
    private List<Sale> saleList;
    private List<ProductSales> productSalesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //disable dark mode

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);
        room_db = AppDatabase.getDbInstance(this);
        checkInternet = new CheckInternet(this);

        if (checkInternet.isInternetConnected(this)) {
            getAllData();
        } else {
            checkInternet.showInternetDialog(this);
        }

    }


    public void getAllData() {
        room_db.clearAllTables();//clear all tables
        call = RetrofitClient.getInstance().getApi().getAllData();
        call.enqueue(new Callback<AllDataResponse>() {
            @Override
            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                AllDataResponse response1 = response.body();
                if (response1 != null) {
                    if (!response1.isError()) {
                        room_db.clearAllTables();

                        userList = response1.getUsers();
                        for (int i = 0; i < userList.size(); i++) {
                            room_db.userDao().insertUser(userList.get(i));
                        }

                        businessInfoList = response1.getBusiness_info();
                        for (int i = 0; i < businessInfoList.size(); i++) {
                            room_db.businessInfoDao().insertBusinessInfo(businessInfoList.get(i));
                        }

                        categoryList = response1.getCategories();
                        for (int i = 0; i < categoryList.size(); i++) {
                            room_db.categoryDao().insertCategory(categoryList.get(i));
                        }

                        productList = response1.getProducts();
                        for (int i = 0; i < productList.size(); i++) {
                            room_db.productDao().insertProduct(productList.get(i));
                        }

                        unitList = response1.getUnits();
                        for (int i = 0; i < unitList.size(); i++) {
                            room_db.unitDao().insertUnit(unitList.get(i));
                        }

                        saleList = response1.getSales();
                        for (int i = 0; i < saleList.size(); i++) {
                            room_db.saleDao().insertSale(saleList.get(i));
                        }

                        productSalesList = response1.getProduct_sales();
                        for (int i = 0; i < productSalesList.size(); i++) {
                            room_db.productSalesDao().insertProductSales(productSalesList.get(i));
                        }

                        splashScreen();

                    } else {


                        Toastie.allCustom(SplashscreenActivity.this)
                                .setTypeFace(Typeface.DEFAULT_BOLD)
                                .setTextSize(16)
                                .setCardRadius(25)
                                .setCardElevation(10)
                                .setIcon(R.drawable.ic_error_black_24dp)
                                .setCardBackgroundColor(R.color.red)
                                .setMessage(response1.getMessage())
                                .setGravity(Gravity.BOTTOM, 5, 5)
                                .createToast(Toast.LENGTH_LONG)
                                .show();
//                        Toast.makeText(SplashscreenActivity.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toastie.allCustom(SplashscreenActivity.this)
                            .setTypeFace(Typeface.DEFAULT_BOLD)
                            .setTextSize(16)
                            .setCardRadius(25)
                            .setCardElevation(10)
                            .setIcon(R.drawable.ic_error_black_24dp)
                            .setCardBackgroundColor(R.color.red)
                            .setMessage("No server reposonse!")
                            .setGravity(Gravity.BOTTOM, 5, 5)
                            .createToast(Toast.LENGTH_LONG)
                            .show();

                }

            }

            @Override
            public void onFailure(Call<AllDataResponse> call, Throwable t) {
                Log.e("error", "Connection problem or " + t.getMessage());
                Toastie.allCustom(SplashscreenActivity.this)
                        .setTypeFace(Typeface.DEFAULT_BOLD)
                        .setTextSize(16)
                        .setCardRadius(25)
                        .setCardElevation(10)
                        .setIcon(R.drawable.ic_error_black_24dp)
                        .setCardBackgroundColor(R.color.red)
                        .setMessage("Connection problem or " + t.getMessage())
                        .setGravity(Gravity.BOTTOM, 5, 5)
                        .createToast(Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public void splashScreen() {
        //delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            sharedPreferencesOnboardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

            boolean isFirstTimeUser = sharedPreferencesOnboardingScreen.getBoolean("firstTime", true);
            if (isFirstTimeUser) {

                SharedPreferences.Editor editor = sharedPreferencesOnboardingScreen.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
                //start the next actitivty after 5 seconds
                startActivity(new Intent(getApplicationContext(), OnBoardingScreenActivity.class));
                finish();
            } else {
                //check if logged in here and redirect
                if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                    if (SharedPrefManager.getInstance(this).getAccountType().equals("customer")) {
                        startActivity(new Intent(getApplicationContext(), BuyerDashboardActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(getApplicationContext(), SupplierDashboardActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), BuyerDashboardActivity.class));
//                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }

            }


        }, SLIDE_TIMER);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }
    }
}