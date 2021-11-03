package com.example.malonda.supplier.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.SaleProductsAdapter;
import com.example.malonda.models.ProductSales;
import com.example.malonda.models.Sale;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class SaleProductsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SaleProductsAdapter adapter;
    final Context context = this;
    AppDatabase room_db;
    List<ProductSales> saleList = new ArrayList<>();
    TextView textViewProductsWarning, textViewName, textViewPhone, textViewTotal;


    int user_id = -1, sale_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_products);
        recyclerView = findViewById(R.id.productsSaleListRecycler);
        textViewProductsWarning = findViewById(R.id.productWarning);
        textViewName = findViewById(R.id.customerName);
        textViewPhone = findViewById(R.id.customerPhone);
        textViewTotal = findViewById(R.id.productsAmount);


        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
        room_db = AppDatabase.getDbInstance(this);

        Intent intent = this.getIntent();
        sale_id = intent.getIntExtra("sale_id", -1);

        Sale sale = room_db.saleDao().findBySaleId(sale_id);

        textViewTotal.setText("MWK " + sale.getTotal_amount() + " | " + sale.getPayment_method());
        textViewPhone.setText(sale.getCustomer_phone());
        textViewName.setText(sale.getCustomer_name());

        setRecyclerView();
    }

    private void setRecyclerView() {
        if (room_db.productSalesDao().getSingleProductSalesCount(sale_id) > 0) {
            saleList = room_db.productSalesDao().getAllProductSalesBySaleID(sale_id);

            adapter = new SaleProductsAdapter(this, saleList);

// Initialize the RecyclerView and attach the Adapter to it as usual

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//line between items
            recyclerView.setAdapter(adapter);

        } else {
            textViewProductsWarning.setVisibility(View.VISIBLE);
        }
    }

    public void goBackMxendu(View view) {
        onBackPressed();
    }
}