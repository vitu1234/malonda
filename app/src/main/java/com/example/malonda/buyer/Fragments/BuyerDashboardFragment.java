package com.example.malonda.buyer.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.CustomSpinnerAdapter;
import com.example.malonda.adapters.TerminalAdapter;
import com.example.malonda.buyer.activities.CategoryProductsActivity;
import com.example.malonda.buyer.activities.NearByBusinessesActivity;
import com.example.malonda.buyer.activities.SalesCheckoutActivity;
import com.example.malonda.common.LoginActivity;
import com.example.malonda.models.Category;
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


public class BuyerDashboardFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    RecyclerView recyclerViewPosTerminal;
    TerminalAdapter adapter;
    AppDatabase roomdb;
    List<Product> productList;
    List<Category> categoryList;
    List<POS> posList;
    public TextView textViewWarning, textViewTotalItems;
    TextInputLayout textInputLayoutSearch;
    MyProgressDialog progressDialog;
    ImageView imageViewBarcode, sortListImageView, imageViewLogout;
    public Button buttonDiscard;
    ImageCarousel carousel;
    List<CarouselItem> list = new ArrayList<>();
    AlertDialog alertDialog;
    double total = 0;
    TextView textViewCategories;

    int show_category = -1;

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
        textViewCategories = view.findViewById(R.id.showCategories);


        roomdb = AppDatabase.getDbInstance(this.getContext());
        productList = roomdb.productDao().getAllProductsAvailable();
        categoryList = roomdb.categoryDao().getAllCategorys();
        progressDialog = new MyProgressDialog(this.getContext());


        setViews();
        setRecyclerView();
        return view;
    }

    private void setSpinner() {
        String[] category_names = new String[categoryList.size()];
        int[] category_ids = new  int[categoryList.size()];
        for (int i =0;i<categoryList.size();i++){
            category_ids[i]=categoryList.get(i).getCategory_id();
            category_names[i]=categoryList.get(i).getCategory_name();
        }

        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.prompt_spinner_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        Spinner spinner = promptsView.findViewById(R.id.categorySpinner);
        CustomSpinnerAdapter customAdapter=new CustomSpinnerAdapter(getContext(),category_ids,category_names);
        spinner.setAdapter(customAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("e",category_names[i]+" ID: "+category_ids[i]);
                show_category = category_ids[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("VIEW",
                        (dialog, id) -> {
                            //save here
                            Boolean wantToCloseDialog = false;
                            //Do stuff, possibly set wantToCloseDialog to true then...
                            if (wantToCloseDialog) {
                                alertDialog.dismiss();
                            } else {

                                if (show_category != -1 ){
                                    Intent intent = new Intent(getContext(), CategoryProductsActivity.class);
                                    intent.putExtra("category_id",show_category);
                                    intent.putExtra("bus_user_id",-1);
                                }

                            }
                        })
                .setNegativeButton("Close",
                        (dialog, id) -> dialog.cancel());

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple_700));


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
                    startActivity(new Intent(getContext(), NearByBusinessesActivity.class));
                    getActivity().overridePendingTransition(0, 0);
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

//        sortListImageView.setOnClickListener(v -> sortList());
        sortListImageView.setOnClickListener(view -> showSortDialog());
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

        textViewCategories.setOnClickListener(view -> setSpinner());

    }

    private void showSortDialog() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.sort_prompt_box, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        RadioGroup radioGroupOptions = promptsView.findViewById(R.id.search_filter);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Go",
                        (dialog, id) -> {
                            //save here
                            Boolean wantToCloseDialog = false;
                            //Do stuff, possibly set wantToCloseDialog to true then...
                            if (wantToCloseDialog) {
                                alertDialog.dismiss();
                            } else {


                                radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
                                    Log.e("dld", String.valueOf(checkedId) + " CHECKED");

                                    if (checkedId == R.id.search_ascending) {
                                        filterRecyclerView("price_asc");
                                    } else if (checkedId == R.id.search_descending) {
                                        filterRecyclerView("price_desc");
                                    } else if (checkedId == R.id.search_az) {
                                        filterRecyclerView("az_sort");
                                    } else if (checkedId == R.id.search_za) {
                                        filterRecyclerView("za_sort");
                                    }
                                });

                                if (radioGroupOptions.getCheckedRadioButtonId() == R.id.search_ascending) {
                                    filterRecyclerView("price_asc");
                                } else if (radioGroupOptions.getCheckedRadioButtonId() == R.id.search_descending) {
                                    filterRecyclerView("price_desc");
                                } else if (radioGroupOptions.getCheckedRadioButtonId() == R.id.search_az) {
                                    filterRecyclerView("az_sort");
                                } else if (radioGroupOptions.getCheckedRadioButtonId() == R.id.search_za) {
                                    filterRecyclerView("za_sort");
                                }


                            }
                        })
                .setNegativeButton("Close",
                        (dialog, id) -> dialog.cancel());

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple_700));

    }


    private void checkOutReceipt() {
        total = 0;
        Intent intent = new Intent(this.getContext(), SalesCheckoutActivity.class);
        getContext().startActivity(intent);
//        Toast.makeText(getContext(), "checkout", Toast.LENGTH_SHORT).show();
    }

    private void setRecyclerView() {

        if (productList.size() > 0) {
            adapter = new TerminalAdapter(this.getContext(), productList);

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
        if (adapter != null) {
            adapter.sortList(sortByName(productList));
        }
    }

    private void filterRecyclerView(String sort_qry) {
        List<Product> filteredList = new ArrayList<>();
        Log.e("dsfafa", sort_qry);
        if (sort_qry.equals("price_asc")) {
            for (Product product : roomdb.productDao().getAllProductsAvailablePriceAsc()) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("price_desc")) {
            for (Product product : roomdb.productDao().getAllProductsAvailablePriceDesc()) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("az_sort")) {
            for (Product product : roomdb.productDao().getAllProductsAvailable()) {
                filteredList.add(product);
            }
        } else if (sort_qry.equals("za_sort")) {
            for (Product product : roomdb.productDao().getAllProductsAvailableNameDesc()) {
                filteredList.add(product);
            }
        }

        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }


    public List<Product> sortByName(List<Product> productList) {

        Collections.sort(productList, (product1, product2) -> product1.getProduct_name().compareTo(product2.getProduct_name()));
        return productList;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}