
package com.example.malonda.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.models.Sale;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.supplier.activities.SaleProductsActivity;
import com.example.malonda.utils.MyProgressDialog;

import java.util.List;


public class BusSalesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    private List<Sale> saleList;
    AppDatabase room_db;
    MyProgressDialog progressDialog;
    AlertDialog alertDialog;

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public BusSalesAdapter(Context context, List<Sale> saleList) {
        this.context = context;
        this.saleList = saleList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;


        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_bus_sales_line, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        room_db = AppDatabase.getDbInstance(context);
        Sale sale = saleList.get(position);
        int products_bus = room_db.productSalesDao().getSingleProductSalesCount(sale.getSale_id());

        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(sale.getCustomer_name());
            ((MyViewHolder) holder).textViewPrice.setText("MWK " + sale.getTotal_amount() + " - " + sale.getPayment_method());
            ((MyViewHolder) holder).textViewDate.setText("" + sale.getDate_created());


            ((MyViewHolder) holder).textViewProdCount.setText(products_bus + " Products");

            ((MyViewHolder) holder).container.setOnClickListener(view -> {
                Intent intent = new Intent(context, SaleProductsActivity.class);
                intent.putExtra("sale_id", sale.getSale_id());
                context.startActivity(intent);

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
    public void filterList(List<Sale> saleList) {
        // below line is to add our filtered
        // list in our course array list.
        this.saleList = saleList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    public void swapItems(List<Sale> saleList) {
        this.saleList = saleList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName, textViewPrice, textViewDate, textViewProdCount;
        RelativeLayout container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.salesContainer);
            textViewName = itemView.findViewById(R.id.sale_cust_name);
            textViewPrice = itemView.findViewById(R.id.saleAmount);
            textViewDate = itemView.findViewById(R.id.saleDate);
            textViewProdCount = itemView.findViewById(R.id.saleCount);

            itemView.setOnLongClickListener(this);

        }


        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


}
