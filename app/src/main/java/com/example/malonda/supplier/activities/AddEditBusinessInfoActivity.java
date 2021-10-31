package com.example.malonda.supplier.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.malonda.R;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.maphelpers.GpsTracker;
import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.Product;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.utils.CheckInternet;
import com.example.malonda.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.mrntlu.toastie.Toastie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditBusinessInfoActivity extends AppCompatActivity {

    TextInputLayout textInputLayoutBusName, textInputLayoutBusPhone, textInputLayoutBusAddress;

    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<BusinessInfo> businessInfoList;
    private List<Product> productList;
    private List<Unit> unitList;

    MyProgressDialog progressDialog;
    AppDatabase room_db;
    String me_longtude = null, me_latitude = null;

    private GpsTracker gpsTracker;

    int business_id = -1, user_id = -1;
    CheckInternet checkInternet;
    BusinessInfo businessInfo;
    ImageView imageViewDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_business_info);

        textInputLayoutBusName = findViewById(R.id.busNameAdd);
        textInputLayoutBusPhone = findViewById(R.id.busPhoneAdd);
        textInputLayoutBusAddress = findViewById(R.id.busAddressAdd);
        imageViewDel = findViewById(R.id.delBus);

        progressDialog = new MyProgressDialog(this);
        checkInternet = new CheckInternet(this);
        room_db = AppDatabase.getDbInstance(this);
        gpsTracker = new GpsTracker(this);
        Intent intent = getIntent();

        if (gpsTracker.canGetLocation()) {
            me_latitude = String.valueOf(gpsTracker.getLatitude());
            me_longtude = String.valueOf(gpsTracker.getLongitude());

            Log.e("ssd", "Lat: " + me_latitude + " Long: " + me_longtude);
        } else {
            gpsTracker.showSettingsAlert();
        }

        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
        // There are no request codes
        if (intent.getIntExtra("business_id", -1) > 0) {
            business_id = intent.getIntExtra("business_id", -1);
            setViewsEdit();
        }

    }

    private void setViewsEdit() {

        if (business_id != -1) {
            if (room_db.businessInfoDao().getSingleBusinessInfoCount(business_id) > 0) {
                imageViewDel.setVisibility(View.VISIBLE);
                businessInfo = room_db.businessInfoDao().findByBusinessInfoId(business_id);
                textInputLayoutBusName.getEditText().setText(businessInfo.getBusiness_name());
                textInputLayoutBusPhone.getEditText().setText(businessInfo.getBusiness_phone());
                textInputLayoutBusAddress.getEditText().setText(businessInfo.getBusiness_address());
            }
        }

    }

    public void goback(View view) {
        onBackPressed();
    }

    public void addBusToServer(View view) {
        if (checkInternet.isInternetConnected(this)) {

            if (validateField(textInputLayoutBusName) && validateField(textInputLayoutBusPhone) && validateField(textInputLayoutBusAddress)) {
                String business_name = textInputLayoutBusName.getEditText().getText().toString();
                String business_phone = textInputLayoutBusPhone.getEditText().getText().toString();
                String business_address = textInputLayoutBusAddress.getEditText().getText().toString();
                if (business_id == -1) {
                    progressDialog.showDialog("Adding...");
                    call = RetrofitClient.getInstance().getApi().add_business_info(user_id, business_name, business_phone, business_address, me_longtude, me_latitude);
                } else {
                    progressDialog.showDialog("Updating...");
                    call = RetrofitClient.getInstance().getApi().update_business_info(user_id, business_id, business_name, business_phone, business_address, me_longtude, me_latitude);
                }

                call.enqueue(new Callback<AllDataResponse>() {
                    @Override
                    public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                        AllDataResponse response1 = response.body();
                        progressDialog.closeDialog();
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

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    Intent intent = getIntent().putExtra("business_id", room_db.businessInfoDao().findByBusinessByUserId(user_id).getBusiness_id());
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);

                                }, 500);

                                Toastie.allCustom(AddEditBusinessInfoActivity.this)
                                        .setTypeFace(Typeface.DEFAULT_BOLD)
                                        .setTextSize(16)
                                        .setCardRadius(25)
                                        .setCardElevation(10)
                                        .setIcon(R.drawable.ic_check_circle_black_24dp)
                                        .setCardBackgroundColor(R.color.purple_500)
                                        .setMessage(response1.getMessage())
                                        .setGravity(Gravity.BOTTOM, 5, 5)
                                        .createToast(Toast.LENGTH_LONG)
                                        .show();

                            } else {


                                Toastie.allCustom(AddEditBusinessInfoActivity.this)
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
                            Toastie.allCustom(AddEditBusinessInfoActivity.this)
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
                        progressDialog.closeDialog();
                        Log.e("error", "Connection problem or " + t.getMessage());
                        Toastie.allCustom(AddEditBusinessInfoActivity.this)
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


        } else {
            checkInternet.showInternetDialog(this);
        }

    }

    public boolean validateField(TextInputLayout textInputLayout) {
        String email = textInputLayout.getEditText().getText().toString();

        if (email.isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Fill field");
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError(null);
            return true;


        }
    }

    public void deleteBusInfo(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure to delete? All related info will be lost");
        builder1.setTitle("Warning");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteBusinessInfo();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.purple_700));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.red));
    }

    private void deleteBusinessInfo() {
        if (checkInternet.isInternetConnected(this)) {
            progressDialog.showDialog("Deleting...");
            call = RetrofitClient.getInstance().getApi().deleteBusiness(business_id);
            call.enqueue(new Callback<AllDataResponse>() {
                @Override
                public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                    AllDataResponse response1 = response.body();
                    progressDialog.closeDialog();
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

                            Toastie.allCustom(AddEditBusinessInfoActivity.this)
                                    .setTypeFace(Typeface.DEFAULT_BOLD)
                                    .setTextSize(16)
                                    .setCardRadius(25)
                                    .setCardElevation(10)
                                    .setIcon(R.drawable.ic_check_circle_black_24dp)
                                    .setCardBackgroundColor(R.color.purple_500)
                                    .setMessage(response1.getMessage())
                                    .setGravity(Gravity.BOTTOM, 5, 5)
                                    .createToast(Toast.LENGTH_LONG)
                                    .show();

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                onBackPressed();
                                overridePendingTransition(0, 0);

                            }, 500);

                        } else {


                            Toastie.allCustom(AddEditBusinessInfoActivity.this)
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
                        Toastie.allCustom(AddEditBusinessInfoActivity.this)
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
                    progressDialog.closeDialog();
                    Log.e("error", "Connection problem or " + t.getMessage());
                    Toastie.allCustom(AddEditBusinessInfoActivity.this)
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
        } else {
            checkInternet.showInternetDialog(this);
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
}