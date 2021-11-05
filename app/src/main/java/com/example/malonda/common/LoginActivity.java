package com.example.malonda.common;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.malonda.R;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.buyer.activities.BuyerDashboardActivity;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.LoginResponse;
import com.example.malonda.models.Product;
import com.example.malonda.models.ProductSales;
import com.example.malonda.models.Sale;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.supplier.activities.SupplierDashboardActivity;
import com.example.malonda.utils.CheckInternet;
import com.example.malonda.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.mrntlu.toastie.Toastie;

import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Call<LoginResponse> call;
    CheckInternet checkInternet;
    TextInputLayout textEmail, textPassword;
    //dialog
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
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //hooks
        textEmail = findViewById(R.id.emailLayout);
        textPassword = findViewById(R.id.passwordLayout);
        checkInternet = new CheckInternet(this);
        progressDialog = new MyProgressDialog(this);
        room_db = AppDatabase.getDbInstance(this);
    }

    public void forgetPassword(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgetPassword1Activity.class);

        //add shared animation
        Pair[] pairs = new Pair[1];//number of elements to be animated
        pairs[0] = new Pair<View, String>(view.findViewById(R.id.forgetBtn), "forgetTransition");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void login(View view) {

        if (validateEmail() && validatePassword()) {
            if (checkInternet.isInternetConnected(this)) {
                progressDialog.showDialog("Background check...");
                String password = textPassword.getEditText().getText().toString().trim();
                String mail = textEmail.getEditText().getText().toString().trim();

                call = RetrofitClient.getInstance().getApi().loginUser(mail, password);
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
                                SharedPrefManager sharedPrefManager = new SharedPrefManager(LoginActivity.this);
                                sharedPrefManager.saveUser(response1.getUser());
                                User user = response1.getUser();
                                int user_id = user.getUser_id();

                                String role = room_db.userDao().findByUserId(user_id).getUser_type();


                                Intent intent;

                                if (role.equals("customer")) {
                                    intent = new Intent(LoginActivity.this, BuyerDashboardActivity.class);
                                    sharedPrefManager.SaveAccountType("customer");
                                } else {
                                    sharedPrefManager.SaveAccountType("supplier");
                                    intent = new Intent(LoginActivity.this, SupplierDashboardActivity.class);

                                }
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);

                            } else {
                                Log.e("error",response1.getMessage());
                                Toastie.allCustom(LoginActivity.this)
                                        .setTypeFace(Typeface.DEFAULT_BOLD)
                                        .setTextSize(16)
                                        .setCardRadius(25)
                                        .setCardElevation(10)
                                        .setIcon(R.drawable.ic_error_black_24dp)
                                        .setCardBackgroundColor(R.color.red)
                                        .setMessage(response1.getMessage())
                                        .setGravity(Gravity.BOTTOM,5,5)
                                        .createToast(Toast.LENGTH_LONG)
                                        .show();
                            }

                        } else {
                            Log.e("error","No server response");
                            Toastie.allCustom(LoginActivity.this)
                                    .setTypeFace(Typeface.DEFAULT_BOLD)
                                    .setTextSize(16)
                                    .setCardRadius(25)
                                    .setCardElevation(10)
                                    .setIcon(R.drawable.ic_error_black_24dp)
                                    .setCardBackgroundColor(R.color.red)
                                    .setMessage("No server response!")
                                    .setGravity(Gravity.BOTTOM,5,5)
                                    .createToast(Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        try {
                            Log.e("error","Connection problem or "+t.getMessage());
                            progressDialog.closeDialog();
                            Toastie.allCustom(LoginActivity.this)
                                    .setTypeFace(Typeface.DEFAULT_BOLD)
                                    .setTextSize(16)
                                    .setCardRadius(25)
                                    .setCardElevation(10)
                                    .setIcon(R.drawable.ic_error_black_24dp)
                                    .setCardBackgroundColor(R.color.red)
                                    .setMessage("Connection problem or "+t.getMessage())
                                    .setGravity(Gravity.BOTTOM,5,5)
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
            Toastie.error(this,"Make sure you fill fields correctly!",1000);
        }

    }

    public void toSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);


            startActivity(intent);

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
        String mail = textEmail.getEditText().getText().toString().trim();
        String check_spaces = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (mail.isEmpty()) {
            textEmail.setError("Required field");
            textEmail.setErrorEnabled(true);
            return false;
        } else {

                if (isValidPhoneNumber(mail)) {
                    textEmail.setError(null);
                    textEmail.setErrorEnabled(false);
                    return true;
                } else {
                    textEmail.setError("Enter a valid phone number");
                    textEmail.setErrorEnabled(true);
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