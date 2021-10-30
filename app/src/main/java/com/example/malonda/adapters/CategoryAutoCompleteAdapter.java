package com.example.malonda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.malonda.R;
import com.example.malonda.models.Category;

import java.util.ArrayList;

public class CategoryAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Category> originalList;
    private ArrayList<Category> suggestions = new ArrayList<>();
    private Filter filter = new CustomFilter();

    public CategoryAutoCompleteAdapter(Context context, ArrayList<Category> originalList) {
        this.context = context;
        this.originalList = originalList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dropdown_layout, parent, false);
            }


            Category model = suggestions.get(position);

            AppCompatTextView tvTitle = convertView.findViewById(R.id.text1);
            AppCompatTextView id = convertView.findViewById(R.id.text2);
            tvTitle.setText(model.getCategory_name());
            id.setText(model.getCategory_id()+"");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();

            if (originalList != null && constraint != null) {
                for (int i = 0; i < originalList.size(); i++) {
                    if (originalList.get(i).getCategory_name().toLowerCase().contains(constraint.toString().toLowerCase().trim())) {
                        suggestions.add(originalList.get(i));
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}