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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.malonda.R;
import com.example.malonda.buyer.activities.BuyerDashboardActivity;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.supplier.activities.AddEditBusinessInfoActivity;
import com.example.malonda.supplier.activities.BusSalesActivity;
import com.example.malonda.supplier.activities.MyProductsActivity;
import com.example.malonda.supplier.activities.POSActivity;
import com.example.malonda.supplier.activities.ReportsActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SupplierDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SupplierDashboardFragment extends Fragment {
    TextView textViewProductsCount, textViewOrdersCount, textViewSold;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout relativeLayoutMyProducts, relativeLayoutPOS, relativeLayoutSales, relativeLayoutReports;
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
        swipeRefreshLayout = view.findViewById(R.id.supplierDashSwiperefresh);
        relativeLayoutMyProducts = view.findViewById(R.id.relativeLayoutMyProducts);
        imageViewLogout = view.findViewById(R.id.dashLogout);
        relativeLayoutPOS = view.findViewById(R.id.relativeLayoutPOS);
        relativeLayoutSales = view.findViewById(R.id.relativeLayoutSales);
        textViewSold = view.findViewById(R.id.dashSoldCount);
        relativeLayoutReports = view.findViewById(R.id.relativeLayoutreports);

        room_db = AppDatabase.getDbInstance(getContext());
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        user_id = sharedPrefManager.getUser().getUser_id();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshViewData();
            }
        });

        setViewValues();
        setOnclickListeners();
        checkBusinessInfo();

        return view;
    }

    private void refreshViewData() {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), "Will refresh view here", Toast.LENGTH_SHORT).show();
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
            getActivity().overridePendingTransition(0, 0);
        });
        relativeLayoutPOS.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), POSActivity.class);
            //add shared animation

            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);

        });

        relativeLayoutSales.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), BusSalesActivity.class);
            //add shared animation

            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        });

        relativeLayoutReports.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ReportsActivity.class);
            //add shared animation

            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        });

    }

    private void setViewValues() {
        int user_products = room_db.productDao().getUserProductCount(user_id);
        int user_available_products = room_db.productDao().getUserAvailableProductCount(user_id);
        textViewProductsCount.setText(user_products + " Products");
        textViewOrdersCount.setText(user_available_products + " Products");
        textViewSold.setText(room_db.saleDao().getSingleSaleBusCount(user_id) + " Sales");

        imageViewLogout.setOnClickListener(view -> {
            sharedPrefManager.logoutUser();
            startActivity(new Intent(this.getContext(), BuyerDashboardActivity.class));
            getActivity().finish();
        });
    }
}