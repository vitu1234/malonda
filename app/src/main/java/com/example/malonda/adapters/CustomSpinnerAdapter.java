package com.example.malonda.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.malonda.R;

public class CustomSpinnerAdapter extends BaseAdapter {
    Context context;
    int category_ids[];
    String[] category_names;
    LayoutInflater inflter;

    public CustomSpinnerAdapter(Context applicationContext, int[] category_ids, String[] category_names) {
        this.context = applicationContext;
        this.category_ids = category_ids;
        this.category_names = category_names;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return category_names.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_category_layout, null);
        TextView names = (TextView) view.findViewById(R.id.tvCategory);
        TextView ids = (TextView) view.findViewById(R.id.tvCategoryID);
        names.setText(category_names[i]);
        ids.setText(category_ids[i]+"");
        return view;
    }
}