package com.example.malonda.supplier.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.POSAdapter;
import com.example.malonda.models.Category;
import com.example.malonda.models.POS;
import com.example.malonda.models.Product;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class POSActivity extends AppCompatActivity {
    RecyclerView recyclerViewPosTerminal;
    POSAdapter adapter;
    AppDatabase roomdb;
    List<Product> productList;
    List<Category> categoryList;
    List<POS> posList;
    public TextView textViewWarning, textViewTotalItems;
    TextInputLayout textInputLayoutSearch;
    MyProgressDialog progressDialog;
    ImageView imageViewBarcode, sortListImageView, imageViewLogout;
    public Button buttonDiscard;
    ImageCarousel carousel;
    List<CarouselItem> list = new ArrayList<>();
    AlertDialog alertDialog;
    double total = 0;
    TextView textViewCategories;


    int user_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_o_s);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        recyclerViewPosTerminal = findViewById(R.id.productsListPos);
        textViewWarning = findViewById(R.id.posWarning);
        textViewTotalItems = findViewById(R.id.itemsTotal);
        textInputLayoutSearch = findViewById(R.id.posSearchProduct);
        buttonDiscard = findViewById(R.id.discardBtn);
        sortListImageView = findViewById(R.id.sortListImageview);
        imageViewLogout = findViewById(R.id.buyerMenu);
        carousel = findViewById(R.id.carousel);
        textViewCategories = findViewById(R.id.showCategories);


        roomdb = AppDatabase.getDbInstance(this);

        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();

        productList = roomdb.productDao().getAllUserProducts(user_id);
        categoryList = roomdb.categoryDao().getAllCategorys();
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
        buttonDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomdb.posDao().deleteAllPos();
                buttonDiscard.setVisibility(View.GONE);
                textViewTotalItems.setText("No Items: K0.00");
            }
        });


        posList = roomdb.posDao().getAllPos();
        for (int i = 0; i < posList.size(); i++) {
            total += posList.get(i).getTotal();
        }

        textViewTotalItems.setOnClickListener(v -> {
            posList.clear();
            posList = roomdb.posDao().getAllPos();
            for (int i = 0; i < posList.size(); i++) {
                total += posList.get(i).getTotal();
            }
            if (posList.size() > 0) {
                textViewTotalItems.setText("Items: K" + total);
                checkOutReceipt();
            } else {
                progressDialog.showErrorToast("No items!");
            }
        });

//        sortListImageView.setOnClickListener(v -> sortList());
        sortListImageView.setOnClickListener(view -> showSortDialog());


    }

    private void showSortDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.sort_prompt_box, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        RadioGroup radioGroupOptions = promptsView.findViewById(R.id.search_filter);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Go",
                        (dialog, id) -> {
                            //save here
                            Boolean wantToCloseDialog = false;
                            //Do stuff, possibly set wantToCloseDialog to true then...
                            if (wantToCloseDialog) {
                                alertDialog.dismiss();
                            } else {


                                radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
                                    Log.e("dld", String.valueOf(checkedId) + " CHECKED");

//                                    if (checkedId == R.id.search_ascending) {
//                                        filterRecyclerView("price_asc");
//                                    } else if (checkedId == R.id.search_descending) {
//                                        filterRecyclerView("price_desc");
//                                    } else if (checkedId == R.id.search_az) {
//                                        filterRecyclerView("az_sort");
//                                    } else if (checkedId == R.id.search_za) {
//                                        filterRecyclerView("za_sort");
//                                    }
                                });

                                if (radioGroupOptions.getCheckedRadioButtonId() == R.id.search_ascending) {
                                    filterRecyclerView("price_asc");
                                } else if (radioGroupOptions.getCheckedRadioButtonId() == R.id.search_descending) {
                                    filterRecyclerView("price_desc");
                                } else if (radioGroupOptions.getCheckedRadioButtonId() == R.id.search_az) {
                                    filterRecyclerView("az_sort");
                                } else if (radioGroupOptions.getCheckedRadioButtonId() == R.id.search_za) {
                                    filterRecyclerView("za_sort");
                                }


                            }
                        })
                .setNegativeButton("Close",
                        (dialog, id) -> dialog.cancel());

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple_700));

    }


    private void checkOutReceipt() {
        total = 0;
        Intent intent = new Intent(this, SalesCheckoutActivity.class);
        this.startActivity(intent);
//        Toast.makeText(this, "checkout", Toast.LENGTH_SHORT).show();
    }

    private void setRecyclerView() {

        if (productList.size() > 0) {
            adapter = new POSAdapter(this, this, productList);

            // setting grid layout manager to implement grid view.
            // in this method '2' represents number of columns to be displayed in grid view.
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);


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
        for (Product product : roomdb.productDao().getAllUserProducts(user_id)) {
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
        if (adapter != null) {
            adapter.sortList(sortByName(productList));
        }
    }

    private void filterRecyclerView(String sort_qry) {
        List<Product> filteredList = new ArrayList<>();
        Log.e("dsfafa", sort_qry);
        if (sort_qry.equals("price_asc")) {
            for (Product product : roomdb.productDao().getAllUserProductsAvailablePriceAsc(user_id)) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("price_desc")) {
            for (Product product : roomdb.productDao().getAllUserProductsAvailablePriceDesc(user_id)) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("az_sort")) {
            for (Product product : roomdb.productDao().getAllUserProductsAvailable(user_id)) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("za_sort")) {
            for (Product product : roomdb.productDao().getAllProductsAvailableNameDesc(user_id)) {
                filteredList.add(product);
            }
        }

        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }


    public List<Product> sortByName(List<Product> productList) {

        Collections.sort(productList, (product1, product2) -> product1.getProduct_name().compareTo(product2.getProduct_name()));
        return productList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (roomdb.posDao().countAllPos() > 0) {
            roomdb.posDao().deleteAllPos();
            buttonDiscard.setVisibility(View.GONE);
            textViewTotalItems.setText("No Items: K0.00");
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }
}