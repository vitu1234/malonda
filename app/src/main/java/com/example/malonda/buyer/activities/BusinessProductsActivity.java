package com.example.malonda.buyer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.CustomSpinnerAdapter;
import com.example.malonda.adapters.TerminalAdapter;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.Product;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.supplier.activities.SalesCheckoutActivity;
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
    List<Category> categoryList;
    public TextView textViewWarning, textViewBusName;
    TextInputLayout textInputLayoutSearch;
    MyProgressDialog progressDialog;
    ImageView imageViewBarcode, sortListImageView, imageViewLogout;
    AlertDialog alertDialog;
    int business_id = -1, bus_user_id;
    TextView textViewCategories;
    int show_category = -1;

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
        textViewCategories = findViewById(R.id.showCategories1);


        roomdb = AppDatabase.getDbInstance(this);

        Intent intent = getIntent();
        business_id = intent.getIntExtra("business_id", -1);
        BusinessInfo businessInfo = roomdb.businessInfoDao().findByBusinessInfoId(business_id);
        bus_user_id = businessInfo.getUser_id();
        textViewBusName.setText(businessInfo.getBusiness_name() + " Products");

        productList = roomdb.productDao().getAllUserProductsAvailable(bus_user_id);
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
        imageViewLogout.setOnClickListener(view -> {
            onBackPressed();
        });


//        sortListImageView.setOnClickListener(v -> sortList());
        sortListImageView.setOnClickListener(view -> showSortDialog());
        textViewCategories.setOnClickListener(view -> setSpinner());

    }

    private void setSpinner() {
        String[] category_names = new String[categoryList.size()];
        int[] category_ids = new  int[categoryList.size()];
        for (int i =0;i<categoryList.size();i++){
            category_ids[i]=categoryList.get(i).getCategory_id();
            category_names[i]=categoryList.get(i).getCategory_name();
        }

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.prompt_spinner_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        Spinner spinner = promptsView.findViewById(R.id.categorySpinner);
        CustomSpinnerAdapter customAdapter=new CustomSpinnerAdapter(getApplicationContext(),category_ids,category_names);
        spinner.setAdapter(customAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("e",category_names[i]+" ID: "+category_ids[i]);
                show_category = category_ids[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
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

                                if (show_category != -1 ){
                                    Intent intent = new Intent(getApplicationContext(), CategoryProductsActivity.class);
                                    intent.putExtra("category_id",show_category);
                                    intent.putExtra("bus_user_id",bus_user_id);
                                    startActivity(intent);
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
        startActivity(intent);
//        Toast.makeText(this, "checkout", Toast.LENGTH_SHORT).show();
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

                                    if (checkedId == R.id.search_ascending) {
                                        filterRecyclerView("price_asc");
                                    } else if (checkedId == R.id.search_descending) {
                                        filterRecyclerView("price_desc");
                                    } else if (checkedId == R.id.search_az) {
                                        filterRecyclerView("az_sort");
                                    } else if (checkedId == R.id.search_za) {
                                        filterRecyclerView("za_sort");
                                    }
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

    private void filterRecyclerView(String sort_qry) {
        List<Product> filteredList = new ArrayList<>();
        Log.e("dsfafa", sort_qry);
        if (sort_qry.equals("price_asc")) {
            for (Product product : roomdb.productDao().getAllProductsAvailablePriceAsc(bus_user_id)) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("price_desc")) {
            for (Product product : roomdb.productDao().getAllProductsAvailablePriceDesc(bus_user_id)) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("az_sort")) {
            for (Product product : roomdb.productDao().getAllUserProductsAvailable(bus_user_id)) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("za_sort")) {
            for (Product product : roomdb.productDao().getAllProductsAvailableNameDesc(bus_user_id)) {
                filteredList.add(product);
            }
        }

        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }


}