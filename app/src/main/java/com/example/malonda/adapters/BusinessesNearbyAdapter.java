package com.example.malonda.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.buyer.activities.BusinessProductsActivity;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.BusinessLocationDistance;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.utils.MyProgressDialog;

import java.util.List;


public class BusinessesNearbyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    private List<BusinessLocationDistance> businessLocationDistanceList;
    AppDatabase room_db;
    MyProgressDialog progressDialog;
    AlertDialog alertDialog;

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public BusinessesNearbyAdapter(Context context, List<BusinessLocationDistance> businessLocationDistanceList) {
        this.context = context;
        this.businessLocationDistanceList = businessLocationDistanceList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;


        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_line_nearby_businesses, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        room_db = AppDatabase.getDbInstance(context);
        BusinessLocationDistance businessLocationDistance = businessLocationDistanceList.get(position);
        BusinessInfo entity = room_db.businessInfoDao().findByBusinessInfoId(businessLocationDistance.getBusiness_id());


        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(entity.getBusiness_name());
            ((MyViewHolder) holder).textViewPhone.setText("" + entity.getBusiness_phone());
            ((MyViewHolder) holder).textViewAddress.setText("" + entity.getBusiness_address());
            ((MyViewHolder) holder).textViewKilometres.setText("" +(Math.round( businessLocationDistance.getKm_from_me() * 100.0) / 100.0) + "KM away");

            ((MyViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BusinessProductsActivity.class);
                    intent.putExtra("business_id", entity.getBusiness_id());
                    context.startActivity(intent);

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return businessLocationDistanceList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return HIDE_MENU;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName, textViewPhone, textViewAddress, textViewKilometres;
        FrameLayout container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.frameSale2);
            textViewName = itemView.findViewById(R.id.busName);
            textViewPhone = itemView.findViewById(R.id.busPhone);
            textViewAddress = itemView.findViewById(R.id.busAddress);
            textViewKilometres = itemView.findViewById(R.id.busDistance);

            itemView.setOnLongClickListener(this);

        }


        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


}
