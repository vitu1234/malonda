package com.example.malonda.supplier.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.BusSalesAdapter;
import com.example.malonda.models.Sale;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class BusSalesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    BusSalesAdapter adapter;
    TextView textViewProductsWarning;
    final Context context = this;
    AppDatabase room_db;
    List<Sale> saleList = new ArrayList<>();


    TextInputLayout textInputLayoutSearch;

    int user_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_sales);
        recyclerView = findViewById(R.id.busSaleListRecycler);
        textViewProductsWarning = findViewById(R.id.productWarning);
        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
        textInputLayoutSearch = findViewById(R.id.searchProductText);
        room_db = AppDatabase.getDbInstance(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        textInputLayoutSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        setRecyclerView();
    }

    private void setRecyclerView() {
        if (room_db.saleDao().getSingleSaleBusCount(user_id) > 0) {
            saleList = room_db.saleDao().getAllSalesByBusinessID(user_id);

            adapter = new BusSalesAdapter(this, saleList);

// Initialize the RecyclerView and attach the Adapter to it as usual

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//line between items
            recyclerView.setAdapter(adapter);

        } else {
            textViewProductsWarning.setVisibility(View.VISIBLE);
        }
    }


    public void goBackMendu(View view) {
        onBackPressed();
    }

    //filtering the list
    private void filter(String text) {
        List<Sale> filteredList = new ArrayList<>();
        for (Sale sale : room_db.saleDao().getAllSalesByBusinessID(user_id)) {
            if (sale.getCustomer_name().toLowerCase().contains(text.toLowerCase()) || sale.getPayment_method().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(sale);
                textViewProductsWarning.setVisibility(View.INVISIBLE);
            } else {
                textViewProductsWarning.setVisibility(View.VISIBLE);
                textViewProductsWarning.setText("change query for more filter results!");
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(context, "resumesd", Toast.LENGTH_SHORT).show();
        if (adapter != null) {
            saleList.clear();
            saleList = room_db.saleDao().getAllSalesByBusinessID(user_id);
            adapter.filterList(saleList);
        }

    }
}