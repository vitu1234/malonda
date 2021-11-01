package com.example.malonda.supplier.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.ProductsAdapter;
import com.example.malonda.models.Product;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.utils.BetterActivityResult;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MyProductsActivity extends AppCompatActivity {
    ProductsAdapter adapter;
    TextView textViewProductsWarning;
    final Context context = this;
    AppDatabase room_db;
    List<Product> productList = new ArrayList<>();

    int user_id =-1;

    //properties
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    TextInputLayout textInputLayoutSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

        room_db = AppDatabase.getDbInstance(this);
        textViewProductsWarning = findViewById(R.id.productWarning);
        textInputLayoutSearch = findViewById(R.id.searchProductText);
        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setProductsRecycler();

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
    }

    public void openAddProduct(View view) {
        startActivity(new Intent(this, AddEditProductActivity.class));
    }

    public void goBackMenu(View view) {
        onBackPressed();
    }

    private void setProductsRecycler() {
        if (room_db.productDao().getUserProductCount(user_id) > 0) {
            productList = room_db.productDao().getAllUserProducts(user_id);

            adapter = new ProductsAdapter(this, productList);

// Initialize the RecyclerView and attach the Adapter to it as usual
            RecyclerView recyclerView = findViewById(R.id.productsListRecycler);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//line between items
            recyclerView.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        adapter.closeMenu();
                    }
                });
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

        } else {
            textViewProductsWarning.setVisibility(View.VISIBLE);
        }

    }
    //filtering the list
    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : room_db.productDao().getAllUserProducts(user_id)) {
            if (product.getProduct_name().toLowerCase().contains(text.toLowerCase()) || product.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
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
        if (adapter != null){
            productList.clear();
            productList = room_db.productDao().getAllUserProducts(user_id);
            adapter.filterList(productList);
        }

    }
}