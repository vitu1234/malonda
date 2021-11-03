package com.example.malonda.supplier.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.malonda.R;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.POS;
import com.example.malonda.models.Product;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.payment.PayPalConfig;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.utils.CheckInternet;
import com.example.malonda.utils.MyProgressDialog;
import com.mrntlu.toastie.Toastie;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    PaymentButton payPalButton;
    String amount = "";
    String cus_name = "cus_name", cus_phone = "cus_phone";
    double total_amount = 0;

    CheckInternet checkInternet;
    MyProgressDialog progressDialog;
    SharedPrefManager sharedPrefManager;
    Call<AllDataResponse> call;

    private List<User> userList;
    private List<Category> categoryList;
    private List<BusinessInfo> businessInfoList;
    private List<Product> productList;
    private List<Unit> unitList;
    List<POS> posList;

    AppDatabase room_db;
    PayPalConfig payPalConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        payPalButton = findViewById(R.id.payPalButton);
        checkInternet = new CheckInternet(this);
        progressDialog = new MyProgressDialog(this);
        room_db = AppDatabase.getDbInstance(this);
        sharedPrefManager = new SharedPrefManager(this);
        payPalConfig = new PayPalConfig(this);
        payPalConfig.configurePayPal();

        Intent intent = this.getIntent();

        cus_name = intent.getStringExtra("cus_name");
        cus_phone = intent.getStringExtra("cus_phone");
        total_amount = 0;
        posList = room_db.posDao().getAllPosGrouped();
        for (int position = 0; position < posList.size(); position++) {
            POS pos = posList.get(position);
            Product entity = room_db.productDao().findByProductId(pos.getProduct_id());
            int qty = room_db.posDao().totalProQtyPosCount(pos.getProduct_id());
            total_amount += (double) (qty * (Integer.parseInt(entity.getPrice())));

        }


        convertCurrency();
    }

    private void convertCurrency() {
        if (total_amount != 0) {
            //check internet
            if (checkInternet.isInternetConnected(this)) {
                progressDialog.showDialog("Please wait...");
                call = RetrofitClient.getInstance().getApi().convertCurrency(String.valueOf(total_amount));
                call.enqueue(new Callback<AllDataResponse>() {
                    @Override
                    public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                        AllDataResponse response1 = response.body();
                        progressDialog.closeDialog();
                        if (response1 != null) {
                            if (!response1.isError()) {
                                //clear all old data
//                                room_db.clearAllTables();


                                payPalButton.setVisibility(View.VISIBLE);
                                Log.e("amountUSD", String.valueOf(Double.valueOf(response1.getMessage())));

                                payPalButton.setup(
                                        createOrderActions -> {
                                            ArrayList purchaseUnits = new ArrayList<>();
                                            purchaseUnits.add(
                                                    new PurchaseUnit.Builder()
                                                            .amount(
                                                                    new Amount.Builder()
                                                                            .currencyCode(CurrencyCode.USD)
                                                                            .value(String.format("%.2f", Double.valueOf(9)))
                                                                            .build()
                                                            )
                                                            .build()
                                            );
                                            Order order = new Order(
                                                    OrderIntent.CAPTURE,
                                                    new AppContext.Builder()
                                                            .userAction(UserAction.PAY_NOW)
                                                            .build(),
                                                    purchaseUnits
                                            );
                                            createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                                        },
                                        approval -> approval.getOrderActions().capture(new OnCaptureComplete() {
                                            @Override
                                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                                Log.e("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                                                Toast.makeText(CheckoutActivity.this, "Almost done, please wait...", Toast.LENGTH_SHORT).show();
                                                saveSale(cus_phone, cus_name, "Card Payment");
                                            }
                                        }),
                                        () -> Toast.makeText(CheckoutActivity.this, "You cancelled the payment process", Toast.LENGTH_SHORT).show(),
                                        errorInfo -> Toast.makeText(CheckoutActivity.this, "An error occured | " + errorInfo.getReason(), Toast.LENGTH_SHORT).show()
                                );

                            } else {
                                progressDialog.showDangerAlert("An error occured!");
                            }
                        } else {
                            progressDialog.showDangerAlert("Our servers did not respond");
                        }
                    }

                    @Override
                    public void onFailure(Call<AllDataResponse> call, Throwable t) {
                        try {
                            progressDialog.closeDialog();
                            progressDialog.showDangerAlert("An error occured!");
                        } catch (Exception e) {

                        }
                    }
                });
            } else {
                checkInternet.showInternetDialog(this);
            }
        } else {
            onBackPressed();
            Toast.makeText(this, "We did not get the amount", Toast.LENGTH_SHORT).show();
        }
    }

    public void closeCheckout(View view) {
        onBackPressed();
    }

    public void saveSale(String phone, String name, String payment_method) {
        if (checkInternet.isInternetConnected(this)) {
            progressDialog.showDialog("processing...");
            List<Integer> product_id = new ArrayList<>();
            List<Integer> qty = new ArrayList<>();
            List<POS> posList = room_db.posDao().getAllPos();
            for (int i = 0; i < posList.size(); i++) {
                product_id.add(posList.get(i).getProduct_id());
                qty.add(posList.get(i).getQty());
            }

            call = RetrofitClient.getInstance().getApi().add_sale(phone, name, payment_method, product_id, qty, total_amount, SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id());
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


                            Toastie.allCustom(CheckoutActivity.this)
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
                            Toastie.allCustom(CheckoutActivity.this)
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
                        Toastie.allCustom(CheckoutActivity.this)
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
                }

                @Override
                public void onFailure(Call<AllDataResponse> call, Throwable t) {
                    Toastie.allCustom(CheckoutActivity.this)
                            .setTypeFace(Typeface.DEFAULT_BOLD)
                            .setTextSize(16)
                            .setCardRadius(25)
                            .setCardElevation(10)
                            .setIcon(R.drawable.ic_error_black_24dp)
                            .setCardBackgroundColor(R.color.red)
                            .setMessage("No server response!")
                            .setGravity(Gravity.BOTTOM, 5, 5)
                            .createToast(Toast.LENGTH_LONG)
                            .show();
                    progressDialog.closeDialog();
                }
            });
        } else {
            checkInternet.showInternetDialog(this);
        }

    }

}