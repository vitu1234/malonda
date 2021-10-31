package com.example.malonda.buyer.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.TerminalAdapter;
import com.example.malonda.buyer.activities.SalesCheckoutActivity;
import com.example.malonda.common.LoginActivity;
import com.example.malonda.models.POS;
import com.example.malonda.models.Product;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BuyerDashboardFragment extends Fragment {
    RecyclerView recyclerViewPosTerminal;
    TerminalAdapter adapter;
    AppDatabase roomdb;
    List<Product> productList;
    List<POS> posList;
    public TextView textViewWarning, textViewTotalItems;
    TextInputLayout textInputLayoutSearch;
    MyProgressDialog progressDialog;
    ImageView imageViewBarcode, sortListImageView, imageViewLogout;
    public Button buttonDiscard;
    ImageCarousel carousel;
    List<CarouselItem> list = new ArrayList<>();

    double total = 0;

    public BuyerDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuyerDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuyerDashboardFragment newInstance(String param1, String param2) {
        BuyerDashboardFragment fragment = new BuyerDashboardFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_buyer_dashboard, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        recyclerViewPosTerminal = view.findViewById(R.id.productsListPos);
        textViewWarning = view.findViewById(R.id.posWarning);
        textViewTotalItems = view.findViewById(R.id.itemsTotal);
        textInputLayoutSearch = view.findViewById(R.id.posSearchProduct);
        buttonDiscard = view.findViewById(R.id.discardBtn);
        sortListImageView = view.findViewById(R.id.sortListImageview);
        imageViewLogout = view.findViewById(R.id.buyerMenu);
        carousel = view.findViewById(R.id.carousel);

        roomdb = AppDatabase.getDbInstance(this.getContext());
        productList = roomdb.productDao().getAllProductsAvailable();
        progressDialog = new MyProgressDialog(this.getContext());


        setViews();


        setRecyclerView();
        return view;
    }

    private void setViews() {
        textInputLayoutSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        buttonDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomdb.posDao().deleteAllPos();
                buttonDiscard.setVisibility(View.GONE);
                textViewTotalItems.setText("No Items: K0.00");
            }
        });
        imageViewLogout.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(getContext(), view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.overflow_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menu_login) {
                    startActivity(new Intent(view.getContext(), LoginActivity.class));
                } else if (menuItem.getItemId() == R.id.menu_nearby) {

                } else if (menuItem.getItemId() == R.id.menu_trending) {

                } else if (menuItem.getItemId() == R.id.menu_suggestions) {

                } else if (menuItem.getItemId() == R.id.menu_about) {

                }
                return true;
            });

            popup.show();
        });


        posList = roomdb.posDao().getAllPos();
        for (int i = 0; i < posList.size(); i++) {
            total += posList.get(i).getTotal();
        }

        textViewTotalItems.setOnClickListener(v -> {
            posList.clear();
            posList = roomdb.posDao().getAllPos();
            for (int i = 0; i < posList.size(); i++) {
                total += posList.get(i).getTotal();
            }
            if (posList.size() > 0) {
                textViewTotalItems.setText("Items: K" + total);
                checkOutReceipt();
            } else {
                progressDialog.showErrorToast("No items!");
            }
        });

        sortListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortList();
            }
        });

        list.add(
                new CarouselItem(
                        "https://images.unsplash.com/photo-1532581291347-9c39cf10a73c?w=1080",
                        "Photo by Aaron Wu on Unsplash"
                )
        );
        list.add(
                new CarouselItem(
                        "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080",
                        "Photo by Johannes Plenio on Unsplash"
                )
        );

        carousel.addData(list);

    }

    private void checkOutReceipt() {
        total = 0;
        Intent intent = new Intent(this.getContext(), SalesCheckoutActivity.class);
        getContext().startActivity(intent);
//        Toast.makeText(getContext(), "checkout", Toast.LENGTH_SHORT).show();
    }

    private void setRecyclerView() {
        if (productList.size() > 0) {
            adapter = new TerminalAdapter(this, this.getContext(), productList);

            // setting grid layout manager to implement grid view.
            // in this method '2' represents number of columns to be displayed in grid view.
            GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);

            // at last set adapter to recycler view.
            recyclerViewPosTerminal.setLayoutManager(layoutManager);
            recyclerViewPosTerminal.setAdapter(adapter);
        } else {
            recyclerViewPosTerminal.setVisibility(View.GONE);
            textViewWarning.setText("No items availabe now!");
            textViewWarning.setVisibility(View.VISIBLE);
        }

    }

    //filtering the list
    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : roomdb.productDao().getAllProductsAvailable()) {
            if (product.getProduct_name().toLowerCase().contains(text.toLowerCase()) || product.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
                textViewWarning.setVisibility(View.INVISIBLE);
            } else {
                textViewWarning.setVisibility(View.VISIBLE);
                textViewWarning.setText("Change query for more filter results!");
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    public void sortList() {

        adapter.sortList(sortByName(productList));
    }

    public List<Product> sortByName(List<Product> productList) {

        Collections.sort(productList, (product1, product2) -> product1.getProduct_name().compareTo(product2.getProduct_name()));
        return productList;
    }
}