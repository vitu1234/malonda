package com.example.malonda.buyer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.TerminalAdapter;
import com.example.malonda.common.LoginActivity;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.POS;
import com.example.malonda.models.Product;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BusinessProductsActivity extends AppCompatActivity {
    RecyclerView recyclerViewPosTerminal;
    TerminalAdapter adapter;
    AppDatabase roomdb;
    List<Product> productList = new ArrayList<>();
    List<POS> posList;
    public TextView textViewWarning, textViewBusName;
    TextInputLayout textInputLayoutSearch;
    MyProgressDialog progressDialog;
    ImageView imageViewBarcode, sortListImageView, imageViewLogout;

    int business_id = -1, bus_user_id;

    double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_products);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        recyclerViewPosTerminal = findViewById(R.id.productsListPos);
        textViewWarning = findViewById(R.id.posWarning);
        textInputLayoutSearch = findViewById(R.id.posSearchProduct);
        sortListImageView = findViewById(R.id.sortListImageview);
        imageViewLogout = findViewById(R.id.buyerMenu);
        textViewBusName = findViewById(R.id.businessName);


        roomdb = AppDatabase.getDbInstance(this);

        Intent intent = getIntent();
        business_id = intent.getIntExtra("business_id", -1);
        BusinessInfo businessInfo = roomdb.businessInfoDao().findByBusinessInfoId(business_id);
        bus_user_id = businessInfo.getUser_id();
        textViewBusName.setText(businessInfo.getBusiness_name() + " Products");

        productList = roomdb.productDao().getAllUserProductsAvailable(bus_user_id);
        progressDialog = new MyProgressDialog(this);


        setViews();


        setRecyclerView();

    }

    private void setViews() {
        textInputLayoutSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imageViewLogout.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.overflow_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_login) {
                    startActivity(new Intent(view.getContext(), LoginActivity.class));
                } else if (menuItem.getItemId() == R.id.menu_nearby) {
                    startActivity(new Intent(this, NearByBusinessesActivity.class));
                    this.overridePendingTransition(0, 0);
                } else if (menuItem.getItemId() == R.id.menu_trending) {

                } else if (menuItem.getItemId() == R.id.menu_suggestions) {

                } else if (menuItem.getItemId() == R.id.menu_about) {

                }
                return true;
            });

            popup.show();
        });


        sortListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortList();
            }
        });


    }

    private void checkOutReceipt() {
        total = 0;
        Intent intent = new Intent(this, SalesCheckoutActivity.class);
        startActivity(intent);
//        Toast.makeText(getContext(), "checkout", Toast.LENGTH_SHORT).show();
    }

    private void setRecyclerView() {
        if (productList.size() > 0) {
            adapter = new TerminalAdapter(this, productList);

            // setting grid layout manager to implement grid view.
            // in this method '2' represents number of columns to be displayed in grid view.
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

            // at last set adapter to recycler view.
            recyclerViewPosTerminal.setLayoutManager(layoutManager);
            recyclerViewPosTerminal.setAdapter(adapter);
        } else {
            recyclerViewPosTerminal.setVisibility(View.GONE);
            textViewWarning.setText("No items availabe now!");
            textViewWarning.setVisibility(View.VISIBLE);
        }

    }

    //filtering the list
    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : roomdb.productDao().getAllUserProductsAvailable(bus_user_id)) {
            if (product.getProduct_name().toLowerCase().contains(text.toLowerCase()) || product.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
                textViewWarning.setVisibility(View.INVISIBLE);
            } else {
                textViewWarning.setVisibility(View.VISIBLE);
                textViewWarning.setText("Change query for more filter results!");
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    public void sortList() {

        adapter.sortList(sortByName(productList));
    }

    public List<Product> sortByName(List<Product> productList) {

        Collections.sort(productList, (product1, product2) -> product1.getProduct_name().compareTo(product2.getProduct_name()));
        return productList;
    }
}