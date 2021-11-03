
package com.example.malonda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.models.Product;
import com.example.malonda.models.ProductSales;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.utils.MyProgressDialog;

import java.util.List;


public class SaleProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    private List<ProductSales> saleList;
    AppDatabase room_db;
    MyProgressDialog progressDialog;
    AlertDialog alertDialog;

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public SaleProductsAdapter(Context context, List<ProductSales> saleList) {
        this.context = context;
        this.saleList = saleList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;


        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_recycler_line, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        room_db = AppDatabase.getDbInstance(context);
        ProductSales sale = saleList.get(position);
        int product_id = sale.getProduct_id();
        Product product = room_db.productDao().findByProductId(product_id);

        if (holder instanceof MyViewHolder) {
            ((SaleProductsAdapter.MyViewHolder) holder).textViewName.setText(product.getProduct_name());
            ((SaleProductsAdapter.MyViewHolder) holder).textViewQty.setText("" + sale.getQty());
            ((SaleProductsAdapter.MyViewHolder) holder).textViewPrice.setText("K " + product.getPrice());
            ((SaleProductsAdapter.MyViewHolder) holder).textViewTotal.setText("K " + (sale.getQty() * (Integer.parseInt(product.getPrice()))));

            ((MyViewHolder) holder).container.setOnClickListener(view -> {


            });
        }


    }

    @Override
    public int getItemCount() {
        return saleList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return HIDE_MENU;

    }

    //filter list'
    // method for filtering our recyclerview items.
    public void filterList(List<ProductSales> saleList) {
        // below line is to add our filtered
        // list in our course array list.
        this.saleList = saleList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    public void swapItems(List<ProductSales> saleList) {
        this.saleList = saleList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName, textViewQty, textViewPrice, textViewTotal;
        FrameLayout container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.frameSale);
            textViewName = itemView.findViewById(R.id.saleProdName);
            textViewQty = itemView.findViewById(R.id.saleProdQty);
            textViewPrice = itemView.findViewById(R.id.saleProdPrice);
            textViewTotal = itemView.findViewById(R.id.saleProdTotal);


            itemView.setOnLongClickListener(this);

        }


        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


}
