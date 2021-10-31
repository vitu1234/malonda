package com.example.malonda.supplier.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.malonda.R;
import com.example.malonda.buyer.activities.BuyerDashboardActivity;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.supplier.activities.AddEditBusinessInfoActivity;
import com.example.malonda.supplier.activities.MyProductsActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SupplierDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SupplierDashboardFragment extends Fragment {
    TextView textViewProductsCount, textViewOrdersCount, textViewSalesCount;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout relativeLayoutMyProducts;
    ImageView imageViewLogout;

    AppDatabase room_db;
    SharedPrefManager sharedPrefManager;

    int user_id = -1;


    public SupplierDashboardFragment() {
        // Required empty public constructor
    }


    public static SupplierDashboardFragment newInstance(String param1, String param2) {
        SupplierDashboardFragment fragment = new SupplierDashboardFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_supplier_dashboard, container, false);

        textViewProductsCount = view.findViewById(R.id.dashProductsCount);
        textViewOrdersCount = view.findViewById(R.id.dashOrdersCount);
        textViewSalesCount = view.findViewById(R.id.dashSalesCount);
        swipeRefreshLayout = view.findViewById(R.id.supplierDashSwiperefresh);
        relativeLayoutMyProducts = view.findViewById(R.id.relativeLayoutMyProducts);
        imageViewLogout = view.findViewById(R.id.dashLogout);

        room_db = AppDatabase.getDbInstance(getContext());
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        user_id = sharedPrefManager.getUser().getUser_id();

        setViewValues();
        setOnclickListeners();
        checkBusinessInfo();

        return view;
    }

    private void checkBusinessInfo() {
        if (room_db.businessInfoDao().getSingleBusinessInfoCountByBusinessInfoID(user_id) == 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage(R.string.pontetial_customers_txt);
            builder1.setTitle(R.string.warning_txt);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    R.string.add_txt,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(getContext(), AddEditBusinessInfoActivity.class));
                        }
                    });

            builder1.setNegativeButton(
                    R.string.close_txt,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder1.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.red));
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.purple_700));
        }
    }

    private void setOnclickListeners() {
        relativeLayoutMyProducts.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), MyProductsActivity.class);
            //add shared animation

            startActivity(intent);

        });
    }

    private void setViewValues() {
        int user_products = room_db.productDao().getUserProductCount(user_id);
        textViewProductsCount.setText(user_products + " Products");

        imageViewLogout.setOnClickListener(view -> {
            sharedPrefManager.logoutUser();
            startActivity(new Intent(this.getContext(), BuyerDashboardActivity.class));
            getActivity().finish();
        });
    }
}