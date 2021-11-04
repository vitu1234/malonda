package com.example.malonda.common;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.malonda.R;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.LoginResponse;
import com.example.malonda.models.Product;
import com.example.malonda.models.ProductSales;
import com.example.malonda.models.Sale;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.utils.CheckInternet;
import com.example.malonda.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.mrntlu.toastie.Toastie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    Call<LoginResponse> call;
    CheckInternet checkInternet;
    TextInputLayout textInputLayoutFname, textInputLayoutLname, textInputLayoutPhone, textPassword;    //dialog
    MyProgressDialog progressDialog;

    private List<User> userList;
    private List<Category> categoryList;
    private List<BusinessInfo> businessInfoList;
    private List<Product> productList;
    private List<Unit> unitList;
    private List<Sale> saleList;
    private List<ProductSales> productSalesList;

    private AppDatabase room_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //hooks
        textInputLayoutFname = findViewById(R.id.fnameLayout);
        textInputLayoutLname = findViewById(R.id.lnameLayout);
        textPassword = findViewById(R.id.passwordLayout);
        textInputLayoutPhone = findViewById(R.id.emailLayout);

        checkInternet = new CheckInternet(this);
        progressDialog = new MyProgressDialog(this);
        room_db = AppDatabase.getDbInstance(this);

    }

    private boolean validatePassword() {
        String password = textPassword.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            textPassword.setError("Enter password");
            textPassword.setErrorEnabled(true);
            return false;
        } else {
            textPassword.setError(null);
            textPassword.setErrorEnabled(false);
            return true;
        }


    }

    //validation functions
    private boolean validateEmail() {
        String mail = textInputLayoutPhone.getEditText().getText().toString().trim();
        String check_spaces = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (mail.isEmpty()) {
            textInputLayoutPhone.setError("Required field");
            textInputLayoutPhone.setErrorEnabled(true);
            return false;
        } else {

            if (isValidPhoneNumber(mail)) {
                textInputLayoutPhone.setError(null);
                textInputLayoutPhone.setErrorEnabled(false);
                return true;
            } else {
                textInputLayoutPhone.setError("Enter a valid phone number");
                textInputLayoutPhone.setErrorEnabled(true);
                return false;
            }


        }
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private boolean validateInputLayout(TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText().getText().toString().isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("required!");
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError(null);
            return true;
        }
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

    public void signUp(View view) {

        if (validateEmail() && validatePassword() && validateInputLayout(textInputLayoutFname) && validateInputLayout(textInputLayoutLname)) {
            if (checkInternet.isInternetConnected(this)) {
                progressDialog.showDialog("Background check...");
                String password = textPassword.getEditText().getText().toString().trim();
                String phone = textInputLayoutPhone.getEditText().getText().toString().trim();
                String fname = textInputLayoutPhone.getEditText().getText().toString().trim();
                String lname = textInputLayoutPhone.getEditText().getText().toString().trim();

                call = RetrofitClient.getInstance().getApi().createUser(fname, lname, phone, password);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        LoginResponse response1 = response.body();
                        progressDialog.closeDialog();
                        if (response1 != null) {

                            if (!response1.isError()) {
                                //clear all old data
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


                                //save user
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    startActivity(new Intent(SignupActivity.this, SplashscreenActivity.class));

                                    finish();
                                    overridePendingTransition(0, 0);

                                }, 500);

                                Toastie.allCustom(SignupActivity.this)
                                        .setTypeFace(Typeface.DEFAULT_BOLD)
                                        .setTextSize(16)
                                        .setCardRadius(25)
                                        .setCardElevation(10)
                                        .setIcon(R.drawable.success_circle)
                                        .setCardBackgroundColor(R.color.purple_500)
                                        .setMessage(response1.getMessage())
                                        .setGravity(Gravity.BOTTOM, 5, 5)
                                        .createToast(Toast.LENGTH_LONG)
                                        .show();

                            } else {
                                Log.e("error", response1.getMessage());
                                Toastie.allCustom(SignupActivity.this)
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
                            }

                        } else {
                            Log.e("error", "No server response");
                            Toastie.allCustom(SignupActivity.this)
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
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        try {
                            Log.e("error", "Connection problem or " + t.getMessage());
                            progressDialog.closeDialog();
                            Toastie.allCustom(SignupActivity.this)
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                checkInternet.showInternetDialog(this);
            }
        } else {
            Toastie.error(this, "Make sure you fill fields correctly!", 1000);
        }
    }
}