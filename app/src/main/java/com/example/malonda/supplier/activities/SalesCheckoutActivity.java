package com.example.malonda.supplier.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.SaleAdapter;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.models.AllDataResponse;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.POS;
import com.example.malonda.models.Product;
import com.example.malonda.models.ProductSales;
import com.example.malonda.models.Sale;
import com.example.malonda.models.Unit;
import com.example.malonda.models.User;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.utils.CheckInternet;
import com.example.malonda.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.mrntlu.toastie.Toastie;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesCheckoutActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SaleAdapter adapter;
    AppDatabase room_db;
    List<POS> posList;

    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<BusinessInfo> businessInfoList;
    private List<Product> productList;
    private List<Unit> unitList;
    private List<Sale> saleList;
    private List<ProductSales>productSalesList ;

    CheckInternet checkInternet;
    MyProgressDialog progressDialog;
    AlertDialog alertDialog;
    String cus_name = "cus_name", cus_phone = "cus_phone";

    public TextView textViewSubTotal;

    ImageButton imageButtonSavePrint;

    public double sub_total_amount = 0;
    public double total_amount = 0, VAT, total_final, paid_amount = 0, discount, checked_qty = 0, change = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_checkout);
        recyclerView = findViewById(R.id.salesListRecycler);

        textViewSubTotal = findViewById(R.id.saleSubTotal);


        VAT = 16.7;
        room_db = AppDatabase.getDbInstance(this);
        checkInternet = new CheckInternet(this);
        progressDialog = new MyProgressDialog(this);

        setViews();
        setSalesRecylerView();


    }

    public void setViews() {
        sub_total_amount = 0;
        total_amount = 0;
        posList = room_db.posDao().getAllPosGrouped();
        for (int position = 0; position < posList.size(); position++) {
            POS pos = posList.get(position);
            Product entity = room_db.productDao().findByProductId(pos.getProduct_id());
            int qty = room_db.posDao().totalProQtyPosCount(pos.getProduct_id());

            sub_total_amount += (double) (qty * (Integer.parseInt(entity.getPrice())));
            total_amount += (double) (qty * (Integer.parseInt(entity.getPrice())));

        }

        textViewSubTotal.setText("K " + sub_total_amount);
    }


    private void setSalesRecylerView() {

        if (room_db.posDao().countAllPos() > 0) {
            adapter = new SaleAdapter(this, this, posList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//line between items
            recyclerView.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> adapter.closeMenu());
            }

            ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.teal_200));

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.showMenu(viewHolder.getAdapterPosition());
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                    View itemView = viewHolder.itemView;

                    if (dX > 0) {
                        background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                    } else if (dX < 0) {
                        background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    } else {
                        background.setBounds(0, 0, 0, 0);
                    }

                    background.draw(c);
                }

//            @Override
//            public float getSwipeThreshold( RecyclerView.ViewHolder viewHolder){
//
//                return .5f;
//            }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }


    public void goback(View view) {
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

                            saleList = response1.getSales();
                            for (int i = 0; i < saleList.size(); i++) {
                                room_db.saleDao().insertSale(saleList.get(i));
                            }

                            productSalesList = response1.getProduct_sales();
                            for (int i = 0; i < productSalesList.size(); i++) {
                                room_db.productSalesDao().insertProductSales(productSalesList.get(i));
                            }


                            Toastie.allCustom(SalesCheckoutActivity.this)
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
                            Toastie.allCustom(SalesCheckoutActivity.this)
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
                        Toastie.allCustom(SalesCheckoutActivity.this)
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
                    Toastie.allCustom(SalesCheckoutActivity.this)
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

    private void reopenActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            recreate();

            finish();
        }, 800);
    }


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

    @Override
    protected void onResume() {
        super.onResume();
        if (room_db.posDao().countAllPos() == 0) {
            Toast.makeText(this, "Nothing here", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void setPayments(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_payment_method, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        TextInputLayout textInputLayoutPhone = promptsView.findViewById(R.id.cusPhone), textInputLayoutName = promptsView.findViewById(R.id.custName);
        LinearLayout linearLayoutCash = promptsView.findViewById(R.id.linearCash), linearLayoutCard = promptsView.findViewById(R.id.linearCard);

        linearLayoutCard.setOnClickListener(view1 -> {
            if (textInputLayoutName.getEditText().getText().toString().isEmpty()) {
                textInputLayoutName.setError("Required");
                textInputLayoutName.setErrorEnabled(true);
                return;
            } else {
                textInputLayoutName.setError(null);
                textInputLayoutName.setErrorEnabled(false);
            }

            if (textInputLayoutPhone.getEditText().getText().toString().isEmpty()) {
                textInputLayoutPhone.setError("Required");
                textInputLayoutPhone.setErrorEnabled(true);
                return;
            } else {
                textInputLayoutPhone.setError(null);
                textInputLayoutPhone.setErrorEnabled(false);
            }
            cardPayment(textInputLayoutPhone.getEditText().getText().toString(), textInputLayoutName.getEditText().getText().toString());
        });

        linearLayoutCash.setOnClickListener(view12 -> {
            if (textInputLayoutName.getEditText().getText().toString().isEmpty()) {
                textInputLayoutName.setError("Required");
                textInputLayoutName.setErrorEnabled(true);
                return;
            } else {
                textInputLayoutName.setError(null);
                textInputLayoutName.setErrorEnabled(false);
            }

            if (textInputLayoutPhone.getEditText().getText().toString().isEmpty()) {
                textInputLayoutPhone.setError("Required");
                textInputLayoutPhone.setErrorEnabled(true);
                return;
            } else {
                textInputLayoutPhone.setError(null);
                textInputLayoutPhone.setErrorEnabled(false);
            }
            saveSale(textInputLayoutPhone.getEditText().getText().toString(), textInputLayoutName.getEditText().getText().toString(), "Cash Payment");
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("VIEW",
                        (dialog, id) -> {
                            //save here
                            Boolean wantToCloseDialog = false;
                            //Do stuff, possibly set wantToCloseDialog to true then...
                            if (wantToCloseDialog) {
                                alertDialog.dismiss();
                            } else {


                            }
                        })
                .setNegativeButton("Close",
                        (dialog, id) -> dialog.cancel());

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
    }

    private void cardPayment(String phone, String name) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("cus_name", name);
        intent.putExtra("cus_phone", phone);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}